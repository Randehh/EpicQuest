package main.java.randy.quests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.java.randy.epicquest.MetricsHandler;
import main.java.randy.quests.EpicQuestTask.TaskTypes;

public class EpicQuestDatabase {
	
	private static List<String> questTags = new ArrayList<String>();
	
	//Quest
	private static HashMap<String, String> questName = new HashMap<String, String>();
	private static HashMap<String, String> questStartInfo = new HashMap<String, String>();
	private static HashMap<String, String> questEndInfo = new HashMap<String, String>();
	
	//Tasks
	private static HashMap<String, TaskTypes> questTaskType = new HashMap<String, TaskTypes>();
	private static HashMap<String, String> questTaskID = new HashMap<String, String>();
	private static HashMap<String, Integer> questTaskAmount = new HashMap<String, Integer>();
	
	//Rewards
	private static HashMap<String, List<EpicQuestReward>> questRewards = new HashMap<String, List<EpicQuestReward>>();
	
	//Requirements
	private static HashMap<String, List<EpicQuestRequirement>> questRequirements = new HashMap<String, List<EpicQuestRequirement>>();
	private static HashMap<String, Integer> questResetTime = new HashMap<String, Integer>();
	
	//Misc
	private static HashMap<String, Boolean> questAutoComplete = new HashMap<String, Boolean>();
	
	public static void ClearDatabase(){
		questName.clear();
		questStartInfo.clear();
		questEndInfo.clear();
		questTaskType.clear();
		questTaskID.clear();
		questTaskAmount.clear();
		questRewards.clear();
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
	
	public static List<EpicQuestRequirement> getQuestRequirements(String quest){
		return questRequirements.get(quest);
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
	public static List<EpicQuestReward> getRewards(String quest){
		return questRewards.get(quest);
	}
	
	/*
	 * Misc functions
	 */
	public static Integer getTotalAmountQuests(){
		return questTags.size();
	}
	
	public static List<String> getQuestTags(){
		return questTags;
	}
	
	public static Boolean getQuestAutoComplete(String quest){
		return questAutoComplete.get(quest);
	}
	
	public static Integer getQuestResetTime(String quest){
		return questResetTime.get(quest);
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
	public static void setRequirements(String quest, List<EpicQuestRequirement> requirements){
		for(EpicQuestRequirement requirement : requirements){
			if(requirement.isEmpty()) continue;
			//MetricsHandler.incrementRewardType(reward.type);
		}
		questRequirements.put(quest, requirements);
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
	public static void setRewards(String quest, List<EpicQuestReward> rewards){
		for(EpicQuestReward reward : rewards){
			if(reward.isEmpty()) continue;
			MetricsHandler.incrementRewardType(reward.type);
		}
		questRewards.put(quest, rewards);
	}
	
	/*
	 * Misc functions
	 */
	public static void AddQuestTag(String quest){
		if(questTags.contains(quest)) return;
		questTags.add(quest);
	}
	public static void setQuestAutoComplete(String quest, Boolean autcomplete){
		questAutoComplete.put(quest, autcomplete);
	}
	public static void setQuestResetTime(String quest, Integer time){
		questResetTime.put(quest, time);
	}
}
