package randy.epicquest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import randy.listeners.OpenBookListener;
import randy.quests.EpicQuest;
import randy.quests.EpicQuestDatabase;
import randy.quests.EpicQuestTask;
import randy.quests.EpicQuestTask.TaskTypes;

public class EpicPlayer {
	
	String playerName;
	List<EpicQuest> questList;
	List<Integer> questCompleted;
	int questDailyLeft;
	List<Integer> questTimer;
	float statMoneyEarned;
	int statQuestCompleted;
	int statQuestDropped;
	int statQuestGet;
	int statTaskCompleted;
	EpicParty currentParty;
	public boolean partyChat = false;
	public EpicPlayer hasPartyInvitation = null;
	HashMap<TaskTypes, List<EpicQuestTask>> questTasks = new HashMap<TaskTypes, List<EpicQuestTask>>();
	
	public EpicPlayer(String playerName, List<EpicQuest> questList, List<Integer> questCompleted, int questDailyLeft, List<Integer> questTimer, float statMoneyEarned, int statQuestCompleted, int statQuestDropped, int statQuestGet, int statTaskCompleted){
		
		this.playerName = playerName;
		this.questList = questList;
		this.questCompleted = questCompleted;
		this.questDailyLeft = questDailyLeft;
		this.questTimer = questTimer;
		this.statMoneyEarned = statMoneyEarned;
		this.statQuestCompleted = statQuestCompleted;
		this.statQuestDropped = statQuestDropped;
		this.statQuestGet = statQuestGet;
		this.statTaskCompleted = statTaskCompleted;
		
	}
	
	public EpicPlayer(String playerName){
		
		this.playerName = playerName;
		this.questList = new ArrayList<EpicQuest>();
		this.questCompleted = new ArrayList<Integer>();
		questCompleted.add(-1);
		this.questDailyLeft = EpicSystem.getDailyLimit();
		List<Integer> tempList = new ArrayList<Integer>();
		for(int i = 0; i < (EpicQuestDatabase.getTotalAmountQuests()); i++){
			tempList.add(0);
		}
		this.questTimer = tempList;
		this.statMoneyEarned = 0;
		this.statQuestCompleted = 0;
		this.statQuestDropped = 0;
		this.statQuestGet = 0;
		this.statTaskCompleted = 0;
	}

	/*
	 * 
	 * Player methods
	 * 
	 */
	public Player getPlayer(){
		@SuppressWarnings("deprecation")
		Player[] playerList = Bukkit.getOnlinePlayers();
		for(int i = 0; i < playerList.length; i++){
			if(playerList[i].getName().equals(playerName)){
				return playerList[i];
			}
		}
		return null;
	}
	public String getPlayerName() { return playerName; }
	
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
					if(bookMeta.getTitle().equals("Quest Book")) return;
				}
			}
		}
		
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
		BookMeta bookMeta = (BookMeta)book.getItemMeta();
		bookMeta.setTitle("Quest Book");
		book.setItemMeta(bookMeta);
		getPlayer().getInventory().addItem(book);
	}
	
	/*
	 * 
	 * Quest methods
	 * 
	 */
	public List<EpicQuest> getQuestList(){ return questList; }
	public List<Integer> getQuestListNumbers() {
		List<Integer> questNumbers = new ArrayList<Integer>();
		for(int i = 0; i < questList.size(); i ++){
			questNumbers.add(questList.get(i).getQuestNo());
		}
		return questNumbers;
	}
	public EpicQuest getQuest(int questNo){ return questList.get(questNo); }
	public EpicQuest getQuestByNumber(int questNo){
		for(int i = 0; i < questList.size(); i++) if(getQuest(i).getQuestNo() == questNo) return getQuest(i);
		return null;
	}
	public List<Integer> getQuestsCompleted(){ return questCompleted; }
	public int getQuestDailyLeft() { return questDailyLeft; }
	public List<Integer> getQuestTimerList() { return questTimer; }
	public List<EpicQuestTask> getTasksByType(TaskTypes type){
		if(questTasks.containsKey(type)) return questTasks.get(type);
		return new ArrayList<EpicQuestTask>();
	}
	
	public void setQuestsCompleted(List<Integer> newList){ questCompleted = newList; }
	public void setQuestTimerList(List<Integer> newList){ questTimer = newList; }
	public void setQuestTimer(Integer quest, Integer time){ questTimer.set(quest, time); }
	public void setQuestDailyLeft(int questsLeft){ questDailyLeft = questsLeft; }
	
	//Quest managing
	public boolean addQuest(EpicQuest quest){
		if(canGetQuest(quest.getQuestNo()) == true){
			
			//Take items first
			if(hasItemRequirements(quest.getQuestNo())){
				List<Integer> amountList = EpicQuestDatabase.getQuestItemRequiredAmount(quest.getQuestNo());
				List<String> idList = EpicQuestDatabase.getQuestItemRequiredID(quest.getQuestNo());
				Inventory inventory = getPlayer().getInventory();
					
				for(int i = 0; i < idList.size(); i++){
					int amountLeft = amountList.get(i);
					Material material = Material.matchMaterial(idList.get(i));
					while(amountLeft >= 1){						
						ItemStack[] inventoryStacks = inventory.getContents();
						for(int e = 0; e < inventoryStacks.length; e++){
							ItemStack currentStack = inventoryStacks[e];
							if(currentStack != null && currentStack.getType() == material){
								int currentStackAmount = currentStack.getAmount();
								if(currentStackAmount >= amountLeft){
									currentStackAmount -= amountLeft;
									amountLeft = 0;
									if(currentStackAmount == 0) inventory.remove(currentStack);
									else currentStack.setAmount(currentStackAmount);
									break;
								}else{
									amountLeft -= currentStackAmount;
									inventory.remove(currentStack);
								}
								break;
							}
						}
					}
				}
			}
			
			questList.add(quest);
			modifyStatQuestGet(1);
			
			getPlayer().sendMessage(""+ChatColor.GOLD + quest.getQuestName());
			getPlayer().sendMessage(""+ChatColor.GRAY + ChatColor.ITALIC + quest.getQuestStart());
			for(int i = 0; i < quest.getTasks().size(); i++){
				getPlayer().sendMessage(quest.getTasks().get(i).getPlayerTaskProgressText());
			}
			
			OpenBookListener.UpdateBook(this);
			return true;
		}else{
			
			//If the reason was not having the required items
			if(!hasItemRequirements(quest.getQuestNo())){
				getPlayer().sendMessage(ChatColor.RED + "You do not have the required items for this quest!");
			}
			return false;
		}
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
				modifyStatQuestCompleted(1);
			}
		}else{
			getPlayer().sendMessage(ChatColor.RED + "There are no quests to complete!");
		}
	}
	public void completeQuest(EpicQuest quest){
		quest.completeQuest();
	}
	public void addQuestRandom(){
		List<Integer> obtainableQuests = getObtainableQuests();
		if(!obtainableQuests.isEmpty()){
			Random generator = new Random();
			int randomQuest = generator.nextInt(obtainableQuests.size());
			EpicQuest quest = new EpicQuest(this, obtainableQuests.get(randomQuest));
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
	public List<Integer> getObtainableQuests(){
		List<Integer> obtainableQuests = new ArrayList<Integer>();
		if(!isQuestListFull()){
			for(int i = 0; i < EpicQuestDatabase.getTotalAmountQuests(); i++){
				if(canGetQuest(i)){
					obtainableQuests.add(i);
				}
			}
		}
		return obtainableQuests;
	}
	public boolean canGetQuest(int questNo){
		
		if(!hasQuest(questNo) &&
				hasUnlockedQuest(questNo) &&
				hasDailyQuestLeft() &&
				isTimeOut(questNo) &&
				!isQuestListFull() &&
				hasItemRequirements(questNo) &&
				isHighEnoughLevel(questNo)){
			return true;
		}
		return false;
	}
	public boolean canGetQuest(){
		if(getObtainableQuests().isEmpty()) { return false; } else { return true; }
	}
	public boolean hasQuest(int questNo){
		
		for(int i = 0; i < questList.size(); i++){
			if(questList.get(i).getQuestNo() == questNo){
				return true;
			}
		}
		return false;
	}
	public boolean hasQuest(EpicQuest quest){
		if(questList.contains(quest)){ return true; }
		return false;
	}
	public boolean hasUnlockedQuest(int questNo){
		List<Integer> questLockedList = EpicQuestDatabase.getQuestLocked(questNo);
		
		if(questCompleted.containsAll(questLockedList)){ return true; }
		return false;
	}
	public boolean hasDailyQuestLeft(){
		if(questDailyLeft > 0){ return true; }
		return false;
	}
	public boolean isTimeOut(int questNo){
		if(EpicQuestDatabase.getQuestResetTime(questNo) == -1 && getQuestsCompleted().contains(questNo)) return true;
		
		checkTimer(questNo, false);
		
		if(questTimer.get(questNo) <= 0){ return true; }
		return false;
	}
	public boolean isQuestListFull(){
		if(questList.size() >= EpicSystem.getQuestLimit()){ return true; }
		return false;
	}
	public boolean hasItemRequirements(int questNo){
		List<Integer> amountList = EpicQuestDatabase.getQuestItemRequiredAmount(questNo);
		List<String> idList = EpicQuestDatabase.getQuestItemRequiredID(questNo);
		
		Inventory inventory = getPlayer().getInventory();
			
		for(int i = 0; i < idList.size(); i++){
			if(amountList.get(i) >= 1 &&
					idList.get(i) != null &&
					idList.get(i) != ""){
				Material material = Material.matchMaterial(idList.get(i));
				if(material == null) return true;
				if(!inventory.contains(material, amountList.get(i))) return false;
			}
			
		}
		return true;
	}
	public boolean isHighEnoughLevel(int questNo){
		int requiredLevel = EpicQuestDatabase.getQuestLevel(questNo);
		int playerLevel = getPlayer().getLevel();
		if(playerLevel >= requiredLevel) return true;
		return false;
	}
	public void checkTimer(int questNo, boolean substractDifference){		
		int timeDifference = EpicSystem.getTime() - EpicSystem.getStartTime();
		int newQuestTime = questTimer.get(questNo) - timeDifference;
		
		if(substractDifference){
			questTimer.set(questNo, newQuestTime);
		}
		
		if(newQuestTime <= 0){
			newQuestTime = 0;
			questTimer.set(questNo, 0);
		}
	}
	
	/*
	 * 
	 * Stats
	 * 
	 */
	public float getStatMoneyEarned(){ return statMoneyEarned; }
	public int getStatQuestCompleted() { return statQuestCompleted; }
	public int getStatQuestDropped(){ return statQuestDropped; }
	public int getStatQuestGet() { return statQuestGet; }
	public int getStatTaskCompleted(){ return statTaskCompleted; }
	
	//Stat modifying
	public void modifyStatMoneyEarned(float amount){ statMoneyEarned += amount; }
	public void modifyStatQuestCompleted(int amount){ statQuestCompleted += amount; }
	public void modifyStatQuestDropped(int amount){ statQuestDropped += amount; }
	public void modifyStatQuestGet(int amount){ statQuestGet += amount; }
	public void modifyStatTaskCompleted(int amount){ statTaskCompleted += amount; }

}
