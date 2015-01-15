package main.java.randy.quests;

import java.util.Arrays;
import java.util.List;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.herocraftonline.heroes.characters.Hero;

import main.java.randy.engine.EpicPlayer;
import main.java.randy.engine.EpicSystem;
import main.java.randy.epicquest.EpicMain;

public class EpicQuestReward {

	public static enum RewardTypes{
		MONEY,
		ITEM,
		RANK,
		COMMAND,
		HEROES_EXP
	}

	public RewardTypes type;
	private Object reward;

	public EpicQuestReward(RewardTypes type, Object reward){
		this.type = type;
		this.reward = reward;
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	public void GiveRewards(EpicPlayer ePlayer){
		if(isEmpty()) return;
		Player player = ePlayer.getPlayer();
		String playerName = ePlayer.getPlayerName();
		switch(type){
		case COMMAND:
			List<String> commands = (List<String>)reward;
			for(String command : commands){
				if(command.contains("<player>"))
					command = command.replaceAll("<player>", player.getName());

				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
			}
			break;
		case HEROES_EXP:
			int heroesExp = (Integer)reward;
			if(EpicSystem.useHeroes()){
				Hero playerHero = EpicMain.heroes.getCharacterManager().getHero(player);
				playerHero.addExp(heroesExp, playerHero.getHeroClass(), player.getLocation());
				player.sendMessage(ChatColor.GREEN + "You gained " + heroesExp + " experience points for " + playerHero.getHeroClass().getName() + ".");
			}
			break;
		case ITEM:
			List<ItemStack> itemList = (List<ItemStack>)reward;
			PlayerInventory inventory = player.getInventory();
			for(int i = 0; i < itemList.size(); i++){
				ItemStack itemStack = itemList.get(i);
				inventory.addItem(itemStack);
				player.sendMessage(ChatColor.GREEN + "You got " + itemStack.getAmount() + " " + itemStack.getType().toString().toLowerCase().replace("_", " ") + ".");
			}
			break;
		case MONEY:
			int money = (Integer)reward;
			if(EpicSystem.enabledMoneyRewards()){
				Economy economy = EpicMain.economy;
				if(!economy.hasAccount(playerName)){ economy.createPlayerAccount(playerName); }

				economy.depositPlayer(playerName, money);
				String moneyname = economy.currencyNamePlural();
				if(money == 1){ moneyname = economy.currencyNameSingular(); }

				ePlayer.playerStatistics.AddMoneyEarned(money);
				player.sendMessage(ChatColor.GREEN + "You received " + money + " " + moneyname + ".");
			}
			break;
		case RANK:
			String rank = (String)reward;
			if(EpicSystem.usePermissions()){
				Permission permission = EpicMain.permission;
				if(Arrays.asList(permission.getGroups()).contains(rank)){
					permission.playerAddGroup(player, rank);
					player.sendMessage(ChatColor.GREEN + "You got promoted to " + rank + ".");
				}
			}
			break;
		default:
			break;

		}
	}

	@SuppressWarnings("unchecked")
	public boolean isEmpty(){
		switch(type){
		case COMMAND:
			List<String> commands = (List<String>)reward;
			if(commands.isEmpty()) return true;
			return false;
		case HEROES_EXP:
			int heroesExp = (Integer)reward;
			if(heroesExp <= 0) return true;
			return false;
		case ITEM:
			List<ItemStack> itemList = (List<ItemStack>)reward;
			if(itemList.isEmpty()) return true;
			return false;
		case MONEY:
			int money = (Integer)reward;
			if(money <= 0) return true;
			return false;
		case RANK:
			String rank = (String)reward;
			if(rank != null && rank != "") return false;
			return true;
		default:
			return true;
		}
	}
}
