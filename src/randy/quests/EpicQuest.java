package randy.quests;

import java.util.ArrayList;
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

import randy.epicquest.EpicAnnouncer;
import randy.epicquest.EpicPlayer;
import randy.epicquest.EpicSystem;
import randy.epicquest.EpicMain;
import randy.quests.EpicQuestTask.TaskTypes;

public class EpicQuest {
	
	private EpicPlayer epicPlayer;
	
	private String questTag;
	private List<EpicQuestTask> questTasks = new ArrayList<EpicQuestTask>();
	
	public EpicQuest(EpicPlayer epicPlayer, String questTag){
		
		this.epicPlayer = epicPlayer;
		this.questTag = questTag;
		
		int totalTaskAmount = EpicQuestDatabase.getTaskTotal(questTag);
		for(int i = 0; i < totalTaskAmount; i ++){	
			TaskTypes taskType = EpicQuestDatabase.getTaskType(questTag, i);
			
			EpicQuestTask task = new EpicQuestTask(
					taskType,
					EpicQuestDatabase.getTaskID(questTag, i), 
					EpicQuestDatabase.getTaskAmount(questTag, i),
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
	public String getQuestTag(){ return questTag; }
	public String getQuestName(){ return EpicQuestDatabase.getQuestName(questTag); }
	public String getQuestStart(){ return EpicQuestDatabase.getQuestStartInfo(questTag); }
	public String getQuestEnd(){ return EpicQuestDatabase.getQuestEndInfo(questTag); }
	public List<String> getQuestWorlds(){ return EpicQuestDatabase.getQuestWorlds(questTag); }
	public int getQuestResetTime(){ return EpicQuestDatabase.getQuestResetTime(questTag); }
	public int getQuestRewardMoney(){ return EpicQuestDatabase.getRewardMoney(questTag); }
	public List<ItemStack> getQuestRewardItem() { return EpicQuestDatabase.getRewardItems(questTag); }
	public String getQuestRewardPermission() { return EpicQuestDatabase.getRewardRank(questTag); }
	public List<String> getQuestRewardCommand() { return EpicQuestDatabase.getRewardCommand(questTag); }
	public int getQuestRewardHeroesExp() { return EpicQuestDatabase.getRewardHeroesExp(questTag); }
	public Boolean getQuestAutoComplete() { return EpicQuestDatabase.getQuestAutoComplete(questTag); }
	@SuppressWarnings("deprecation")
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
		Economy economy = EpicMain.economy;
		int money = getQuestRewardMoney();
		if(EpicSystem.enabledMoneyRewards()){
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
		Permission permission = EpicMain.permission;
		String rank = getQuestRewardPermission();
		if(permission != null && permission.isEnabled()){
			if(Arrays.asList(permission.getGroups()).contains(rank)){
				permission.playerAddGroup(player, rank);
				player.sendMessage(ChatColor.GREEN + "You got promoted to " + rank + ".");
			}
		}
		
		//Execute command
		List<String> commands = getQuestRewardCommand();
		if(!commands.isEmpty()){
			for(String command : commands){
				if(command.contains("<player>"))
					command = command.replaceAll("<player>", player.getName());
		
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
			}
		}
		
		//Heroes EXP
		int heroesExp = getQuestRewardHeroesExp();
		if(heroesExp != 0 && EpicSystem.useHeroes()){
			Hero playerHero = EpicMain.heroes.getCharacterManager().getHero(player);
			playerHero.addExp(heroesExp, playerHero.getHeroClass(), player.getLocation());
			player.sendMessage(ChatColor.GREEN + "You gained " + heroesExp + " experience points for " + playerHero.getHeroClass().getName() + ".");
		}
		
		//Send ending text
		player.sendMessage(""+ChatColor.GRAY + ChatColor.ITALIC + getQuestEnd());
		
		EpicAnnouncer.SendQuestCompletedText(epicPlayer, this.questTag);
		
		if(!epicPlayer.getQuestsCompleted().contains(questTag)) epicPlayer.getQuestsCompleted().add(questTag);
		epicPlayer.getQuestList().remove(this);
	}
	
	/*
	 * 
	 * Player progress
	 * 
	 */
	public boolean isCompleted(){ for(int i = 0; i < questTasks.size(); i++){ if( !questTasks.get(i).IsComplete() ){ return false; } } return true; }
	
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
					task.setTaskID(EpicQuestDatabase.getTaskID(quest.getQuestTag(), i));
					task.setTaskGoal(EpicQuestDatabase.getTaskAmount(quest.getQuestTag(), i));
				}
			}
		}
	}
}
