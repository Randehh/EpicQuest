package randy.filehandlers;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import randy.epicquest.EpicQuestDatabase;

public class QuestLoader {
	
	/*
	 * Get file(s)
	 */	
	static File questfile = new File("plugins" + File.separator + "EpicQuest" + File.separator + "quests.yml");
	static FileConfiguration quests = YamlConfiguration.loadConfiguration(questfile);
	
	/*
	 * Load quests
	 */
	public static void loadQuests(){
		
		Object[] questlist = QuestLoader.quests.getKeys(false).toArray();
		for(int i = 0; i < questlist.length; i++){
			//Get general info
			EpicQuestDatabase.setQuestName(i, quests.getString("q"+i+".Name"));
			EpicQuestDatabase.setQuestEndInfo(i, quests.getString("q"+i+".End_Info"));
			EpicQuestDatabase.setQuestStartInfo(i, quests.getString("q"+i+".Start_Info"));
			EpicQuestDatabase.setQuestResetTime(i, quests.getInt("q"+i+".Reset_Time"));
			EpicQuestDatabase.setQuestLocked(i, quests.getString("q"+i+".Locked"));
			
			//Get reward info
			EpicQuestDatabase.setRewardMoney(i, quests.getInt("q"+i+".Rewards.Money"));
			EpicQuestDatabase.setRewardID(i, quests.getInt("q"+i+".Rewards.Item.id"));
			EpicQuestDatabase.setRewardAmount(i, quests.getInt("q"+i+".Rewards.Item.Amount"));
			
			//Get tasks info
			int taskamount = quests.getConfigurationSection("q"+i+".Tasks").getKeys(false).size();
			for(int e = 0; e < taskamount; e++){
				EpicQuestDatabase.setTaskType(i, e, quests.getString("q"+i+".Tasks."+e+".Type"));
				EpicQuestDatabase.setTaskID(i, e, quests.getString("q"+i+".Tasks."+e+".id"));
				EpicQuestDatabase.setTaskAmount(i, e, quests.getInt("q"+i+".Tasks."+e+".Amount"));
			}
			
			//Get worlds
			ArrayList<String> worldlist = new ArrayList<String>();
			String[] worlds = quests.getString("q"+i+".Worlds").split(", ");
			for(int d = 0; d < worlds.length; d++){
				worldlist.add(worlds[d]);
			}
			EpicQuestDatabase.setQuestWorlds(i, worldlist);
		}
		
		System.out.print("EpicQuest is done loading " + questlist.length + " quests.");
	}
}
