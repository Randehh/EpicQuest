package main.java.randy.filehandlers;

import java.io.File;
import java.util.logging.Logger;

import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;
import main.java.randy.engine.EpicPlayer;

public class DatabaseHandler {
	private static Database sql;
	
	public static void savePlayers(EpicPlayer epicPlayer){
		if(!databaseIsOpen()) try{
				openDatabaseConnection();
			}catch(Exception e){
				setDatabaseConnection(Logger.getLogger("minecraft"),
	                      "[EpicQuest]",
	                      "plugins" + File.separator + "EpicQuest" + File.separator + "players",
	                      "Players",
	                      ".sqlite");
			}
	}
	
	public static void setDatabaseConnection(Logger logger, String prefix, String location, String filename, String extension){
		sql = new SQLite(logger, prefix, location, filename, extension);
	}
	
	public static void openDatabaseConnection(){
		sql.open();
	}
	
	public static boolean databaseIsOpen(){
		return sql.isOpen();
	}
	
	//TODO: Make the DatabaseHandler act the same as this method
	/*public static void savePlayer(EpicPlayer epicPlayer){		
		UUID id = epicPlayer.getPlayerID();
		File savefile = new File("plugins" + File.separator + "EpicQuest" + File.separator + "Players" + File.separator + id.toString() + ".yml");

		//Reset the file by recreating the file
		try {
			if(!savefile.exists()){
				savefile.createNewFile();
			}else{
				savefile.delete();
				savefile.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Make the file editable
		FileConfiguration save = YamlConfiguration.loadConfiguration(savefile);

		//Save task progress
		List<EpicQuest> questlist = epicPlayer.getQuestList();
		List<String> questTags = new ArrayList<String>();
		
		if(!questlist.isEmpty()){
			for(int e = 0; e < questlist.size(); e++){
				EpicQuest epicQuest = questlist.get(e);
				String questTag = epicQuest.getQuestTag();
				List<EpicQuestTask> taskList = epicQuest.getTasks();
				for(int taskNumber = 0; taskNumber < taskList.size(); taskNumber++){
					save.set("Quest."+questTag+"."+taskNumber, taskList.get(taskNumber).getTaskProgress());
				}
				
				questTags.add(questlist.get(e).getQuestTag());
			}
			save.set("Quest_list", questTags);
		}

		//Save the list of completed quests the player has
		save.set("Completed_Quests", epicPlayer.getQuestsCompleted());

		//Save list of quests that have timers running
		HashMap<String, Integer> timerQuestMap = epicPlayer.getQuestTimerMap();
		List<String> timerQuestTags = new ArrayList<String>();
		for(String questTag : timerQuestMap.keySet()){
			
			//Update timer
			epicPlayer.checkTimer(questTag, true);

			//Save the timer for the quests
			save.set("Quest."+questTag+".timer", timerQuestMap.get(questTag));
			timerQuestTags.add(questTag);
		}
		save.set("Timed_Quests", timerQuestTags);

		//Save stats
		save.set("Stats.Money_Earned", epicPlayer.playerStatistics.GetMoneyEarned());
		save.set("Stats.Quests_Completed", epicPlayer.playerStatistics.GetQuestsCompleted());
		save.set("Stats.Quests_Dropped", epicPlayer.playerStatistics.GetQuestsDropped());
		save.set("Stats.Quests_Get", epicPlayer.playerStatistics.GetQuestsGet());
		save.set("Stats.Tasks_Completed", epicPlayer.playerStatistics.GetTasksCompleted());

		//Set daily limit
		save.set("Daily_Left", epicPlayer.getQuestDailyLeft());

		//Save file
		try {			
			save.save(savefile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
}
