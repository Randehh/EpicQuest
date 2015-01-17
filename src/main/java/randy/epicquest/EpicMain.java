package main.java.randy.epicquest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import main.java.randy.commands.CommandListener;
import main.java.randy.engine.EpicPlayer;
import main.java.randy.engine.EpicSystem;
import main.java.randy.engine.Utils;
import main.java.randy.filehandlers.ConfigLoader;
import main.java.randy.filehandlers.FileChecker;
import main.java.randy.filehandlers.QuestLoader;
import main.java.randy.filehandlers.SaveLoader;
import main.java.randy.listeners.ChatListener;
import main.java.randy.listeners.DeathListener;
import main.java.randy.listeners.InventoryDragListener;
import main.java.randy.listeners.ItemDropListener;
import main.java.randy.listeners.OpenBookListener;
import main.java.randy.listeners.PlayerInteractListener;
import main.java.randy.listeners.PlayerJoinListener;
import main.java.randy.listeners.SignListener;
import main.java.randy.questentities.QuestEntity;
import main.java.randy.questentities.QuestEntityHandler;
import main.java.randy.questtypes.TypeClickBlock;
import main.java.randy.questtypes.TypeCraftItem;
import main.java.randy.questtypes.TypeDestroy;
import main.java.randy.questtypes.TypeEnchant;
import main.java.randy.questtypes.TypeGoTo;
import main.java.randy.questtypes.TypeKill;
import main.java.randy.questtypes.TypeLevelUp;
import main.java.randy.questtypes.TypePlace;
import main.java.randy.questtypes.TypeRepair;
import main.java.randy.questtypes.TypeSmelt;
import main.java.randy.questtypes.TypeTalkToVillager;
import main.java.randy.questtypes.TypeTame;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.mcstats.Metrics;

import com.herocraftonline.heroes.Heroes;

public class EpicMain extends JavaPlugin{
	//Set a few variables needed throughout the start-up
	String pluginversion;
	String pluginname = "EpicQuest";
	static Plugin epicQuestPlugin = Bukkit.getPluginManager().getPlugin("EpicQuest");
	public static Permission permission = null;
	public static Economy economy = null;
	public static Heroes heroes = null;
	private static EpicMain instance;

	//Set the event classes
	private final PlayerJoinListener joinListener = new PlayerJoinListener();
	private final TypeKill killListener = new TypeKill();
	private final TypeTame tameListener = new TypeTame();
	private final TypeDestroy destroyListener = new TypeDestroy();
	private final TypePlace placeListener = new TypePlace();
	private final TypeEnchant enchantListener = new TypeEnchant();
	private final TypeLevelUp levelupListener = new TypeLevelUp();
	private final SignListener signChangeListener = new SignListener();
	private final PlayerInteractListener playerInteractEntityListener = new PlayerInteractListener();
	private final ChatListener partyMessageListener = new ChatListener();
	private final OpenBookListener openBook = new OpenBookListener();
	private final TypeSmelt smeltListener = new TypeSmelt();
	private final InventoryDragListener inventoryDrag = new InventoryDragListener();
	private final TypeCraftItem itemCraftListener = new TypeCraftItem();
	private final ItemDropListener itemDropListener = new ItemDropListener();
	private final TypeRepair repairListener = new TypeRepair();
	private final TypeTalkToVillager talkToVillagerListener = new TypeTalkToVillager();
	private final TypeGoTo goToListener = new TypeGoTo();
	private final TypeClickBlock clickBlockListener = new TypeClickBlock();
	private final DeathListener deathListener = new DeathListener();
	public final CommandListener commandListener = new CommandListener();

	//Party timers
	Timer timer = new Timer();
	TimerTask timerTask;
	HashMap<EpicPlayer, Integer> invitationTimer = new HashMap<EpicPlayer, Integer>();

	public void onDisable() {
		saveAll(true);

		if(timerTask != null){
			timerTask.cancel();
		}
		
		timer.cancel();
		
		System.out.print("[" + pluginname + "] succesfully disabled.");
	}

	public void onEnable() {
		instance = this;
		this.saveDefaultConfig();
		pluginversion = this.getDescription().getVersion();

		/*
		 * Set events
		 *
		 * Example of registering events
		 * getServer().getPluginManager().registerEvents(killListener, this);
		 * 
		 */
		getServer().getPluginManager().registerEvents(joinListener, this);
		getServer().getPluginManager().registerEvents(killListener, this);
		getServer().getPluginManager().registerEvents(tameListener, this);
		getServer().getPluginManager().registerEvents(destroyListener, this);
		getServer().getPluginManager().registerEvents(enchantListener, this);
		getServer().getPluginManager().registerEvents(placeListener, this);
		getServer().getPluginManager().registerEvents(levelupListener, this);
		getServer().getPluginManager().registerEvents(signChangeListener, this);
		getServer().getPluginManager().registerEvents(playerInteractEntityListener, this);
		getServer().getPluginManager().registerEvents(partyMessageListener, this);
		getServer().getPluginManager().registerEvents(smeltListener, this);
		getServer().getPluginManager().registerEvents(inventoryDrag, this);
		getServer().getPluginManager().registerEvents(openBook, this);
		getServer().getPluginManager().registerEvents(itemCraftListener, this);
		getServer().getPluginManager().registerEvents(itemDropListener, this);
		getServer().getPluginManager().registerEvents(repairListener, this);
		getServer().getPluginManager().registerEvents(talkToVillagerListener, this);
		getServer().getPluginManager().registerEvents(goToListener, this);
		getServer().getPluginManager().registerEvents(clickBlockListener, this);
		getServer().getPluginManager().registerEvents(deathListener, this);
		
		this.getCommand("q").setExecutor(commandListener);
		
		/*
		 * Check all files before trying to load the plugin
		 */

		try {
			if(!FileChecker.checkFiles()){
				Bukkit.getPluginManager().disablePlugin(this);
			}
		} catch (SecurityException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		}
		
		/*
		 * Load configs
		 */
		ConfigLoader.loadConfig();
		QuestLoader.loadQuests();

		//Set up these first before loading the rest
		setupPermissions();
		setupEconomy();
		setupHeroes();
		setupCitizens();
		setupBarAPI();
		setupSQLibrary();

		SaveLoader.load();

		//Check all players and see if it is first start
		Player[] players = Utils.getOnlinePlayers();
		if(players.length > 0){
			for(int i = 0; i < players.length; i++){
				SaveLoader.loadPlayer(players[i].getUniqueId());
				
				//Set basic stuff for villager
				for(QuestEntity qEntity : QuestEntityHandler.GetQuestEntityList()){
					qEntity.SetFirstInteraction(EpicSystem.getEpicPlayer(players[i]));
				}
			}
		}

		//Start timer
		startTimer();
		
		//Start streaming metrics data
		try {
			Metrics metrics = new Metrics(this);
			MetricsHandler.createGraphs(metrics);
		    metrics.start();
		} catch (IOException e) {
			e.printStackTrace();
	    }
		
		System.out.print(pluginname + " version " + pluginversion + " enabled.");
	}

	/*
	 * Vault functions
	 */
	private boolean setupPermissions(){
		if(!EpicSystem.usePermissions()) return true;
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null && permissionProvider.getProvider().isEnabled()) {
			permission = permissionProvider.getProvider();
			System.out.print("[EpicQuest]: Successfully hooked into Permissions!");
		}else{
			EpicSystem.setUsePermissions(false);
			System.out.print("[EpicQuest]: Permissions is enabled in the config, but isn't found! Disabling Permissions support.");
		}
		return (permission != null);
	}

	private boolean setupEconomy(){
		if(!EpicSystem.enabledMoneyRewards()) return true;
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null && economyProvider.getProvider().isEnabled()) {
			System.out.print("[EpicQuest]: Succesfully hooked into Economy!");
			economy = economyProvider.getProvider();
			return false;
		}else{
			//Economy not used or found
			EpicSystem.setEnabledMoneyRewards(false);
			System.out.print("[EpicQuest]: Economy is enabled in the config, but isn't found! Disabling Economy support.");
		}
		return true;
	}

	private boolean setupHeroes(){
		if(!EpicSystem.useHeroes()) return true;
		if(Bukkit.getPluginManager().getPlugin("Heroes") == null || !Bukkit.getPluginManager().getPlugin("Heroes").isEnabled()){
			System.out.print("[EpicQuest]: Heroes is enabled in the config, but isn't found! Disabling Heroes support.");
			EpicSystem.setUseHeroes(false);
			return false;
		}else{
			heroes = (Heroes)Bukkit.getPluginManager().getPlugin("Heroes");
			System.out.print("[EpicQuest]: Successfully hooked into Heroes!");
		}
		return true;
	}

	private boolean setupCitizens(){
		if(!EpicSystem.useCitizens()) return true;

		if(Bukkit.getPluginManager().getPlugin("Citizens") == null || !Bukkit.getPluginManager().getPlugin("Citizens").isEnabled()){
			System.out.print("[EpicQuest]: Citizens is enabled in the config, but isn't found! Disabling Citizens support.");
			EpicSystem.setUseCitizens(false);
			return false;
		}else{
			System.out.print("[EpicQuest]: Successfully hooked into Citizens!");
		}
		return true;
	}
	
	private boolean setupBarAPI(){
		if(!EpicSystem.useBarAPI()) return true;

		if(Bukkit.getPluginManager().getPlugin("BarAPI") == null || !Bukkit.getPluginManager().getPlugin("BarAPI").isEnabled()){
			System.out.print("[EpicQuest]: BarAPI is enabled in the config, but isn't found! Disabling BarAPI support.");
			EpicSystem.setUseBarAPI(false);
			return false;
		}else{
			System.out.print("[EpicQuest]: Successfully hooked into BarAPI!");
		}
		return true;
	}
	
	private boolean setupSQLibrary(){
		if(!EpicSystem.useSQLDatabase()) return true;

		if(Bukkit.getPluginManager().getPlugin("SQLibrary") == null || !Bukkit.getPluginManager().getPlugin("SQLibrary").isEnabled()){
			System.out.print("[EpicQuest]: SQLibrary is enabled in the config, but isn't found! Using yml as player storage.");
			EpicSystem.setUseSQLDatabase(false);
			return false;
		}else{
			System.out.print("[EpicQuest]: Successfully hooked into BarAPI!");
		}
		return true;
	}

	/*
	 * 
	 * Commands
	 * 
	 */

	

	private void startTimer(){

		//Start timer, triggers every second
		timerTask = new TimerTask() {
			public void run() {

				//Change time in the config
				EpicSystem.modifyTime(1);
				EpicSystem.modifySaveTime(1);
				EpicSystem.modifyGlobalTime(1);

				//If timer has ran a full day, reset block list, timer and daily quest counters
				if(EpicSystem.getTime() >= 86400){

					//Adjust all quest timers of every player
					List<EpicPlayer> playerList = EpicSystem.getPlayerList();
					for(int i = 0; i < playerList.size(); i ++){
						EpicPlayer epicPlayer = playerList.get(i);
						epicPlayer.setQuestDailyLeft(EpicSystem.getDailyLimit());
					}

					EpicSystem.setTime(0);
					EpicSystem.setBlockList(new ArrayList<Vector>());
				}

				//If timer has run for 5 minutes, save all
				if(EpicSystem.getSaveTime() >= 300){
					saveAll(false);
					EpicSystem.setSaveTime(0);
				}

				//Count down the invitation timers
				if(!invitationTimer.isEmpty()){
					Object[] playerList = invitationTimer.keySet().toArray();
					for(int i = 0; i < playerList.length; i++){
						EpicPlayer tempPlayer = (EpicPlayer)playerList[i];
						invitationTimer.put(tempPlayer, invitationTimer.get(tempPlayer) - 1);

						if(invitationTimer.get(tempPlayer) == 0){
							tempPlayer.hasPartyInvitation.getPlayer().sendMessage(""+ChatColor.ITALIC + ChatColor.RED + tempPlayer.getPlayer().getName() + " declined your party invitation.");
							tempPlayer.hasPartyInvitation = null;

							tempPlayer.getPlayer().sendMessage(""+ChatColor.ITALIC + ChatColor.RED + "You declined " + tempPlayer.hasPartyInvitation.getPlayer().getName() + "'s party invitation.");
							invitationTimer.remove(tempPlayer);
						}
					}
				}

				//Move villagers back
				QuestEntityHandler.MoveVillagersBack();
			}
		};
		timer.schedule(timerTask, 1000, 1000);
	}

	public void saveAll(boolean isShutDown){
		try {
			SaveLoader.save(isShutDown);

			if(isShutDown){
				EpicSystem.getPlayerList().clear();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e){
			e.printStackTrace();
		}
	}

	public static EpicMain getInstance(){
		return instance;
	}
}
