package randy.epicquest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
	EpicPlayer hasPartyInvitation = null;
	
	
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
	public List<Integer> getQuestsCompleted(){ return questCompleted; }
	public int getQuestDailyLeft() { return questDailyLeft; }
	public List<Integer> getQuestTimerList() { return questTimer; }
	
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
			for(int i = 0; i < quest.getTaskAmount(); i++){
				getPlayer().sendMessage(quest.getPlayerTaskProgressText(i));
			}
			
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
			if(questList.get(i).getPlayerQuestCompleted()){ 
				tempList.add(questList.get(i));
			}
		}
		return tempList;
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
		
		System.out.print("--------------");
		System.out.print(hasQuest(questNo));
		System.out.print(hasUnlockedQuest(questNo));
		System.out.print(hasDailyQuestLeft());
		System.out.print(isTimeOut(questNo));
		System.out.print(isQuestListFull() );
		System.out.print(hasItemRequirements(questNo));
		
		if(!hasQuest(questNo) &&
				hasUnlockedQuest(questNo) &&
				hasDailyQuestLeft() &&
				isTimeOut(questNo) &&
				!isQuestListFull() &&
				hasItemRequirements(questNo)){
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
