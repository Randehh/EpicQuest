package randy.filehandlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import randy.quests.EpicQuestDatabase;

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
		
		//Load the file again
		try {
			quests.load(questfile);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InvalidConfigurationException e1) {
			e1.printStackTrace();
		}
		
		Object[] questlist = QuestLoader.quests.getKeys(false).toArray();
		for(int i = 0; i < questlist.length; i++){
			
			//Get general info
			EpicQuestDatabase.setQuestName(i, quests.getString("q"+i+".Name"));
			EpicQuestDatabase.setQuestEndInfo(i, quests.getString("q"+i+".End_Info"));
			EpicQuestDatabase.setQuestStartInfo(i, quests.getString("q"+i+".Start_Info"));
			EpicQuestDatabase.setQuestResetTime(i, quests.getInt("q"+i+".Reset_Time"));
			EpicQuestDatabase.setQuestLocked(i, quests.getString("q"+i+".Requirements.QuestsCompleted"));
			EpicQuestDatabase.setQuestLevel(i, quests.getInt("q"+i+".Requirements.Level"));
			
			//Get reward info
			EpicQuestDatabase.setRewardMoney(i, quests.getInt("q"+i+".Rewards.Money"));
			
			if(quests.getString("q"+i+".Rewards.Item.id") != null){
				String[] idString = quests.getString("q"+i+".Rewards.Item.id").split(",");
				List<String> idList = new ArrayList<String>();
				for(int e = 0; e < idString.length; e++){ idList.add(idString[e]); }
				EpicQuestDatabase.setRewardID(i, idList);
			}else{
				List<String> idList = new ArrayList<String>();
				idList.add("");
				EpicQuestDatabase.setRewardID(i, idList);
			}
			
			EpicQuestDatabase.setRewardHeroesExp(i, quests.getInt("q"+i+".Rewards.HeroesExp", 0));
			
			String[] amountString = quests.getString("q"+i+".Rewards.Item.Amount").split(",");
			List<Integer> amountList = new ArrayList<Integer>();
			for(int e = 0; e < amountString.length; e++){ amountList.add(Integer.parseInt(amountString[e])); }
			EpicQuestDatabase.setRewardAmount(i, amountList);
			EpicQuestDatabase.setRewardCommand(i, quests.getString("q"+i+".Rewards.Command"));
			
			//Get tasks info
			int taskamount = quests.getConfigurationSection("q"+i+".Tasks").getKeys(false).size();
			for(int e = 0; e < taskamount; e++){
				EpicQuestDatabase.setTaskType(i, e, quests.getString("q"+i+".Tasks."+e+".Type"));
				EpicQuestDatabase.setTaskID(i, e, quests.getString("q"+i+".Tasks."+e+".id"));
				EpicQuestDatabase.setTaskAmount(i, e, quests.getInt("q"+i+".Tasks."+e+".Amount"));
			}
			
			//Get worlds
			ArrayList<String> worldlist = new ArrayList<String>();
			String[] worlds = quests.getString("q"+i+".Requirements.Worlds").split(", ");
			for(int d = 0; d < worlds.length; d++){
				worldlist.add(worlds[d]);
			}
			EpicQuestDatabase.setQuestWorlds(i, worldlist);
			
			//Get item requirement
			EpicQuestDatabase.setQuestItemRequiredID(i, quests.getString("q"+i+".Requirements.Item.id"));
			EpicQuestDatabase.setQuestItemRequiredAmount(i, quests.getString("q"+i+".Requirements.Item.Amount"));
		}
		
		System.out.print("EpicQuest is done loading " + questlist.length + " quests.");
	}
}
