package randy.epicquest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class EpicQuest {
	
	private EpicPlayer epicPlayer;
	
	private String questName;
	private String questStart;
	private String questEnd;
	private List<String> questWorlds;
	private int questResetTime;
	private int questRewardMoney;
	private int questRewardItemID;
	private int questRewardItemAmount;
	private String questRewardPermission;
	private List<String> taskType;
	private List<String> taskID;
	private List<Integer> taskAmount;
	private List<Integer> playerProgress;
	private int questNumber;
	
	public EpicQuest(EpicPlayer epicPlayer, int questNo){
		
		this.epicPlayer = epicPlayer;
		
		//Retrieve quest info from the database
		questName = EpicQuestDatabase.getQuestName(questNo);
		questStart = EpicQuestDatabase.getQuestStartInfo(questNo);
		questEnd = EpicQuestDatabase.getQuestEndInfo(questNo);
		questWorlds = EpicQuestDatabase.getQuestWorlds(questNo);
		questResetTime = EpicQuestDatabase.getQuestResetTime(questNo);
		questRewardMoney = EpicQuestDatabase.getRewardMoney(questNo);
		questRewardItemID = EpicQuestDatabase.getRewardID(questNo);
		questRewardItemAmount = EpicQuestDatabase.getRewardAmount(questNo);
		questRewardPermission = EpicQuestDatabase.getRewardRank(questNo);
		playerProgress = new ArrayList<Integer>();
		this.questNumber = questNo;
		
		List<String> tempTaskType =  new ArrayList<String>();
		List<String> tempTaskID = new ArrayList<String>();
		List<Integer> tempTaskAmount =  new ArrayList<Integer>();
		int totalTaskAmount = EpicQuestDatabase.getTaskTotal(questNo);
		for(int i = 0; i < totalTaskAmount; i ++){
			tempTaskType.add(EpicQuestDatabase.getTaskType(questNo, i));
			tempTaskID.add(EpicQuestDatabase.getTaskID(questNo, i));
			tempTaskAmount.add(EpicQuestDatabase.getTaskAmount(questNo, i));
			playerProgress.add(0);
		}
		taskType = tempTaskType;
		taskID = tempTaskID;
		taskAmount = tempTaskAmount;
	}
	
	/*
	 * 
	 * Standard quest info applicable to every quest
	 * 
	 */
	public EpicPlayer getEpicPlayer(){ return epicPlayer; }
	public int getQuestNo(){ return questNumber; }
	public String getQuestName(){ return questName; }
	public String getQuestStart(){ return questStart; }
	public String getQuestEnd(){ return questEnd; }
	public List<String> getQuestWorlds(){ return questWorlds; }
	public int getQuestResetTime(){ return questResetTime; }
	public int getQuestRewardMoney(){ return questRewardMoney; }
	public ItemStack getQuestRewardItem() { return new ItemStack(questRewardItemID, questRewardItemAmount); }
	public String getQuestRewardPermission() { return questRewardPermission; }
	public void completeQuest(){
		
		//Basics
		Player player = epicPlayer.getPlayer();
		String playerName = player.getName();
		
		//Generate item reward
		PlayerInventory inventory = player.getInventory();
		ItemStack itemStack = getQuestRewardItem();
		if(itemStack != null){
			inventory.addItem(itemStack);
			player.sendMessage(ChatColor.GREEN + "You got " + itemStack.getAmount() + " " + Material.getMaterial(itemStack.getTypeId()).toString().toLowerCase().replace("_", " ") + ".");
		}
			
		//Generate money reward
		Economy economy = main.economy;
		int money = getQuestRewardMoney();
		if(economy.isEnabled()){
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
		if(permission.isEnabled()){
			if(Arrays.asList(permission.getGroups()).contains(rank)){
				permission.playerAddGroup(player, rank);
				player.sendMessage(ChatColor.GREEN + "You got promoted to " + rank + ".");
			}
		}
		
		epicPlayer.getQuestList().remove(this);
	}
	
	/*
	 * 
	 * Task info
	 * 
	 */
	public String getTaskType(int taskNo) { String temp = taskType.get(taskNo); if(temp != null){ return temp; } else return null; }
	public String getTaskID(int taskNo) { String temp = taskID.get(taskNo); if(temp != null){ return temp; } else return null; }
	public int getTaskAmount(int taskNo) { int temp = taskAmount.get(taskNo); if(temp > 0){ return temp; } else return 0; }
	
	/*
	 * 
	 * Player progress
	 * 
	 */
	public int getPlayerTaskProgress(int taskNo) { return playerProgress.get(taskNo); }
	public boolean getPlayerTaskCompleted(int taskNo) { if(getPlayerTaskProgress(taskNo) >= taskAmount.get(taskNo)){ return true; } else { return false; } }
	public boolean getPlayerQuestCompleted(){ for(int i = 0; i < getTaskAmount(); i++){ if( !getPlayerTaskCompleted(i) ){ return false; } } return true; }
	public String getPlayerTaskProgressText(int task) {
		//Get information to send the progress
		// ie: Kill 2/10 wolves
		// ie2: Killed 2 wolves
		String questtype = taskType.get(task);
		int taskprogress = getPlayerTaskProgress(task);
		int taskmax = getTaskAmount(task);
		String taskID = getTaskID(task);
		String message = null;
		
		//Convert stuff to task specifics
		//Quest types: Collect, Kill, Killplayer, Killanyplayer, Destroy, Place, Levelup, Enchant, Tame
		if(questtype.equalsIgnoreCase("killplayer")){ questtype = "Kill"; }
		if(questtype.equalsIgnoreCase("killanyplayer")){ questtype = "Kill"; }
		if(questtype.equalsIgnoreCase("levelup")){ questtype = "Level up"; }
		
		if(questtype.equalsIgnoreCase("level up")){ taskID = "times"; }
		if(questtype.equalsIgnoreCase("collect")||
				questtype.equalsIgnoreCase("destroy")||
				questtype.equalsIgnoreCase("place")||
				questtype.equalsIgnoreCase("enchant")){ 
			taskID = Material.getMaterial(Integer.parseInt(taskID)).name(); 
			taskID = taskID.toLowerCase().replace("_", " ");
		}
		
		//Capitalize first letter (questtype)
		questtype = WordUtils.capitalize(questtype);
		
		
		//Change certain types around for grammatical purposes
		if(getPlayerTaskCompleted(task)){
			if(questtype.equalsIgnoreCase("collect")){ questtype = "Collected"; }
			if(questtype.equalsIgnoreCase("kill")){ questtype = "Killed"; }
			if(questtype.equalsIgnoreCase("killplayer")){ questtype = "Killed"; }
			if(questtype.equalsIgnoreCase("killanyplayer")){ questtype = "Killed"; }
			if(questtype.equalsIgnoreCase("destroy")){ questtype = "Destroyed"; }
			if(questtype.equalsIgnoreCase("place")){ questtype = "Placed"; }
			if(questtype.equalsIgnoreCase("levelup")){ questtype = "Leveled up"; }
			if(questtype.equalsIgnoreCase("enchant")){ questtype = "Enchant"; }
			if(questtype.equalsIgnoreCase("tame")){ questtype = "Tamed"; }
			
			//Create the message if the task is finished
			message = ChatColor.GREEN + questtype + " " + taskmax + " " + taskID + ".";
		}else{
			message = ChatColor.RED + questtype + " " + taskprogress + "/" + taskmax + " " + taskID + ".";
		}
		
		//Return message
		return message;
	}
	
	/*
	 * 
	 * Calculating methods
	 * 
	 */
	public int getTaskAmount(){ return taskID.size(); }
	
	/*
	 * 
	 * Quest editing methods
	 * 
	 */
	public void modifyTaskProgress(int taskNo, int amount, boolean printText){ 
		Player player = epicPlayer.getPlayer();
		
		int newProgress = playerProgress.get(taskNo) + amount;
		if(newProgress > getTaskAmount(taskNo)){
			newProgress = getTaskAmount(taskNo);
		}
		playerProgress.set(taskNo, newProgress);
		
		if(printText)player.sendMessage(this.getPlayerTaskProgressText(taskNo));
		
		//Add stats if possible and send player a message that task is complete
		if(getPlayerTaskCompleted(taskNo)){
			epicPlayer.modifyStatTaskCompleted(1);
			
			if(printText)player.sendMessage(ChatColor.GREEN + "Task completed!");
			
			//If quest is complete, send a message
			if(getPlayerQuestCompleted()){
				if(printText)player.sendMessage(ChatColor.GREEN + "Quest completed: " + getQuestName() + "!");
			}
		}
	
	}
	
}
