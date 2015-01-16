package main.java.randy.filehandlers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import main.java.randy.engine.Utils;
import main.java.randy.epicquest.MetricsHandler;
import main.java.randy.quests.EpicQuestDatabase;
import main.java.randy.quests.EpicQuestRequirement;
import main.java.randy.quests.EpicQuestRequirement.RequirementTypes;
import main.java.randy.quests.EpicQuestReward;
import main.java.randy.quests.EpicQuestTask;
import main.java.randy.quests.EpicQuestReward.RewardTypes;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class QuestLoader {
	
	/*
	 * Load quests
	 */
	public static void loadQuests(){
		
		File folder = new File("plugins" + File.separator + "EpicQuest" + File.separator + "Quests");
        String[] fileNames = folder.list();
        if(fileNames.length > 0){
        	for(String questTag : fileNames){
        		
        		questTag = questTag.replace(".yml", "");
        		File questFile = new File("plugins" + File.separator + "EpicQuest" + File.separator + "Quests" + File.separator + questTag + ".yml");
        		YamlConfiguration quest = YamlConfiguration.loadConfiguration(questFile);
        		
        		//Set basic info
        		EpicQuestDatabase.AddQuestTag(questTag);
        		EpicQuestDatabase.setQuestName(questTag, quest.getString("Name"));
    			EpicQuestDatabase.setQuestEndInfo(questTag, quest.getString("End_Info"));
    			EpicQuestDatabase.setQuestStartInfo(questTag, quest.getString("Start_Info"));
    			
    			//Get requirements
    			List<EpicQuestRequirement> questRequirements = new ArrayList<EpicQuestRequirement>();
    			questRequirements.add(new EpicQuestRequirement(RequirementTypes.QUESTS_COMPLETED, quest.getStringList("Requirements.QuestsCompleted")));
    			questRequirements.add(new EpicQuestRequirement(RequirementTypes.LEVEL, quest.getInt("Requirements.Level", 0)));
    			questRequirements.add(new EpicQuestRequirement(RequirementTypes.WORLDS, quest.getStringList("Requirements.Worlds")));
    			List<String> itemRequirementList = quest.getStringList("Requirements.Items");
    			List<ItemStack> itemRequirements = new ArrayList<ItemStack>();
    			for(String item : itemRequirementList){
    				itemRequirements.add(Utils.StringToItemStack(item, "="));
    			}
    			questRequirements.add(new EpicQuestRequirement(RequirementTypes.ITEMS, itemRequirements));
    			EpicQuestDatabase.setRequirements(questTag, questRequirements);
    			EpicQuestDatabase.setQuestResetTime(questTag, quest.getInt("Reset_Time"));
    			
    			//Get reward info
    			List<EpicQuestReward> questRewards = new ArrayList<EpicQuestReward>();
    			questRewards.add(new EpicQuestReward(RewardTypes.MONEY, quest.getInt("Rewards.Money", 0)));

    			List<String> itemRewardList = quest.getStringList("Rewards.Item");
    			List<ItemStack> itemRewards = new ArrayList<ItemStack>();
    			for(String item : itemRewardList){
    				itemRewards.add(Utils.StringToItemStack(item, "="));
    			}
    			questRewards.add(new EpicQuestReward(RewardTypes.ITEM, itemRewards));
    			
    			questRewards.add(new EpicQuestReward(RewardTypes.COMMAND, quest.getStringList("Rewards.Command")));
    			questRewards.add(new EpicQuestReward(RewardTypes.HEROES_EXP, quest.getInt("Rewards.HeroesExp", 0)));
    			questRewards.add(new EpicQuestReward(RewardTypes.RANK, quest.getString("Rewards.Permission_Group")));
    			EpicQuestDatabase.setRewards(questTag, questRewards);
    			
    			//Get tasks info
    			int taskamount = quest.getConfigurationSection("Tasks").getKeys(false).size();
    			for(int e = 0; e < taskamount; e++){
    				EpicQuestDatabase.setTaskType(questTag, e, EpicQuestTask.getTaskTypeFromText(quest.getString("Tasks."+e+".Type")));
    				EpicQuestDatabase.setTaskID(questTag, e, quest.getString("Tasks."+e+".id"));
    				EpicQuestDatabase.setTaskAmount(questTag, e, quest.getInt("Tasks."+e+".Amount"));
    			}
    			MetricsHandler.taskAmount += taskamount;
    			
    			EpicQuestDatabase.setQuestAutoComplete(questTag, quest.getBoolean("Auto_Complete"));
        	}
        }
        MetricsHandler.questAmount = fileNames.length;
		System.out.print("[EpicQuest] Done loading " + fileNames.length + " quests.");
	}
}
