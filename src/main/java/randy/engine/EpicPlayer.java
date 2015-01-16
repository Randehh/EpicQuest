package main.java.randy.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import main.java.randy.listeners.OpenBookListener;
import main.java.randy.quests.EpicQuest;
import main.java.randy.quests.EpicQuestDatabase;
import main.java.randy.quests.EpicQuestRequirement;
import main.java.randy.quests.EpicQuestTask;
import main.java.randy.quests.EpicQuestTask.TaskTypes;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class EpicPlayer {
	
	UUID playerID;
	String playerName;
	List<EpicQuest> questList;
	List<String> questCompleted;
	int questDailyLeft;
	HashMap<String, Integer> questTimer = new HashMap<String, Integer>();
	EpicParty currentParty;
	public boolean partyChat = false;
	public EpicPlayer hasPartyInvitation = null;
	HashMap<TaskTypes, List<EpicQuestTask>> questTasks = new HashMap<TaskTypes, List<EpicQuestTask>>();
	public PlayerStatistics playerStatistics;
	
	public EpicPlayer(UUID playerID, List<EpicQuest> questList, List<String> questCompleted, int questDailyLeft, HashMap<String, Integer> questTimer, PlayerStatistics playerStatistics){
		
		this.playerID = playerID;
		this.questList = questList;
		this.questCompleted = questCompleted;
		this.questDailyLeft = questDailyLeft;
		this.questTimer = questTimer;
		this.playerStatistics = playerStatistics;
	}
	
	public EpicPlayer(UUID playerID){
		
		this.playerID = playerID;
		this.questList = new ArrayList<EpicQuest>();
		this.questCompleted = new ArrayList<String>();
		this.questDailyLeft = EpicSystem.getDailyLimit();
		HashMap<String, Integer> tempList = new HashMap<String, Integer>();
		for(int i = 0; i < (EpicQuestDatabase.getTotalAmountQuests()); i++){
			tempList.put(EpicQuestDatabase.getQuestTags().get(i), 0);
		}
		this.questTimer = tempList;
		this.playerStatistics = new PlayerStatistics(this,0,0,0,0,0);
	}

	/*
	 * 
	 * Player methods
	 * 
	 */
	public Player getPlayer(){
		Player[] playerList = Utils.getOnlinePlayers();
		for(int i = 0; i < playerList.length; i++){
			if(playerList[i].getUniqueId().equals(playerID)){
				return playerList[i];
			}
		}
		return null;
	}
	public UUID getPlayerID() { return playerID; }
	public String getPlayerName() { return playerName; }
	public void setPlayerName(String playerName) { this.playerName = playerName; }
	
	public EpicParty getParty(){
		return currentParty;
	}
	
	public void setParty(EpicParty party){
		currentParty = party;
	}
	
	public boolean hasPermission(String permission){
		if(EpicSystem.usePermissions()){
			if(getPlayer().hasPermission(permission)) return true;
		}
		
		if(permission.contains(".user.")) return true;
		if(permission.contains(".admin.") && getPlayer().isOp()) return true;
		return false;
	}
	
	public void giveQuestBook(){
		if(getPlayer() == null) return;
		
		Inventory inventory = getPlayer().getInventory();
		if(inventory.contains(Material.WRITTEN_BOOK)){
			for(int i = 0; i < inventory.getContents().length; i++){
				ItemStack item = inventory.getContents()[i];
				if(item != null && item.getType() == Material.WRITTEN_BOOK){
					BookMeta bookMeta = (BookMeta)item.getItemMeta();
					if(bookMeta.hasTitle() && bookMeta.getTitle().equals("Quest Book")) return;
				}
			}
		}
		
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
		inventory.addItem(book);
		OpenBookListener.UpdateBook(this);
	}
	
	/*
	 * 
	 * Quest methods
	 * 
	 */
	public List<EpicQuest> getQuestList(){ return questList; }
	public EpicQuest getQuest(int questNo){ return questList.get(questNo); }
	public EpicQuest getQuestByTag(String questTag){
		for(int i = 0; i < questList.size(); i++) if(getQuest(i).getQuestTag().equals(questTag)) return getQuest(i);
		return null;
	}
	public List<String> getQuestsCompleted(){ return questCompleted; }
	public int getQuestDailyLeft() { return questDailyLeft; }
	public HashMap<String, Integer> getQuestTimerMap() { return questTimer; }
	public List<EpicQuestTask> getTasksByType(TaskTypes type){
		if(questTasks.containsKey(type)) return questTasks.get(type);
		return new ArrayList<EpicQuestTask>();
	}
	
	public void setQuestsCompleted(List<String> newList){ questCompleted = newList; }
	public void setQuestTimerMap(HashMap<String, Integer> newMap){ questTimer = newMap; }
	public void setQuestTimer(String quest, Integer time){ questTimer.put(quest, time); }
	public void setQuestDailyLeft(int questsLeft){ questDailyLeft = questsLeft; }
	
	//Quest managing
	public boolean addQuest(EpicQuest quest){
		if(canGetQuest(quest.getQuestTag(), false)){
			
			quest.setEpicPlayer(this);
			
			//Take items first
			/*if(hasItemRequirements(quest.getQuestTag())){
				List<ItemStack> itemList = EpicQuestDatabase.getQuestItemsRequired(quest.getQuestTag());
				Inventory inventory = getPlayer().getInventory();
				
				if(!itemList.isEmpty()){
					for(ItemStack item : itemList){
						inventory.remove(item.clone());
					}
				}
			}*/
			
			questList.add(quest);
			playerStatistics.AddQuestsGet(1);
			
			getPlayer().sendMessage(""+ChatColor.GOLD + quest.getQuestName());
			getPlayer().sendMessage(""+ChatColor.GRAY + ChatColor.ITALIC + quest.getQuestStart());
			for(int i = 0; i < quest.getTasks().size(); i++){
				getPlayer().sendMessage(quest.getTasks().get(i).getPlayerTaskProgressText());
			}
			
			OpenBookListener.UpdateBook(this);
			return true;
		}
		return false;
	}
	public void removeQuest(EpicQuest quest){
		if(hasQuest(quest)){
			questList.remove(quest);
			for(TaskTypes type : questTasks.keySet()){
				List<EpicQuestTask> toBeRemoved = new ArrayList<EpicQuestTask>();
				for(EpicQuestTask task : questTasks.get(type)){
					if(task.getQuest() == quest){
						toBeRemoved.add(task);
					}
				}
				
				for(EpicQuestTask task : toBeRemoved){
					questTasks.get(type).remove(task);
				}
			}
			
			OpenBookListener.UpdateBook(this);
		}
	}
	public void completeAllQuests(){
		List<EpicQuest> completeableQuests = getCompleteableQuest();
		if(!completeableQuests.isEmpty()){
			for(int i = 0; i < completeableQuests.size(); i++){
				completeableQuests.get(i).completeQuest();
				playerStatistics.AddQuestsCompleted(1);
			}
		}else{
			getPlayer().sendMessage(ChatColor.RED + "There are no quests to complete!");
		}
	}
	public void completeQuest(EpicQuest quest){
		quest.completeQuest();
	}
	public void addQuestRandom(){
		List<String> obtainableQuests = getObtainableQuests();
		if(!obtainableQuests.isEmpty()){
			Random generator = new Random();
			int randomQuest = generator.nextInt(obtainableQuests.size());
			EpicQuest quest = new EpicQuest(obtainableQuests.get(randomQuest));
			addQuest(quest);
		}
	}
	public List<EpicQuest> getCompleteableQuest(){
		List<EpicQuest> tempList = new ArrayList<EpicQuest>();
		for(int i = 0; i < questList.size(); i++){
			if(questList.get(i).isCompleted()){ 
				tempList.add(questList.get(i));
			}
		}
		return tempList;
	}
	public void addTask(EpicQuestTask task){
		TaskTypes type = task.getType();
		if(!questTasks.containsKey(type)) questTasks.put(type, (List<EpicQuestTask>)new ArrayList<EpicQuestTask>());
		if(!questTasks.get(type).contains(task)) questTasks.get(type).add(task);
	}
	
	//Quest checking
	public List<String> getObtainableQuests(){
		List<String> obtainableQuests = new ArrayList<String>();
		if(!isQuestListFull()){
			for(String questTag : EpicQuestDatabase.getQuestTags()){
				if(canGetQuest(questTag, false)){
					obtainableQuests.add(questTag);
				}
			}
		}
		return obtainableQuests;
	}
	public boolean canGetQuest(String questTag, boolean sendMessage){		
		if(hasQuest(questTag) ||
				!hasDailyQuestLeft() ||
				isTimeOut(questTag) ||
				isQuestListFull()){
			return false;
		}
		
		//Check quest specific requirements
		for(EpicQuestRequirement requirement : EpicQuestDatabase.getQuestRequirements(questTag)){
			if(!requirement.hasRequirement(this, true)){
				return false;
			}
		}
		
		return true;
	}
	public boolean canGetQuest(){
		if(getObtainableQuests().isEmpty()) { return false; } else { return true; }
	}
	public boolean hasQuest(String questTag){
		for(int i = 0; i < questList.size(); i++){
			if(questList.get(i).getQuestTag().equalsIgnoreCase(questTag)){
				return true;
			}
		}
		return false;
	}
	public boolean hasQuest(EpicQuest quest){
		if(questList.contains(quest)){ return true; }
		return false;
	}
	public boolean hasDailyQuestLeft(){
		if(questDailyLeft > 0){ return true; }
		return false;
	}
	public boolean isTimeOut(String questTag){
		if(EpicQuestDatabase.getQuestResetTime(questTag) == -1 && getQuestsCompleted().contains(questTag)) return false;
		
		checkTimer(questTag, false);
		
		if(questTimer.get(questTag) <= 0){ return true; }
		return false;
	}
	public boolean isQuestListFull(){
		if(questList.size() >= EpicSystem.getQuestLimit()){ return true; }
		return false;
	}
	public void checkTimer(String questTag, boolean substractDifference){		
		int timeDifference = EpicSystem.getTime() - EpicSystem.getStartTime();
		int newQuestTime = questTimer.get(questTag) - timeDifference;
		
		if(substractDifference){
			questTimer.put(questTag, newQuestTime);
		}
		
		if(newQuestTime <= 0){
			newQuestTime = 0;
			questTimer.put(questTag, 0);
		}
	}
}
