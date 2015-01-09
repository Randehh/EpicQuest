package randy.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import randy.filehandlers.SaveLoader;

public class EpicSystem {
	
	public static Random random = new Random();
	
	public static HashMap<UUID, Player> idMap = new HashMap<UUID, Player>();
	private static List<EpicPlayer> playerList = new ArrayList<EpicPlayer>();
	public static List<EpicSign> signList = new ArrayList<EpicSign>();
	private static ArrayList<Vector> blockedList = new ArrayList<Vector>();
	public static HashMap<Location, EpicPlayer> furnaceList = new HashMap<Location, EpicPlayer>();
	
	static int questLimit = 10;
	static int time = 0;
	static int startTime = 0;
	static int dailyLimit = 10;
	static int saveTime = 0;
	static int maxPartySize = 5;
	private final static double version = 3.5;
	static boolean usePermissions = false;
	static boolean useBook = true;
	static boolean useHeroes = false;
	static boolean useCitizens = false;
	static boolean useBarAPI = false;
	static boolean enabledAnnouncer = true;
	static boolean enabledMoneyRewards = true;
	
	public static void setQuestLimit(int limit){ questLimit = limit; }
	public static void setTime(int newTime){ time = newTime; startTime = newTime; }
	public static void setDailyLimit(int limit){ dailyLimit = limit; }
	public static void setPlayerList(List<EpicPlayer> newPlayerList) { playerList = newPlayerList; }
	public static void setSignList(List<EpicSign> newSignList) { signList = newSignList; }
	public static void setSaveTime(int time){ saveTime = time; }
	public static void setBlockList(ArrayList<Vector> newBlockList) { blockedList = newBlockList; }
	public static void setMaxPartySize(int size) { maxPartySize = size; }
	public static void setUsePermissions(boolean use) { usePermissions = use; }
	public static void setUseBook(boolean use) { useBook = use; }
	public static void setUseHeroes(boolean use) { useHeroes = use; }
	public static void setUseCitizens(boolean use) { useCitizens = use; }
	public static void setUseBarAPI(boolean use){ useBarAPI = use; }
	public static void setEnabledAnnouncer(boolean enabled) { enabledAnnouncer = enabled; }
	public static void setEnabledMoneyRewards(boolean enabled) { enabledMoneyRewards = enabled; }
	
	public static int getQuestLimit(){ return questLimit; }
	public static int getTime() { return time; }
	public static int getDailyLimit() { return dailyLimit; }
	public static List<EpicPlayer> getPlayerList() { return playerList; }
	public static List<EpicSign> getSignList() { return signList; }
	public static int getSaveTime(){ return saveTime; }
	public static int getStartTime() { return startTime; }
	public static ArrayList<Vector> getBlockList(){ return blockedList; }
	public static int getMaxPartySize() { return maxPartySize; }
	public static boolean usePermissions() { return usePermissions; }
	public static boolean useBook( ) { return useBook; }
	public static boolean useHeroes() { return useHeroes; }
	public static boolean useCitizens() { return useCitizens; }
	public static boolean useBarAPI(){ return useBarAPI; }
	public static boolean enabledAnnouncer(){ return enabledAnnouncer; }
	public static boolean enabledMoneyRewards(){ return enabledMoneyRewards; }
	
	public static void modifyTime(int newTime) { time += newTime; }
	public static void modifySaveTime(int newSaveTime) { saveTime += newSaveTime; }
	
	public static EpicPlayer getEpicPlayer(UUID id){
		
		//Search list for the player
		for(int i = 0; i < playerList.size(); i++){
			if(playerList.get(i).getPlayerID().equals(id)){ return playerList.get(i); }
		}
		
		//Player is not yet loaded or doesn't exist
		SaveLoader.loadPlayer(id);
		if(playerList.get(playerList.size() - 1).getPlayerID().equals(id)){ return playerList.get(playerList.size() - 1); }
		
		//Player doesn't exist
		return null;
	}
	
	public static List<EpicPlayer> getEpicPlayerList(){
		return playerList;
	}
	
	public static EpicPlayer getEpicPlayer(Player player){
		return getEpicPlayer(player.getUniqueId());
	}
	
	public static Player getPlayerFromID(UUID id){
		return idMap.get(id);
	}
	
	public static List<Object> getLeaderboard(){
		//TODO: Create self rearranging List so that index 0 will have EpicPlayer with greatest Quests completed and index (index.length - 1) with lowest
		
		return new ArrayList<Object>();
	}
	
	public static double getVersion(){
		return version;
	}
	
	public static void addPlayer(EpicPlayer player){
		if(!playerList.contains(player)) playerList.add(player);
	}
	
	public static void addFirstStart(UUID id){
		
		File savefile = new File("plugins" + File.separator + "EpicQuest" + File.separator + "Players" + File.separator + id.toString() + ".yml");

		if(!savefile.exists()){
			EpicPlayer epicPlayer = new EpicPlayer(id);
			EpicSystem.addPlayer(epicPlayer);
		}
	}
}
