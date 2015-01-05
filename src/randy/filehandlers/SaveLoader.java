package randy.filehandlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;

import randy.epicquest.EpicPlayer;
import randy.epicquest.EpicSign;
import randy.epicquest.EpicSystem;
import randy.epicquest.EpicMain;
import randy.questentities.QuestEntity;
import randy.questentities.SentenceBatch;
import randy.questentities.QuestEntityHandler;
import randy.questentities.QuestEntity.QuestPhase;
import randy.quests.EpicQuest;
import randy.quests.EpicQuestTask;

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
	
	static File questEntityfile = new File("plugins" + File.separator + "EpicQuest" + File.separator + "questentities.yml");
	static FileConfiguration questEntity = YamlConfiguration.loadConfiguration(questEntityfile);

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
				int quest = sign.getQuest();
				signFile.set("Signs." + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ(), quest);
			}
		}
		signFile.save(signfile);

		ArrayList<Location> blocklist = EpicSystem.getBlockList();
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
				Location loc = blocklist.get(i);
				block.set("Blocked."+ loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ(), "");
			}
			
			block.save(blockfile);
		}
		
		HashMap<Entity, QuestEntity> entityMap = QuestEntityHandler.entityList;
		if(!entityMap.isEmpty()){
			
			//Reset file
			if(!questEntityfile.exists()){
				questEntityfile.createNewFile();
			}else{
				questEntityfile.delete();
				questEntityfile.createNewFile();
			}
			
			Object[] entityList = entityMap.keySet().toArray();
			for(Object tmp : entityList){
				Entity entity = (Entity)tmp;
				QuestEntity qEntity = QuestEntityHandler.GetQuestEntity(entity);
				String entityName = QuestEntityHandler.getEntityName(entity);
				
				if(QuestEntityHandler.newEntities.contains(qEntity)){
					Location loc = entity.getLocation();
					questEntity.set("Entities."+entityName+".World", entity.getWorld().getName());
					questEntity.set("Entities."+entityName+".Location", loc.getBlockX()+":"+loc.getBlockY()+":"+loc.getBlockZ());
					
					List<Integer> questList = qEntity.questList;
					String questString = "";
					for(int q = 0; q < questList.size(); q++){
						int actualQuest = questList.get(q);
						if(q < questList.size() - 1){
							questString += actualQuest + ",";
						}else{
							questString += actualQuest;
						}
						
						questEntity.set("Entities."+entityName+".OpeningSentences."+actualQuest, qEntity.openingSentences.get(actualQuest).getSentences());
						questEntity.set("Entities."+entityName+".MiddleSentences."+actualQuest, qEntity.middleSentences.get(actualQuest).getSentences());
						questEntity.set("Entities."+entityName+".EndingSentences."+actualQuest, qEntity.endingSentences.get(actualQuest).getSentences());
					}
					questEntity.set("Entities."+entityName+".Quests", questString);
				}
				
				//Save player stuff
				for(EpicPlayer epicPlayer : EpicSystem.getPlayerList()){
					questEntity.set("Entities."+entityName+".Players."+epicPlayer.getPlayerName()+".CurrentQuest", qEntity.currentQuest.get(epicPlayer));
					questEntity.set("Entities."+entityName+".Players."+epicPlayer.getPlayerName()+".QuestPhase", qEntity.questPhases.get(epicPlayer).toString());
				}
				
				if(isShutDown) entity.remove();
			}
		}
		
		List<EpicPlayer> playersToSave = EpicSystem.getPlayerList();
		if(!playersToSave.isEmpty()){
			
			for(int i = 0; i < playersToSave.size(); i++){

				// Get the file of the player which has to be saved
				EpicPlayer epicPlayer = playersToSave.get(i);
				savePlayer(epicPlayer);
			}			

			System.out.print("[EpicQuest]: saved "  + playersToSave.size() + " player(s).");
		}else{
			System.out.print("There are no players to save");
		}
		
		questEntity.save(questEntityfile);
	}

	public static void savePlayer(EpicPlayer epicPlayer){		
		String playername = epicPlayer.getPlayerName();
		File savefile = new File("plugins" + File.separator + "EpicQuest" + File.separator + "Players" + File.separator + playername + ".yml");

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
		String queststring = null;
		
		if(!questlist.isEmpty()){
			for(int e = 0; e < questlist.size(); e++){
				EpicQuest epicQuest = questlist.get(e);
				int quest = epicQuest.getQuestNo();
				List<EpicQuestTask> taskList = epicQuest.getTasks();
				for(int taskNumber = 0; taskNumber < taskList.size(); taskNumber++){
					save.set("Quest."+quest+"."+taskNumber, taskList.get(taskNumber).getTaskProgress());
				}

				//Save the list of quests the player has
				if(queststring == null){
					queststring = ""+questlist.get(e).getQuestNo();
				}else{
					queststring = queststring + ", " + questlist.get(e).getQuestNo();
				}
			}
			save.set("Quest_list", queststring);
		}

		//Save the list of completed quests the player has
		List<Integer> completedquestlist = epicPlayer.getQuestsCompleted();
		String completedqueststring = null;
		for(int e = 0; e < completedquestlist.size(); e++){

			if(!completedquestlist.isEmpty()){
				if(completedqueststring == null){
					completedqueststring = ""+completedquestlist.get(e);
				}else{
					completedqueststring = completedqueststring + ", " + completedquestlist.get(e);
				}
				save.set("Completed_Quests", completedqueststring);
			}
		}

		//Save list of quests that have timers running
		List<Integer> timerquestlist = epicPlayer.getQuestTimerList();
		String timerqueststring = null;
		for(int e = 0; e < timerquestlist.size(); e++){

			//Update timer
			epicPlayer.checkTimer(e, true);

			int quest = timerquestlist.get(e);
			if(timerqueststring == null){
				timerqueststring = ""+quest;
			}else{
				timerqueststring = timerqueststring + ", " + quest;
			}

			//Save the timer for the quests
			save.set("Quest."+quest+".timer", timerquestlist.get(e));
		}
		save.set("Timed_Quests", timerqueststring);

		//Save stats
		save.set("Stats.Money_Earned", epicPlayer.getStatMoneyEarned());
		save.set("Stats.Quests_Completed", epicPlayer.getStatQuestCompleted());
		save.set("Stats.Quests_Dropped", epicPlayer.getStatQuestDropped());
		save.set("Stats.Quests_Get", epicPlayer.getStatQuestGet());
		save.set("Stats.Tasks_Completed", epicPlayer.getStatTaskCompleted());

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
				int quest = sign.getInt("Signs."+coords);
				
				signList.add(new EpicSign(quest, loc));
			}
			
			System.out.print("[EpicQuest]: Succesfully loaded " + signList.size() + " quest signs.");
		}

		ArrayList<Location> blocklist = new ArrayList<Location>();
		if(block.contains("Blocked")){
			Object[] blockarray = block.getConfigurationSection("Blocked").getKeys(false).toArray();
			for(int i = 0; i < blockarray.length; i++){
				String[] blockSplit = ((String) blockarray[i]).split(":");
				Location loc = new Location(null, Integer.parseInt(blockSplit[0]), Integer.parseInt(blockSplit[1]), Integer.parseInt(blockSplit[2]));
				blocklist.add(loc);
			}
			System.out.print("[EpicQuest]: Succesfully loaded " + blockarray.length + " blocks in the block list.");
		}
		EpicSystem.setBlockList(blocklist);
		
		
		//Villagers
		Bukkit.getScheduler().scheduleSyncDelayedTask(EpicMain.getInstance(), new Runnable(){
			@Override
			public void run() {
				if(questEntity.contains("Entities")){
					Object[] entitiesArray = questEntity.getConfigurationSection("Entities").getKeys(false).toArray();
					for(Object tmp : entitiesArray){
						//Name
						String entityName = (String)tmp;
						
						//Location
						World world = Bukkit.getWorld(questEntity.getString("Entities."+entityName+".World"));
						String[] locationSplit = questEntity.getString("Entities."+entityName+".Location").split(":");
						Location loc = new Location(world, Integer.parseInt(locationSplit[0]), Integer.parseInt(locationSplit[1]), Integer.parseInt(locationSplit[2]));
						
						//Quests
						List<Integer> questList = new ArrayList<Integer>();
						
						//Create
						QuestEntityHandler.RemoveLeftoverVillager(entityName, world);
						if(!EpicSystem.useCitizens()) QuestEntityHandler.SpawnVillager(world, loc, entityName);
						
						//Advanced villager stuff
						Entity entity = QuestEntityHandler.GetEntity(world, entityName);
						QuestEntity qEntity = new QuestEntity(entity);
						QuestEntityHandler.entityList.put(entity, qEntity);
						
						Object[] quests = questEntity.getConfigurationSection("Entities."+entityName+".OpeningSentences").getKeys(false).toArray();
						for(int q = 0; q < quests.length; q++){
							int questNo = Integer.parseInt((String)quests[q]);
							questList.add(questNo);
							
							//Load sentences					
							qEntity.openingSentences.put(questNo, new SentenceBatch(questEntity.getStringList("Entities."+entityName+".OpeningSentences."+questNo)));
							qEntity.middleSentences.put(questNo, new SentenceBatch(questEntity.getStringList("Entities."+entityName+".MiddleSentences."+questNo)));
							qEntity.endingSentences.put(questNo, new SentenceBatch(questEntity.getStringList("Entities."+entityName+".EndingSentences."+questNo)));
						}
						
						qEntity.questList = questList;
						
						//Set player stuff
						Object[] players = questEntity.getConfigurationSection("Entities."+entityName+".Players").getKeys(false).toArray();
						for(int p = 0; p < players.length; p++){
							String playername = (String)players[p];
							EpicPlayer epicPlayer = EpicSystem.getEpicPlayer(playername);
							qEntity.currentQuest.put(epicPlayer, questEntity.getInt("Entities."+entityName+".Players."+playername+".CurrentQuest"));
							qEntity.questPhases.put(epicPlayer, QuestPhase.valueOf(questEntity.getString("Entities."+entityName+".Players."+playername+".QuestPhase")));
						}
					}
					System.out.print("[EpicQuest]: Succesfully loaded " + entitiesArray.length + " Quest Givers.");
				}
			}
		}, 50);

		
	}

	public static void loadPlayer(String playername){
		
		//System.out.print("Loading player - " + playername);
		EpicPlayer epicPlayer = null;

		//Get the file
		File savefile = new File("plugins" + File.separator + "EpicQuest" + File.separator + "Players" + File.separator + playername + ".yml");
		if(savefile.exists()){
			
			epicPlayer = new EpicPlayer(playername);

			//Make the file editable
			FileConfiguration save = YamlConfiguration.loadConfiguration(savefile);

			//Get quests and set it
			if(save.contains("Quest_list")){

				//Get quest numbers
				String[] temp = save.getString("Quest_list").split(", ");
				for(int e = 0; e < temp.length; e++){

					//Create the EpicQuests
					int quest = Integer.parseInt(temp[e]);
					EpicQuest epicQuest = new EpicQuest(epicPlayer, quest);

					//Load task progress
					List<EpicQuestTask> taskList = epicQuest.getTasks();
					for(int taskNumber = 0; taskNumber < taskList.size(); taskNumber++){
						int amount = save.getInt("Quest."+quest+"."+taskNumber);
						taskList.get(taskNumber).ProgressTask(amount, null);
					}	
					epicPlayer.getQuestList().add(epicQuest);
				}		
			}

			//Get completed quests and set it
			if(save.contains("Completed_Quests")){
				String[] temp2 = save.get("Completed_Quests").toString().split(", ");
				ArrayList<Integer> completedquestlist = new ArrayList<Integer>();
				for(int e = 0; e < temp2.length; e++){
					int quest = Integer.parseInt(temp2[e]);
					completedquestlist.add(quest);
				}

				epicPlayer.setQuestsCompleted(completedquestlist);

			}

			//Get quests that have a timer running and set it
			if(save.contains("Timed_Quests")){
				String[] temp2 = save.get("Timed_Quests").toString().split(", ");
				ArrayList<Integer> timedquestlist = new ArrayList<Integer>();
				for(int e = 0; e < temp2.length; e++){
					int quest = Integer.parseInt(temp2[e]);
					timedquestlist.add(quest);

					//Get quest timer
					int time = save.getInt("Quest."+quest+".timer");
					epicPlayer.setQuestTimer(quest, time);
				}					
			}


			//Load stats
			epicPlayer.modifyStatMoneyEarned(save.getInt("Stats.Money_Earned", 0));
			epicPlayer.modifyStatQuestCompleted(save.getInt("Stats.Quests_Completed", 0));
			epicPlayer.modifyStatQuestDropped(save.getInt("Stats.Dropped", 0));
			epicPlayer.modifyStatQuestGet(save.getInt("Stats.Quests_Get", 0));
			epicPlayer.modifyStatTaskCompleted(save.getInt("Stats.Tasks_Completed", 0));

			//Load daily limit
			epicPlayer.setQuestDailyLeft(save.getInt("Daily_Left", EpicSystem.getDailyLimit()));
			
			EpicSystem.addPlayer(epicPlayer);
		}else{
			EpicSystem.addFirstStart(playername);
		}
		
		EpicPlayer p = EpicSystem.getEpicPlayer(playername);
		if(EpicSystem.useBook()) p.giveQuestBook();
	}
}
