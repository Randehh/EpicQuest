package randy.quests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.herocraftonline.heroes.characters.Hero;

import randy.epicquest.EpicPlayer;
import randy.epicquest.EpicSystem;
import randy.epicquest.main;
import randy.quests.EpicQuestTask.TaskTypes;

public class EpicQuest {
	
	private EpicPlayer epicPlayer;
	
	private int questNumber;
	private List<EpicQuestTask> questTasks = new ArrayList<EpicQuestTask>();
	
	public EpicQuest(EpicPlayer epicPlayer, int questNo){
		
		this.epicPlayer = epicPlayer;
		this.questNumber = questNo;
		
		int totalTaskAmount = EpicQuestDatabase.getTaskTotal(questNo);
		for(int i = 0; i < totalTaskAmount; i ++){	
			TaskTypes taskType = EpicQuestDatabase.getTaskType(questNo, i);
			EpicQuestTask task = new EpicQuestTask(
					taskType,
					EpicQuestDatabase.getTaskID(questNo, i), 
					EpicQuestDatabase.getTaskAmount(questNo, i),
					this);
			questTasks.add(task);
			epicPlayer.addTask(task);
		}
	}
	
	/*
	 * 
	 * Standard quest info applicable to every quest
	 * 
	 */
	public EpicPlayer getEpicPlayer(){ return epicPlayer; }
	public List<EpicQuestTask> getTasks(){ return questTasks; }
	public int getQuestNo(){ return questNumber; }
	public String getQuestName(){ return EpicQuestDatabase.getQuestName(questNumber); }
	public String getQuestStart(){ return EpicQuestDatabase.getQuestStartInfo(questNumber); }
	public String getQuestEnd(){ return EpicQuestDatabase.getQuestEndInfo(questNumber); }
	public List<String> getQuestWorlds(){ return EpicQuestDatabase.getQuestWorlds(questNumber); }
	public int getQuestResetTime(){ return EpicQuestDatabase.getQuestResetTime(questNumber); }
	public int getQuestRewardMoney(){ return EpicQuestDatabase.getRewardMoney(questNumber); }
	public List<ItemStack> getQuestRewardItem() {
		List<ItemStack> itemList = new ArrayList<ItemStack>();

		for(int i = 0; i < EpicQuestDatabase.getRewardID(questNumber).size(); i++){
			String itemID = EpicQuestDatabase.getRewardID(questNumber).get(i);
			int itemAmount = EpicQuestDatabase.getRewardAmount(questNumber).get(i);
			if( itemID != null &&
					itemAmount > 0){
				itemList.add(new ItemStack(Material.matchMaterial(itemID), itemAmount));
			}
		}
		return itemList;
	}
	public String getQuestRewardPermission() { return EpicQuestDatabase.getRewardRank(questNumber); }
	public String getQuestRewardCommand() { return EpicQuestDatabase.getRewardCommand(questNumber); }
	public int getQuestRewardHeroesExp() { return EpicQuestDatabase.getRewardHeroesExp(questNumber); }
	public void completeQuest(){
		
		//Basics
		Player player = epicPlayer.getPlayer();
		String playerName = player.getName();
		
		//Generate item reward
		PlayerInventory inventory = player.getInventory();
		List<ItemStack> itemList = getQuestRewardItem();
		if(!itemList.isEmpty()){
			for(int i = 0; i < itemList.size(); i++){
				ItemStack itemStack = itemList.get(i);
				inventory.addItem(itemStack);
				player.sendMessage(ChatColor.GREEN + "You got " + itemStack.getAmount() + " " + itemStack.getType().toString().toLowerCase().replace("_", " ") + ".");
			}
		}
			
		//Generate money reward
		Economy economy = main.economy;
		int money = getQuestRewardMoney();
		if(economy != null && economy.isEnabled()){
			if(!economy.hasAccount(playerName)){ economy.createPlayerAccount(playerName); }
			
			//Add money if there's money to add
			if(money >= 1){
				economy.depositPlayer(playerName, money);
				String moneyname = economy.currencyNamePlural();
				if(money == 1){ moneyname = economy.currencyNameSingular(); }
				
				player.sendMessage(ChatColor.GREEN + "You received " + money + " " + moneyname + ".");
				epicPlayer.modifyStatMoneyEarned(money);
			}
		}
		
		//Generate permission reward
		Permission permission = main.permission;
		String rank = getQuestRewardPermission();
		if(permission != null && permission.isEnabled()){
			if(Arrays.asList(permission.getGroups()).contains(rank)){
				permission.playerAddGroup(player, rank);
				player.sendMessage(ChatColor.GREEN + "You got promoted to " + rank + ".");
			}
		}
		
		//Execute command
		String command = getQuestRewardCommand();
		if(command != null && !command.equals("no command")){
			if(command.contains("<player>"))
				command = command.replaceAll("<player>", player.getName());
		
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
		}
		
		//Heroes EXP
		int heroesExp = getQuestRewardHeroesExp();
		if(heroesExp != 0 && EpicSystem.useHeroes()){
			Hero playerHero = main.heroes.getCharacterManager().getHero(player);
			playerHero.addExp(heroesExp, playerHero.getHeroClass(), player.getLocation());
			player.sendMessage(ChatColor.GREEN + "You gained " + heroesExp + " experience points for " + playerHero.getHeroClass().getName() + ".");
		}
		
		//Send ending text
		player.sendMessage(""+ChatColor.GRAY + ChatColor.ITALIC + getQuestEnd());
		
		if(!epicPlayer.getQuestsCompleted().contains(questNumber))
			epicPlayer.getQuestsCompleted().add(questNumber);
		epicPlayer.getQuestList().remove(this);
	}
	
	/*
	 * 
	 * Player progress
	 * 
	 */
	public boolean getPlayerQuestCompleted(){ for(int i = 0; i < questTasks.size(); i++){ if( !questTasks.get(i).IsComplete() ){ return false; } } return true; }
	
	/*
	 * 
	 * Calculating methods
	 * 
	 */
	public int getTaskAmount(){ return questTasks.size(); }
	
	/*
	 * 
	 * Misc
	 * 
	 */
	public static void ResetQuestTaskInfo(){
		for(EpicPlayer player : EpicSystem.getPlayerList()){
			for(EpicQuest quest : player.getQuestList()){
				for(int i = 0; i < quest.getTaskAmount(); i++){
					EpicQuestTask task = quest.getTasks().get(i);
					task.setTaskID(EpicQuestDatabase.getTaskID(quest.getQuestNo(), i));
					task.setTaskGoal(EpicQuestDatabase.getTaskAmount(quest.getQuestNo(), i));
				}
			}
		}
	}
}
