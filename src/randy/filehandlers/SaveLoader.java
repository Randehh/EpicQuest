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
import org.bukkit.entity.Villager;

import randy.epicquest.EpicPlayer;
import randy.epicquest.EpicQuest;
import randy.epicquest.EpicSign;
import randy.epicquest.EpicSystem;
import randy.epicquest.EpicVillager;
import randy.epicquest.VillagerHandler;

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
	
	static File villagerfile = new File("plugins" + File.separator + "EpicQuest" + File.separator + "villager.yml");
	static FileConfiguration villager = YamlConfiguration.loadConfiguration(villagerfile);


	/*
	 *  Create a object to save all players to whoms data has to be saved
	 */
	public static String playerlist = null;


	/*
	 * Save players
	 */
	public static void save(boolean isShutDown) throws IOException, InvalidConfigurationException {

		System.out.print("Saving...");
		
		List<EpicPlayer> playersToSave = EpicSystem.getPlayerList();

		if(!playersToSave.isEmpty()){
			
			//System.out.print("Players to save: " + playersToSave.size());
			
			for(int i = 0; i < playersToSave.size(); i++){

				// Get the file of the player which has to be saved
				EpicPlayer epicPlayer = playersToSave.get(i);
				savePlayer(epicPlayer);
			}			

			System.out.print("[EpicQuest]: saved " + playerlist.split(", ").length + " player(s).");
		}else{
			System.out.print("There are no players to save");
		}
		
		//Set time
		config.set("Time", EpicSystem.getTime());
		config.set("Save_Time", EpicSystem.getSaveTime());

		config.set("Save_List", playerlist);
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
				signFile.set("Signs." + loc.getBlockX() + "-" + loc.getBlockY() + "-" + loc.getBlockZ(), quest);
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
		
		HashMap<Villager, EpicVillager> villagerMap = VillagerHandler.villagerList;
		if(!villagerMap.isEmpty()){
			
			//Reset file
			if(!villagerfile.exists()){
				villagerfile.createNewFile();
			}else{
				villagerfile.delete();
				villagerfile.createNewFile();
			}
			
			Object[] villagerList = villagerMap.keySet().toArray();
			for(int i = 0; i < villagerList.length; i++){
				Villager tempVil = (Villager)villagerList[i];
				EpicVillager epicVil = VillagerHandler.GetEpicVillager(tempVil);
				String villagerName = tempVil.getCustomName();
				
				Location loc = tempVil.getLocation();
				villager.set("Villager."+villagerName+".World", tempVil.getWorld().getName());
				villager.set("Villager."+villagerName+".Location", loc.getBlockX()+":"+loc.getBlockY()+":"+loc.getBlockZ());
				
				List<Integer> questList = epicVil.questList;
				String questString = "";
				for(int q = 0; q < questList.size(); q++){
					int actualQuest = questList.get(q);
					if(q < questList.size() - 1){
						questString += actualQuest + ",";
					}else{
						questString += actualQuest;
					}
					
					List<String> openingSentences = epicVil.openingSentences.get(q);
					for(int os = 0; os < openingSentences.size(); os++){
						villager.set("Villager."+villagerName+".OpeningSentences."+actualQuest+"."+os, openingSentences.get(os));
					}
					
					List<String> middleSentences = epicVil.middleSentences.get(q);
					for(int ms = 0; ms < openingSentences.size(); ms++){
						villager.set("Villager."+villagerName+".MiddleSentences."+actualQuest+"."+ms, middleSentences.get(ms));
					}
					
					List<String> endingSentences = epicVil.endingSentences.get(q);
					for(int es = 0; es < openingSentences.size(); es++){
						villager.set("Villager."+villagerName+".EndingSentences."+actualQuest+"."+es, endingSentences.get(es));
					}
				}
				villager.set("Villager."+villagerName+".Quests", questString);
				
				if(isShutDown) tempVil.remove();
			}
			
			villager.save(villagerfile);
		}
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
				int taskamount = epicQuest.getTaskAmount();
				for(int task = 0; task < taskamount; task++){
					save.set("Quest."+quest+"."+task, epicQuest.getPlayerTaskProgress(task));
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
			save.set("Timed_Quests", timerqueststring);
		}

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

		//Put name in playerlist
		if(playerlist == null){
			playerlist = playername;
		}else{
			playerlist = playerlist + ", " + playername;
		}
	}

	/*
	 * Load players
	 */
	public static void load() {

		//Count amount of players
		int playercount = 0;

		//Check if a player is saved
		if(config.contains("Save_List") && config.get("Save_List").toString() != ""){

			//Cut the string into an array and go through all players
			String playerlist = config.get("Save_List").toString();
			String[] players = playerlist.split(", ");
			
			//System.out.print("Playerlist: " + playerlist);
			for(int i = 0; i < players.length; i++){
				String playername = players[i];

				loadPlayer(playername);

				//Add one to playercount
				playercount++;
			}
		}

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
				String[] coordarray = coords.split("-");
				Location loc = new Location(null, Integer.parseInt(coordarray[0]), Integer.parseInt(coordarray[1]), Integer.parseInt(coordarray[2]));

				//Get quest
				int quest = sign.getInt("Signs."+coords);
				
				signList.add(new EpicSign(quest, loc));
			}
		}

		ArrayList<Location> blocklist = new ArrayList<Location>();
		if(block.contains("Blocked")){
			Object[] blockarray = block.getConfigurationSection("Blocked").getKeys(false).toArray();
			for(int i = 0; i < blockarray.length; i++){
				String[] blockSplit = ((String) blockarray[i]).split(":");
				Location loc = new Location(null, Integer.parseInt(blockSplit[0]), Integer.parseInt(blockSplit[1]), Integer.parseInt(blockSplit[2]));
				blocklist.add(loc);
			}
		}
		EpicSystem.setBlockList(blocklist);
		
		if(villager.contains("Villager")){
			Object[] villagerarray = villager.getConfigurationSection("Villager").getKeys(false).toArray();
			for(int i = 0; i < villagerarray.length; i++){
				
				//Name
				String villagerName = (String)villagerarray[i];
				
				//Location
				World world = Bukkit.getWorld(villager.getString("Villager."+villagerName+".World"));
				String[] locationSplit = villager.getString("Villager."+villagerName+".Location").split(":");
				Location loc = new Location(world, Integer.parseInt(locationSplit[0]), Integer.parseInt(locationSplit[1]), Integer.parseInt(locationSplit[2]));
				
				//Quests
				List<String> openingSentences = new ArrayList<String>();
				List<String> middleSentences = new ArrayList<String>();
				List<String> endingSentences = new ArrayList<String>();
				List<Integer> questList = new ArrayList<Integer>();
				
				//Create
				VillagerHandler.RemoveLeftoverVillager(villagerName, world);
				VillagerHandler.SpawnVillager(world, loc, villagerName);
				
				//Advanced villager stuff
				EpicVillager epicVillager = VillagerHandler.GetEpicVillager(world, villagerName);
				
				Object[] quests = villager.getConfigurationSection("Villager."+villagerName+".Quests").getKeys(false).toArray();
				for(int q = 0; q < quests.length; q++){
					int questNo = Integer.parseInt((String)quests[q]);
					questList.add(questNo);
					
					//Load sentences
					Object[] osNo = villager.getConfigurationSection("Villager."+villagerName+".OpeningSentences."+questNo).getKeys(false).toArray();
					for(int os = 0; os < osNo.length; os++){
						openingSentences.add(villager.getString("Villager."+villagerName+".OpeningSentences."+questNo+"."+os));
					}
					
					Object[] msNo = villager.getConfigurationSection("Villager."+villagerName+".MiddleSentences."+questNo).getKeys(false).toArray();
					for(int ms = 0; ms < msNo.length; ms++){
						middleSentences.add(villager.getString("Villager."+villagerName+".MiddleSentences."+questNo+"."+ms));
					}
					
					Object[] esNo = villager.getConfigurationSection("Villager."+villagerName+".EndingSentences."+questNo).getKeys(false).toArray();
					for(int es = 0; es < esNo.length; es++){
						endingSentences.add(villager.getString("Villager."+villagerName+".EndingSentences."+questNo+"."+es));
					}
					
					epicVillager.openingSentences.put(questNo, openingSentences);
					epicVillager.middleSentences.put(questNo, middleSentences);
					epicVillager.endingSentences.put(questNo, endingSentences);
				}
				epicVillager.questList = questList;
			}
		}

		System.out.print("[EpicQuest]: loaded the progress of " + playercount + " players.");
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
					int taskamount = epicQuest.getTaskAmount();
					for(int task = 0; task < taskamount; task++){
						int amount = save.getInt("Quest."+quest+"."+task);
						epicQuest.modifyTaskProgress(task, amount, false);
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
			epicPlayer.modifyStatMoneyEarned(save.getInt("Stats.Money_Earned"));
			epicPlayer.modifyStatQuestCompleted(save.getInt("Stats.Quests_Completed"));
			epicPlayer.modifyStatQuestDropped(save.getInt("Stats.Dropped"));
			epicPlayer.modifyStatQuestGet(save.getInt("Stats.Quests_Get"));
			epicPlayer.modifyStatTaskCompleted(save.getInt("Stats.Tasks_Completed"));

			//Load daily limit
			epicPlayer.setQuestDailyLeft(save.getInt("Daily_Left"));
			
			EpicSystem.getPlayerList().add(epicPlayer);
		}
	}
}
