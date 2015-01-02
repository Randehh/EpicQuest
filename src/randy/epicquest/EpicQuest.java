package randy.epicquest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class EpicQuest {
	
	private EpicPlayer epicPlayer;
	
	private List<String> taskType;
	private List<String> taskID;
	private List<Integer> taskAmount;
	private List<Integer> playerProgress;
	private int questNumber;
	
	public EpicQuest(EpicPlayer epicPlayer, int questNo){
		
		this.epicPlayer = epicPlayer;
		this.questNumber = questNo;
		
		playerProgress = new ArrayList<Integer>();
		
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
		
		String command = getQuestRewardCommand();
		if(command != null && !command.equals("no command")){
			if(command.contains("<player>"))
				command = command.replaceAll("<player>", player.getName());
		
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
		}
		
		if(!epicPlayer.getQuestsCompleted().contains(questNumber))
			epicPlayer.getQuestsCompleted().add(questNumber);
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
		//Quest types: Collect, Kill, Killplayer, Killanyplayer, Destroy, Place, Levelup, Enchant, Tame, Goto
		if(questtype.equalsIgnoreCase("killplayer")){ questtype = "Kill"; }
		if(questtype.equalsIgnoreCase("killanyplayer")){ questtype = "Kill"; }
		if(questtype.equalsIgnoreCase("levelup")){ questtype = "Level up"; }
		if(questtype.equalsIgnoreCase("goto")){ questtype = "Go to"; }
		
		if(questtype.equalsIgnoreCase("level up")){ taskID = "times"; }
		if(questtype.equalsIgnoreCase("Go to")){ taskID = getTaskID(task).split("=")[2]; }
		if(questtype.equalsIgnoreCase("collect")||
				questtype.equalsIgnoreCase("destroy")||
				questtype.equalsIgnoreCase("place")||
				questtype.equalsIgnoreCase("enchant")||
				questtype.equalsIgnoreCase("craft")||
				questtype.equalsIgnoreCase("smelt")){  
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
			if(questtype.equalsIgnoreCase("craft")){ questtype = "Crafted"; }
			if(questtype.equalsIgnoreCase("smelt")){ questtype = "Smelted"; }
			if(questtype.equalsIgnoreCase("goto")){ questtype = "Went to"; }
			
			//Create the message if the task is finished
			if(!questtype.equalsIgnoreCase("go to")){
				message = ChatColor.GREEN + questtype + " " + taskmax + " " + taskID + ".";
			} else {
				message = ChatColor.GREEN + questtype + " " + taskID + ".";
			}
		}else{
			if(!questtype.equalsIgnoreCase("go to")){
				message = ChatColor.RED + questtype + " " + taskprogress + "/" + taskmax + " " + taskID + ".";
			}else{
				message = ChatColor.RED + questtype + " " + taskID + ".";
			}
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
