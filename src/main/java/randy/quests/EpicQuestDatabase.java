package main.java.randy.quests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.java.randy.epicquest.MetricsHandler;
import main.java.randy.quests.EpicQuestReward.RewardTypes;
import main.java.randy.quests.EpicQuestTask.TaskTypes;

import org.bukkit.inventory.ItemStack;

public class EpicQuestDatabase {
	
	private static List<String> questTags = new ArrayList<String>();
	
	//Quest
	private static HashMap<String, String> questName = new HashMap<String, String>();
	private static HashMap<String, String> questStartInfo = new HashMap<String, String>();
	private static HashMap<String, String> questEndInfo = new HashMap<String, String>();
	private static HashMap<String, List<String>> questWorlds = new HashMap<String, List<String>>();
	
	//Tasks
	private static HashMap<String, TaskTypes> questTaskType = new HashMap<String, TaskTypes>();
	private static HashMap<String, String> questTaskID = new HashMap<String, String>();
	private static HashMap<String, Integer> questTaskAmount = new HashMap<String, Integer>();
	
	//Rewards
	private static HashMap<String, Integer> questRewardMoney = new HashMap<String, Integer>();
	private static HashMap<String, List<ItemStack>> questRewardItems = new HashMap<String, List<ItemStack>>();
	private static HashMap<String, String> questRewardRank = new HashMap<String, String>();
	private static HashMap<String, List<String>> questRewardCommand = new HashMap<String, List<String>>();
	private static HashMap<String, Integer> questRewardHeroesExp = new HashMap<String, Integer>();
	
	//Misc
	private static HashMap<String, Integer> questResetTime = new HashMap<String, Integer>();
	private static HashMap<String, List<String>> questLocked = new HashMap<String, List<String>>();
	private static HashMap<String, List<ItemStack>> questItemsRequired = new HashMap<String, List<ItemStack>>();
	private static HashMap<String, Integer> questLevel = new HashMap<String, Integer>();
	private static HashMap<String, Boolean> questAutoComplete = new HashMap<String, Boolean>();
	
	public static void ClearDatabase(){
		questName.clear();
		questStartInfo.clear();
		questEndInfo.clear();
		questWorlds.clear();
		questTaskType.clear();
		questTaskID.clear();
		questTaskAmount.clear();
		questRewardMoney.clear();
		questRewardItems.clear();
		questRewardRank.clear();
		questRewardCommand.clear();
		questResetTime.clear();
		questLocked.clear();
		questItemsRequired.clear();
		questLevel.clear();
		questAutoComplete.clear();
	}
	
	/*
	 * 
	 * Getters
	 * 
	 */
	
	/*
	 * Quest functions
	 */
	public static String getQuestName(String quest){
		return questName.get(quest);
	}
	
	public static String getQuestStartInfo(String quest){
		return questStartInfo.get(quest);
	}
	
	public static String getQuestEndInfo(String quest){
		return questEndInfo.get(quest);
	}
	
	public static List<String> getQuestWorlds(String quest){
		return questWorlds.get(quest);
	}
	
	
	/*
	 * Tasks functions
	 */
	public static TaskTypes getTaskType(String quest, Integer task){
		return questTaskType.get(quest+"."+task);
	}
	
	public static String getTaskID(String quest, Integer task){
		return questTaskID.get(quest+"."+task);
	}
	
	public static Integer getTaskAmount(String quest, Integer task){
		return questTaskAmount.get(quest+"."+task);
	}
	
	public static Integer getTaskTotal(String quest){
		int count;
		for(count = 0; questTaskID.containsKey(quest+"."+count); count++){	}
		return count;
	}
	
	/*
	 * Rewards funtions
	 */
	public static Integer getRewardMoney(String quest){
		MetricsHandler.incrementRewardType(RewardTypes.MONEY);
		return questRewardMoney.get(quest);
	}
	
	public static List<ItemStack> getRewardItems(String quest){
		MetricsHandler.incrementRewardType(RewardTypes.ITEM);
		return questRewardItems.get(quest);
	}
	
	public static String getRewardRank(String quest){
		MetricsHandler.incrementRewardType(RewardTypes.RANK);
		return questRewardRank.get(quest);
	}
	
	public static List<String> getRewardCommand(String quest){
		MetricsHandler.incrementRewardType(RewardTypes.COMMAND);
		return questRewardCommand.get(quest);
	}
	
	public static int getRewardHeroesExp(String quest){
		MetricsHandler.incrementRewardType(RewardTypes.HEROES_EXP);
		return questRewardHeroesExp.get(quest);
	}
	
	/*
	 * Misc functions
	 */
	public static Integer getQuestResetTime(String quest){
		return questResetTime.get(quest);
	}
	
	public static List<String> getQuestLocked(String quest){
		return questLocked.get(quest);
	}
	
	public static Integer getTotalAmountQuests(){
		return questTags.size();
	}
	
	public static List<ItemStack> getQuestItemsRequired(String quest){
		return questItemsRequired.get(quest);
	}
	
	public static Integer getQuestLevel(String quest){
		return questLevel.get(quest);
	}
	
	public static List<String> getQuestTags(){
		return questTags;
	}
	
	public static Boolean getQuestAutoComplete(String quest){
		return questAutoComplete.get(quest);
	}
	
	/*
	 * 
	 * Setters
	 * 
	 */
	
	/*
	 * Quest functions
	 */
	public static void setQuestName(String quest, String name){
		questName.put(quest, name);
	}
	
	public static void setQuestStartInfo(String quest, String info){
		questStartInfo.put(quest, info);
	}
	
	public static void setQuestEndInfo(String quest, String info){
		questEndInfo.put(quest, info);
	}
	
	public static void setQuestWorlds(String quest, List<String> worlds){
		questWorlds.put(quest, worlds);
	}
	
	
	
	
	/*
	 * Tasks functions
	 */
	public static void setTaskType(String quest, Integer task, TaskTypes type){
		questTaskType.put(quest+"."+task, type);
		MetricsHandler.incrementTaskType(type);
	}
	
	public static void setTaskID(String quest, Integer task, String id){
		questTaskID.put(quest+"."+task, id);
	}
	
	public static void setTaskAmount(String quest, Integer task, Integer amount){
		questTaskAmount.put(quest+"."+task, amount);
	}
	
	/*
	 * Rewards funtions
	 */
	public static void setRewardMoney(String quest, Integer amount){
		questRewardMoney.put(quest, amount);
	}
	
	public static void setRewardItems(String quest, List<ItemStack> items){
		questRewardItems.put(quest, items);
	}
	
	public static void setRewardRank(String quest, String rank){
		questRewardRank.put(quest, rank);
	}
	
	public static void setRewardCommand(String quest, List<String> commands){
		questRewardCommand.put(quest, commands);
	}
	
	public static void setRewardHeroesExp(String quest, Integer amount){
		questRewardHeroesExp.put(quest, amount);
	}
	
	/*
	 * Misc functions
	 */
	public static void setQuestResetTime(String quest, Integer time){
		questResetTime.put(quest, time);
	}
	
	public static void setQuestLocked(String quest, List<String> quests){
		questLocked.put(quest, quests);
	}
	
	public static void setQuestItemsRequired(String quest, List<ItemStack> items){
		questItemsRequired.put(quest, items);
	}
	
	public static void setQuestLevel(String quest, Integer level){
		questLevel.put(quest, level);
	}
	public static void AddQuestTag(String quest){
		if(questTags.contains(quest)) return;
		questTags.add(quest);
	}
	public static void setQuestAutoComplete(String quest, Boolean autcomplete){
		questAutoComplete.put(quest, autcomplete);
	}
}
