package main.java.randy.filehandlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import main.java.randy.engine.EpicAnnouncer;
import main.java.randy.engine.EpicLeaderboard;
import main.java.randy.engine.EpicPlayer;
import main.java.randy.engine.EpicSign;
import main.java.randy.engine.EpicSystem;
import main.java.randy.engine.PlayerStatistics;
import main.java.randy.epicquest.EpicMain;
import main.java.randy.questentities.QuestEntity;
import main.java.randy.questentities.QuestEntityHandler;
import main.java.randy.questentities.SentenceBatch;
import main.java.randy.questentities.QuestEntity.QuestPhase;
import main.java.randy.quests.EpicQuest;
import main.java.randy.quests.EpicQuestTask;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class SaveLoader {

	/*
	 * This is the class that will load and save player progress.
	 */

	/*
	 * Load all files for player progress saving
	 */

	static File configfile = new File("plugins" + File.separator + "EpicQuest" + File.separator + "config.yml");
	static FileConfiguration config = YamlConfiguration.loadConfiguration(configfile);

	static File signfile = new File("plugins" + File.separator + "EpicQuest" + File.separator + "signs.yml");

	static File blockfile = new File("plugins" + File.separator + "EpicQuest" + File.separator + "block.yml");
	static FileConfiguration block = YamlConfiguration.loadConfiguration(blockfile);
	
	static File announcerfile = new File("plugins" + File.separator + "EpicQuest" + File.separator + "announcer.yml");
	static FileConfiguration announcer = YamlConfiguration.loadConfiguration(announcerfile);

	static File leaderboardfile = new File("plugins" + File.separator + "EpicQuest" + File.separator + "leaderboard.yml");
	static FileConfiguration leaderboard = YamlConfiguration.loadConfiguration(leaderboardfile);
	
	/*
	 * Save players
	 */
	public static void save(boolean isShutDown) throws IOException, InvalidConfigurationException {

		System.out.print("Saving...");
		
		//Set time
		config.set("Time", EpicSystem.getTime());
		config.set("Save_Time", EpicSystem.getSaveTime());

		config.save(configfile);

		//Reset the file by recreating the file
		if(!signfile.exists()){
			signfile.createNewFile();
		}else{
			signfile.delete();
			signfile.createNewFile();
		}
		
		//Reload the file
		FileConfiguration signFile = YamlConfiguration.loadConfiguration(signfile);

		//Get the quest signs if possible and go through them, remove the section first			
		List<EpicSign> questsignlist = EpicSystem.getSignList();

		if(!questsignlist.isEmpty()){

			for(int i = 0; i < questsignlist.size(); i++){

				EpicSign sign = questsignlist.get(i);
				Location loc = sign.getLocation();
				String quest = sign.getQuest();
				signFile.set("Signs." + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ(), quest);
			}
		}
		signFile.save(signfile);
		
		//Leaderboards
		if(!EpicLeaderboard.questsCompleted.isEmpty()){
			List<String> list = new ArrayList<String>();
			for(UUID id : EpicLeaderboard.questsCompleted.keySet()){
				list.add(id.toString()+"="+EpicLeaderboard.questsCompleted.get(id));
			}
			leaderboard.set("Quests_Completed", list);
		}
		if(!EpicLeaderboard.tasksCompleted.isEmpty()){
			List<String> list = new ArrayList<String>();
			for(UUID id : EpicLeaderboard.tasksCompleted.keySet()){
				list.add(id.toString()+"="+EpicLeaderboard.tasksCompleted.get(id));
			}
			leaderboard.set("Tasks_Completed", list);
		}
		if(!EpicLeaderboard.moneyEarned.isEmpty()){
			List<String> list = new ArrayList<String>();
			for(UUID id : EpicLeaderboard.moneyEarned.keySet()){
				list.add(id.toString()+"="+EpicLeaderboard.moneyEarned.get(id));
			}
			leaderboard.set("Money_Earned", list);
		}
		leaderboard.save(leaderboardfile);

		ArrayList<Vector> blocklist = EpicSystem.getBlockList();
		if(!blocklist.isEmpty()){

			//Reset file
			if(!blockfile.exists()){
				blockfile.createNewFile();
			}else{
				blockfile.delete();
				blockfile.createNewFile();
			}

			//Reload the file
			FileConfiguration block = YamlConfiguration.loadConfiguration(blockfile);

			//Set the block in the file
			for(int i = 0; i < blocklist.size(); i++){
				Vector loc = blocklist.get(i);
				block.set("Blocked."+ loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ(), "");
			}
			
			block.save(blockfile);
		}
		
		saveQuestEntities(isShutDown);
		
		List<EpicPlayer> playersToSave = EpicSystem.getPlayerList();
		if(!playersToSave.isEmpty()){
			
			for(int i = 0; i < playersToSave.size(); i++){

				// Get the file of the player which has to be saved
				EpicPlayer epicPlayer = playersToSave.get(i);
				savePlayer(epicPlayer);
			}			

			System.out.print("[EpicQuest] Saved "  + playersToSave.size() + " player(s).");
		}else{
			System.out.print("[EpicQuest] There are no players to save");
		}
	}
	
	public static void saveQuestEntities(boolean isShutDown) throws IOException{
		HashMap<Entity, QuestEntity> entityMap = QuestEntityHandler.entityList;
		if(!entityMap.isEmpty()){			
			Object[] entityList = entityMap.keySet().toArray();
			for(Object tmp : entityList){
				Entity entity = (Entity)tmp;
				QuestEntity qEntity = QuestEntityHandler.GetQuestEntity(entity);
				String entityName = QuestEntityHandler.getEntityName(entity);
				
				File savefile = new File("plugins" + File.separator + "EpicQuest" + File.separator + "QuestEntities" + File.separator + entityName + ".yml");
				
				//Make the file editable
				FileConfiguration save = YamlConfiguration.loadConfiguration(savefile);
				
				if(QuestEntityHandler.newEntities.contains(qEntity)){
					
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
					
					Location loc = entity.getLocation();
					save.set("World", entity.getWorld().getName());
					save.set("Location", loc.getBlockX()+":"+loc.getBlockY()+":"+loc.getBlockZ());
					
					List<String> questList = qEntity.questList;
					save.set("Quests", questList);
					
					for(String quest : questList){
						save.set("OpeningSentences."+quest, qEntity.openingSentences.get(quest).getSentences());
						save.set("MiddleSentences."+quest, qEntity.middleSentences.get(quest).getSentences());
						save.set("EndingSentences."+quest, qEntity.endingSentences.get(quest).getSentences());
					}
				}
				
				//Save player stuff
				for(EpicPlayer epicPlayer : EpicSystem.getPlayerList()){
					save.set("Players."+epicPlayer.getPlayerID().toString()+".CurrentQuest", qEntity.currentQuest.get(epicPlayer));
					QuestPhase phase = qEntity.questPhases.get(epicPlayer);
					if(phase == null) phase = QuestPhase.INTRO_TALK;
					save.set("Players."+epicPlayer.getPlayerID().toString()+".QuestPhase", phase.toString());
				}
				
				save.save(savefile);
				
				if(isShutDown) entity.remove();
			}
		}
	}

	public static void savePlayer(EpicPlayer epicPlayer){		
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
	}

	/*
	 * Load players
	 */
	public static void load() {

		EpicSystem.setTime(config.getInt("Time"));
		EpicSystem.setSaveTime(config.getInt("Save_Time"));

		//Get the quest signs if possible and go through them
		FileConfiguration sign = YamlConfiguration.loadConfiguration(signfile);
		if(sign.contains("Signs")){
			List<EpicSign> signList = EpicSystem.getSignList();
			Object[] coordslist = sign.getConfigurationSection("Signs").getKeys(false).toArray();
			for(int i = 0; i < coordslist.length; i++){

				//Get coords
				String coords = coordslist[i].toString();
				String[] coordarray = coords.split(":");
				Location loc = new Location(null, Integer.parseInt(coordarray[0]), Integer.parseInt(coordarray[1]), Integer.parseInt(coordarray[2]));

				//Get quest
				String quest = sign.getString("Signs."+coords);
				
				signList.add(new EpicSign(quest, loc));
			}
			
			System.out.print("[EpicQuest]: Succesfully loaded " + signList.size() + " quest signs.");
		}
		
		//Blocked list
		ArrayList<Vector> blocklist = new ArrayList<Vector>();
		if(block.contains("Blocked")){
			Object[] blockarray = block.getConfigurationSection("Blocked").getKeys(false).toArray();
			for(int i = 0; i < blockarray.length; i++){
				String[] blockSplit = ((String) blockarray[i]).split(":");
				Vector loc = new Vector(Integer.parseInt(blockSplit[0]), Integer.parseInt(blockSplit[1]), Integer.parseInt(blockSplit[2]));
				blocklist.add(loc);
			}
			System.out.print("[EpicQuest]: Succesfully loaded " + blockarray.length + " blocks in the block list.");
		}
		EpicSystem.setBlockList(blocklist);
		
		//Announcer
		for(String line : announcer.getStringList("Quest_Amount_Completed")){
			String[] split = line.split("=");
			EpicAnnouncer.questAmountCompletedText.put(split[0], split[1]);
		}
		for(String line : announcer.getStringList("Quest_Completed")){
			String[] split = line.split("=");
			EpicAnnouncer.questCompletedText.put(split[0], split[1]);
		}
		
		//Leaderboards
		for(String line : leaderboard.getStringList("Quests_Completed")){
			String[] split = line.split("=");
			EpicLeaderboard.questsCompleted.put(UUID.fromString(split[0]), Float.parseFloat(split[1]));
		}
		for(String line : leaderboard.getStringList("Tasks_Completed")){
			String[] split = line.split("=");
			EpicLeaderboard.tasksCompleted.put(UUID.fromString(split[0]), Float.parseFloat(split[1]));
		}
		for(String line : leaderboard.getStringList("Money_Earned")){
			String[] split = line.split("=");
			EpicLeaderboard.moneyEarned.put(UUID.fromString(split[0]), Float.parseFloat(split[1]));
		}
		
		//Villagers
		Bukkit.getScheduler().scheduleSyncDelayedTask(EpicMain.getInstance(), new Runnable(){
			@Override
			public void run() {
				loadQuestEntities();
			}
		}, 50);
	}
	
	public static void loadQuestEntities(){
		File folder = new File("plugins" + File.separator + "EpicQuest" + File.separator + "QuestEntities");
        String[] fileNames = folder.list();
        if(fileNames.length > 0){
        	for(String entityName : fileNames){
        		
        		entityName = entityName.replace(".yml", "");
        		
        		File saveFile = new File("plugins" + File.separator + "EpicQuest" + File.separator + "QuestEntities" + File.separator + entityName + ".yml");
        		YamlConfiguration save = YamlConfiguration.loadConfiguration(saveFile);
        		
        		//Location
        		World world = Bukkit.getWorld(save.getString("World"));
        		String[] locationSplit = save.getString("Location").split(":");
        		Location loc = new Location(world, Integer.parseInt(locationSplit[0]), Integer.parseInt(locationSplit[1]), Integer.parseInt(locationSplit[2]));
				
				//Create
				QuestEntityHandler.RemoveLeftoverVillager(entityName, world);
				if(!EpicSystem.useCitizens()) QuestEntityHandler.SpawnVillager(world, loc, entityName);
				
				//Advanced QuestEntity stuff
				Entity entity = QuestEntityHandler.GetEntity(world, entityName);
				QuestEntity qEntity = new QuestEntity(entity);
				QuestEntityHandler.entityList.put(entity, qEntity);
				
				qEntity.questList = save.getStringList("Quests");
				qEntity.originalLocation = loc;
				
				for(String quest : qEntity.questList){
					
					//Load sentences					
					qEntity.openingSentences.put(quest, new SentenceBatch(save.getStringList("OpeningSentences."+quest)));
					qEntity.middleSentences.put(quest, new SentenceBatch(save.getStringList("MiddleSentences."+quest)));
					qEntity.endingSentences.put(quest, new SentenceBatch(save.getStringList("EndingSentences."+quest)));
				}
				
				//Set player stuff
				if(save.contains("Players")){
					Object[] players = save.getConfigurationSection("Players").getKeys(false).toArray();
					for(Object idObj : players){
						UUID id = UUID.fromString((String)idObj);
						EpicPlayer epicPlayer = EpicSystem.getEpicPlayer(id);
						qEntity.currentQuest.put(epicPlayer, save.getString("Players."+id.toString()+".CurrentQuest"));
						qEntity.questPhases.put(epicPlayer, QuestPhase.valueOf(save.getString("Players."+id.toString()+".QuestPhase")));
					}
				}
        	}
        	System.out.print("[EpicQuest] Loaded " + fileNames.length + " Quest Givers.");
        }
	}

	public static void loadPlayer(UUID id){
		
		//System.out.print("Loading player - " + playername);
		EpicPlayer epicPlayer = null;

		//Get the file
		File savefile = new File("plugins" + File.separator + "EpicQuest" + File.separator + "Players" + File.separator + id.toString() + ".yml");
		if(savefile.exists()){
			
			epicPlayer = new EpicPlayer(id);

			//Make the file editable
			FileConfiguration save = YamlConfiguration.loadConfiguration(savefile);

			//Get quests and set it
			if(save.contains("Quest_list")){

				//Get quest numbers
				List<String> questList = save.getStringList("Quest_list");
				for(String questTag : questList){

					//Create the EpicQuests
					EpicQuest epicQuest = new EpicQuest(questTag);

					//Load task progress
					List<EpicQuestTask> taskList = epicQuest.getTasks();
					for(int taskNumber = 0; taskNumber < taskList.size(); taskNumber++){
						int amount = save.getInt("Quest."+questTag+"."+taskNumber);
						taskList.get(taskNumber).ProgressTask(amount, epicPlayer, false);
					}	
					epicPlayer.getQuestList().add(epicQuest);
					epicQuest.setEpicPlayer(epicPlayer);
				}		
			}

			//Get completed quests and set it
			epicPlayer.setQuestsCompleted(save.getStringList("Completed_Quests"));
			
			//Get quests that have a timer running and set it
			if(save.contains("Timed_Quests")){
				List<String> timedQuests = save.getStringList("Timed_Quests");
				for(String questTag : timedQuests){
					epicPlayer.setQuestTimer(questTag, save.getInt("Quest."+questTag+".timer"));
				}				
			}


			//Load stats
			epicPlayer.playerStatistics = new PlayerStatistics(epicPlayer,
					(float)save.getDouble("Stats.Money_Earned", 0), 
					save.getInt("Stats.Quests_Completed", 0), 
					save.getInt("Stats.Dropped", 0), 
					save.getInt("Stats.Quests_Get", 0), 
					save.getInt("Stats.Tasks_Completed", 0));

			//Load daily limit
			epicPlayer.setQuestDailyLeft(save.getInt("Daily_Left", EpicSystem.getDailyLimit()));
			
			EpicSystem.addPlayer(epicPlayer);
		}else{
			EpicSystem.addFirstStart(id);
		}
		
		EpicPlayer p = EpicSystem.getEpicPlayer(id);
		if(EpicSystem.useBook()) p.giveQuestBook();
	}
}
