package randy.epicquest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.herocraftonline.heroes.Heroes;

import randy.filehandlers.QuestLoader;
import randy.filehandlers.FileChecker;
import randy.filehandlers.ConfigLoader;
import randy.filehandlers.SaveLoader;
import randy.listeners.InventoryDragListener;
import randy.listeners.ItemDropListener;
import randy.listeners.OpenBookListener;
import randy.listeners.ChatListener;
import randy.listeners.PlayerInteractListener;
import randy.listeners.PlayerJoinListener;
import randy.listeners.SignListener;
import randy.questentities.QuestEntityHandler;
import randy.quests.EpicQuest;
import randy.quests.EpicQuestDatabase;
import randy.quests.EpicQuestTask;
import randy.quests.EpicQuestTask.TaskTypes;
import randy.questtypes.TypeClickBlock;
import randy.questtypes.TypeCraftItem;
import randy.questtypes.TypeDestroy;
import randy.questtypes.TypeEnchant;
import randy.questtypes.TypeGoTo;
import randy.questtypes.TypeKill;
import randy.questtypes.TypeLevelUp;
import randy.questtypes.TypePlace;
import randy.questtypes.TypeRepair;
import randy.questtypes.TypeSmelt;
import randy.questtypes.TypeTalkToVillager;
import randy.questtypes.TypeTame;

public class EpicMain extends JavaPlugin{
	//Set a few variables needed throughout the start-up
	String pluginversion = "3.4";
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
		
		System.out.print(pluginname + " succesfully disabled.");
	}

	@SuppressWarnings("deprecation")
	public void onEnable() {
		instance = this;
		this.saveDefaultConfig();

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

		SaveLoader.load();

		//Check all players and see if it is first start
		Player[] players = getServer().getOnlinePlayers();
		if(players.length > 0){
			for(int i = 0; i < players.length; i++){
				SaveLoader.loadPlayer(players[i].getName());
			}
		}

		//Start timer
		startTimer();

		System.out.print(pluginname + " version " + pluginversion + " enabled.");
	}

	/*
	 * Vault functions
	 */
	private boolean setupPermissions(){
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		return (permission != null);
	}

	private boolean setupEconomy(){
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		return (economy != null);
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

	/*
	 * 
	 * Commands
	 * 
	 */

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String commandName, String[] args){
		if(sender instanceof Player){
			if(commandName.equalsIgnoreCase("q") || commandName.equalsIgnoreCase("quest")){
				Player player = (Player) sender;
				String playername = player.getName();
				EpicPlayer epicPlayer = EpicSystem.getEpicPlayer(playername);

				List<EpicQuestTask> taskList = epicPlayer.getTasksByType(TaskTypes.EXECUTE_COMMAND);

				if(!taskList.isEmpty()){
					StringBuilder fullCommand = new StringBuilder();
					fullCommand.append(commandName);
					for(String arg : args){
						fullCommand.append(" ");
						fullCommand.append(arg);
					}


					for(EpicQuestTask task : taskList){
						if(task.getTaskID().equalsIgnoreCase(fullCommand.toString())){
							task.ProgressTask(1, epicPlayer);
						}
					}
				}

				if(args.length > 0){

					/*
					 * Help command
					 */
					if(args[0].equalsIgnoreCase("help")){
						if(epicPlayer.hasPermission("epicquest.user.help")){
							if(args.length == 1 || (args.length == 2 && args[1] == ""+2)){

								player.sendMessage(ChatColor.GOLD + "[=======  Help list (1/2) =======]");
								player.sendMessage(ChatColor.GOLD + "/q help <number> - Displays a help page.");
								player.sendMessage(ChatColor.GOLD + "/q give <number> - Gives you a quest, quest number optional from questlist.");
								player.sendMessage(ChatColor.GOLD + "/q questbook <page> - Displays all the quests you have.");
								player.sendMessage(ChatColor.GOLD + "/q questlist <page> - Displays available to you.");
								player.sendMessage(ChatColor.GOLD + "/q info <number> - Display info on the quest.");
								player.sendMessage(ChatColor.GOLD + "/q stats <playername> - Display stats on the player.");
								player.sendMessage(ChatColor.GOLD + "/q turnin - Turn in your quests.");
								player.sendMessage(ChatColor.GOLD + "[=======================]");

							}else if(args.length == 2){
								if(Integer.parseInt(args[1]) == 2){

									player.sendMessage(ChatColor.GOLD + "[=======  Help list (2/2) =======]");
									player.sendMessage(ChatColor.GOLD + "/q party - Shows who is in your party.");
									player.sendMessage(ChatColor.GOLD + "/q party questbook - Gets the party questbook.");
									player.sendMessage(ChatColor.GOLD + "/q party invite <playername> - Invites a player to your party.");
									player.sendMessage(ChatColor.GOLD + "/q party kick <playername> - Kicks player from group.");
									player.sendMessage(ChatColor.GOLD + "/q party leader <playername> - Makes another player party leader.");
									player.sendMessage(ChatColor.GOLD + "/q party leave - Leave the party.");
									player.sendMessage(ChatColor.GOLD + "/q party chat - Toggle party chat.");
									player.sendMessage(ChatColor.GOLD + "[=======================]");

								}
							}
						}else{
							player.sendMessage(ChatColor.RED + "You don't have permission to do that.");
						}
						return true;
					}

					/*
					 * Party commands
					 */
					if(args[0].equalsIgnoreCase("party")){
						if(epicPlayer.hasPermission("epicquest.user.party")){

							//If there are more arguments than only party
							if(args.length >= 2){

								//Invite player
								if(args[1].equalsIgnoreCase("invite")){
									if(args.length == 3){
										Player invitedPlayer = Bukkit.getPlayer(args[2]);
										if(invitedPlayer != null){
											if(invitedPlayer != player){
												EpicPlayer invitedEpicPlayer = EpicSystem.getEpicPlayer(invitedPlayer);
												if(invitedEpicPlayer.getParty() == null){

													//Count current party size, including invited members
													int partySize = 1;
													if(epicPlayer.getParty() != null){
														partySize = epicPlayer.getParty().getSize();

														if(!invitationTimer.keySet().isEmpty()){
															EpicPlayer[] playerList = (EpicPlayer[]) (invitationTimer.keySet().toArray());
															for(int i = 0; i < playerList.length; i++){
																if(playerList[i].hasPartyInvitation == epicPlayer){ partySize++; }
															}
														}
													}

													if(partySize != EpicSystem.getMaxPartySize()){
														invitedPlayer.sendMessage(ChatColor.GREEN + player.getName() + " invited you to his party (" + partySize + ").");
														invitedPlayer.sendMessage(ChatColor.GREEN + "Type '/q party accept' to accept the invitation.");
														invitedEpicPlayer.hasPartyInvitation = epicPlayer;

														invitationTimer.put(invitedEpicPlayer, 15);

														player.sendMessage("" + ChatColor.ITALIC + ChatColor.GREEN + "You invited " + invitedEpicPlayer.getPlayerName() + " to your party.");
													}else{
														player.sendMessage(ChatColor.RED + "Your party is already full!");
													}
												}else{
													player.sendMessage(ChatColor.RED + "That player is already in a party.");
												}
											}else{
												player.sendMessage(ChatColor.RED + "You can't invite yourself.");
											}
										}else{
											player.sendMessage(ChatColor.RED + "That player can't be found.");
										}
									}else{
										player.sendMessage("/q party invite <playername>");
									}
								}

								//Accept invitation
								if(args[1].equalsIgnoreCase("accept")){
									if(epicPlayer.hasPartyInvitation != null && invitationTimer.containsKey(epicPlayer)){

										EpicPlayer invitationPlayer = epicPlayer.hasPartyInvitation;
										if(invitationPlayer.getParty() == null){
											new EpicParty(invitationPlayer, epicPlayer);
											epicPlayer.hasPartyInvitation = null;

											invitationPlayer.getPlayer().sendMessage(ChatColor.GREEN + player.getDisplayName() + " has joined your party!");
											player.sendMessage(ChatColor.GREEN + "You have joined " + invitationPlayer.getPlayerName() + "'s party!");
										}else{
											epicPlayer.hasPartyInvitation.getParty().addPlayer(epicPlayer);
											epicPlayer.hasPartyInvitation = null;
										}

										//Remove player from timer
										invitationTimer.remove(epicPlayer);
									}else{
										player.sendMessage(ChatColor.RED + "You don't have a party invitation.");
									}
								}

								//Kick player
								if(args[1].equalsIgnoreCase("kick")){
									if(args.length == 3){
										EpicParty party = epicPlayer.getParty();
										if(party != null){
											if(party.partyLeader == epicPlayer){
												EpicPlayer kickPlayer = EpicSystem.getEpicPlayer(Bukkit.getPlayer(args[2]));
												if(party.getPartyMembers().contains(kickPlayer)){
													party.removePlayer(kickPlayer, true);
												}else{
													player.sendMessage(ChatColor.RED + "That player is not in your party.");
												}
											}else{
												player.sendMessage(ChatColor.RED + "You are not the party leader.");
											}
										}else{
											player.sendMessage(ChatColor.RED + "You are not in a party.");
										}
									}else{
										player.sendMessage("/q party kick <playername>");
									}
								}

								//Change party leader
								if(args[1].equalsIgnoreCase("leader")){
									if(args.length == 3){
										EpicParty party = epicPlayer.getParty();
										if(party != null){
											if(party.partyLeader == epicPlayer){
												EpicPlayer newLeader = EpicSystem.getEpicPlayer(Bukkit.getPlayer(args[2]));
												if(party.getPartyMembers().contains(newLeader)){
													party.setPartyLeader(newLeader);
												}else{
													player.sendMessage(ChatColor.RED + "That player is not in your party.");
												}
											}else{
												player.sendMessage(ChatColor.RED + "You are not the party leader.");
											}
										}else{
											player.sendMessage(ChatColor.RED + "You are not in a party.");
										}
									}else{
										player.sendMessage("/q party leader <playername>");
									}
								}

								//Leave party
								if(args[1].equalsIgnoreCase("leave")){
									EpicParty party = epicPlayer.getParty();
									if(party != null){
										party.removePlayer(epicPlayer, false);
									}else{
										player.sendMessage(ChatColor.RED + "You are not in a party.");
									}
								}

								//Toggle party chat
								if(args[1].equalsIgnoreCase("chat")){
									if(epicPlayer.getParty() != null){
										epicPlayer.partyChat = !epicPlayer.partyChat;
									}else{
										player.sendMessage(ChatColor.RED + "You are not in a party.");
									}	
								}
							}else{

								//Show party formation
								EpicParty party = epicPlayer.getParty();
								if(party != null){
									party.sendPartyMembers(epicPlayer);
								}else{
									player.sendMessage(ChatColor.RED + "You are not in a party.");
								}
							}
						}else{
							player.sendMessage(ChatColor.RED + "You don't have permission to do that.");
						}
						return true;
					}

					/*
					 * Turn in quests command
					 */
					if(args[0].equalsIgnoreCase("turnin")){
						if(epicPlayer.hasPermission("epicquest.user.turnin")){
							if(!epicPlayer.getCompleteableQuest().isEmpty()){
								epicPlayer.completeAllQuests();
							}else{
								player.sendMessage(ChatColor.RED + "There are no quests to turn in.");
							}
						}else{
							player.sendMessage(ChatColor.RED + "You don't have permission to do that.");
						}
						return true;
					}

					/*
					 * Give quest command
					 */
					if(args[0].equalsIgnoreCase("give")){
						if(args.length == 1 || args.length == 2){
							if(epicPlayer.hasPermission("epicquest.user.give")){
								if(args.length == 1){

									//Give random quest							
									if(epicPlayer.canGetQuest()){
										epicPlayer.addQuestRandom();
									}else{
										player.sendMessage(ChatColor.RED + "There are no more quests available.");
									}
								}else{
									//Get quest
									int quest = Integer.parseInt(args[1]);

									//Get all available quests
									List<Integer> availableQuests = epicPlayer.getObtainableQuests();

									if(quest <= availableQuests.size()){

										//Give quest
										epicPlayer.addQuest(new EpicQuest(epicPlayer, availableQuests.get(quest)));

									}else{
										player.sendMessage(ChatColor.RED + "You can't get that quest.");
									}
								}
							}else{
								player.sendMessage(ChatColor.RED + "You don't have permission to do that.");
							}
							return true;
						}
					}

					/*
					 * Drop quest command
					 */
					if(args[0].equalsIgnoreCase("drop") || args[0].equalsIgnoreCase("d")){
						if(args.length == 2){
							if(epicPlayer.hasPermission("epicquest.user.drop")){

								//Get quest list
								List<EpicQuest> questlist = epicPlayer.getQuestList();
								int dispnumber = Integer.parseInt(args[1]);
								if(!questlist.isEmpty() && dispnumber >= 0 && 
										dispnumber < questlist.size()){

									EpicQuest quest = questlist.get(dispnumber);

									//Get quest
									player.sendMessage(ChatColor.GRAY + "Quest '" + quest.getQuestName() + "' succesfully dropped.");
									epicPlayer.modifyStatQuestDropped(1);
									epicPlayer.removeQuest(quest);

								}else{
									player.sendMessage(ChatColor.RED + "You don't have quest " + args[1] + ".");
								}
							}else{
								player.sendMessage(ChatColor.RED + "You don't have permission to do that.");
							}
							return true;
						}
					}

					/*
					 * Get quest book
					 */
					if(args[0].equalsIgnoreCase("questbook") || args[0].equalsIgnoreCase("qb")){
						if(args.length == 1 || args.length == 2){
							if(epicPlayer.hasPermission("epicquest.user.questbook")){

								//Get quest list
								List<EpicQuest> list = epicPlayer.getQuestList();

								if(!list.isEmpty()){

									//Seperate the pages
									int maxpages = 0;
									for(int i = 0; list.size() > (i * 10); i ++){
										maxpages++;
									}

									//Get the page
									int page = 0;
									if(args.length == 2){
										page = Integer.parseInt(args[1]) - 1;
									}

									int displaypage = page + 1;

									if(page < maxpages && page >= 0){
										//Send the messages
										player.sendMessage(ChatColor.GOLD + "[=======  Questbook  " + displaypage + "/" + maxpages + "  =======]");

										/*player.sendMessage("List size: " + list.size());
									player.sendMessage("Quest: " + list.get(page * 10));
									player.sendMessage("Page max:" + displaypage * 10);
									player.sendMessage("Quest # in list: " + page * 10);*/

										for(int quest = page * 10; quest < list.size() && quest < displaypage * 10; quest++){
											String message =  null;
											if(list.get(quest).getPlayerQuestCompleted()){
												message = ChatColor.GREEN + "";
											}else{
												message = ChatColor.RED + "";
											}
											message = quest + ": " + message + list.get(quest).getQuestName();
											player.sendMessage(message);
										}
										player.sendMessage(ChatColor.GOLD + "[============================]");
									}else{
										player.sendMessage(ChatColor.RED + "That quest page doesn't exist.");
									}
								}else{
									player.sendMessage(ChatColor.RED + "You don't have any quests.");
								}
							}else{
								player.sendMessage(ChatColor.RED + "You don't have permission to do that.");
							}
							return true;
						}
					}

					/*
					 * Get quest info
					 */
					if(args[0].equalsIgnoreCase("info")){
						if(epicPlayer.hasPermission("epicquest.user.info")){
							if(args.length == 2){
								int number = Integer.parseInt(args[1]);

								//Get quest list
								List<EpicQuest> list = epicPlayer.getQuestList();

								if(number < list.size()){

									EpicQuest quest = list.get(number);

									//Send quest name and start info
									player.sendMessage(""+ChatColor.GOLD + quest.getQuestName());
									player.sendMessage(""+ChatColor.GRAY + ChatColor.ITALIC + quest.getQuestStart());

									//Get tasks
									for(int task = 0; task < quest.getTasks().size(); task++){
										player.sendMessage(quest.getTasks().get(task).getPlayerTaskProgressText());
									}
								}
							}
							return true;
						}
					}

					/*
					 * Get Questlist
					 */
					if(args[0].equalsIgnoreCase("questlist") || args[0].equalsIgnoreCase("ql")){
						if(args.length == 1 || args.length == 2){
							if(epicPlayer.hasPermission("epicquest.user.questlist")){

								//Get all available quests
								List<Integer> availableQuests = epicPlayer.getObtainableQuests();

								//Seperate the pages
								int maxpages = 0;
								for(int i = 0; availableQuests.size() > (i * 10); i ++){
									maxpages++;
								}

								//Get the page
								int page = 0;
								if(args.length == 2){
									page = Integer.parseInt(args[1]) - 1;
								}

								int displaypage = page + 1;

								if(page < maxpages && page >= 0){
									//Send the messages
									player.sendMessage(ChatColor.GOLD + "[=======  Questlist  " + displaypage + "/" + maxpages + "  =======]");
									for(int quest = page * 10; quest < availableQuests.size() && quest < displaypage * 10; quest++){
										String message = ChatColor.GREEN + "" +  quest + ": " + EpicQuestDatabase.getQuestName(availableQuests.get(quest));
										player.sendMessage(message);
									}
									player.sendMessage(ChatColor.GOLD + "[============================]");
								}else{
									player.sendMessage(ChatColor.RED + "That quest page doesn't exist.");
								}
							}else{
								player.sendMessage(ChatColor.RED + "You don't have permission to do that.");
							}
							return true;
						}
					}


					/*
					 * Get stats command
					 */
					if(args[0].equalsIgnoreCase("stats")){
						if(epicPlayer.hasPermission("epicquest.user.stats")){
							if(args.length == 2){
								Player player2 = Bukkit.getPlayer(args[1]);
								String player2name = player2.getName();
								EpicPlayer epicPlayer2 = EpicSystem.getEpicPlayer(player2name);

								if(epicPlayer2 != null){
									player.sendMessage(ChatColor.YELLOW + "Statistics for player '" + player2name + "'.");
									player.sendMessage(ChatColor.GOLD + "Quests get: " + epicPlayer2.getStatQuestGet() + ".");
									player.sendMessage(ChatColor.GOLD + "Quests finished: " + epicPlayer2.getStatQuestCompleted() + ".");
									player.sendMessage(ChatColor.GOLD + "Quests dropped: " + epicPlayer2.getStatQuestDropped() + ".");
									player.sendMessage(ChatColor.GOLD + economy.currencyNamePlural() + " earned: " + epicPlayer2.getStatMoneyEarned() + ".");
									player.sendMessage(ChatColor.GOLD + "Tasks completed: " + epicPlayer2.getStatTaskCompleted() + ".");
								}else{
									player.sendMessage(ChatColor.RED + "That player doesn't exist!");
								}
							}else if (args.length == 1){
								player.sendMessage(ChatColor.YELLOW + "Statistics for player '" + playername + "'.");
								player.sendMessage(ChatColor.GOLD + "Quests get: " + epicPlayer.getStatQuestGet() + ".");
								player.sendMessage(ChatColor.GOLD + "Quests finished: " + epicPlayer.getStatQuestCompleted() + ".");
								player.sendMessage(ChatColor.GOLD + "Quests dropped: " + epicPlayer.getStatQuestDropped() + ".");
								player.sendMessage(ChatColor.GOLD + economy.currencyNamePlural() + " earned: " + epicPlayer.getStatMoneyEarned() + ".");
								player.sendMessage(ChatColor.GOLD + "Tasks completed: " + epicPlayer.getStatTaskCompleted() + ".");
							}
						}else{
							player.sendMessage(ChatColor.RED + "You don't have permission to do that.");
						}
						return true;
					}

					/*
					 * Leaderboards
					 */
					/*if(args[0].equalsIgnoreCase("leaderboard")){
					if(epicPlayer.hasPermission("epicquest.user.leaderboard")){
						Collections.sort(playerprogress.getQuestList(playername));
					}else{
						player.sendMessage(ChatColor.RED + "You don't have permission to do that.");
					}
					return true;
				}*/

					/*
					 * Admin commmands
					 */

					//Debug stuff
					if(args[0].equalsIgnoreCase("debug")){
						if(epicPlayer.hasPermission("epicquest.admin.debug")){
							if(args[1].equalsIgnoreCase("quests")){
								player.sendMessage("Current quests: " + epicPlayer.getQuestList().toString());
								player.sendMessage("Completed quests: " + epicPlayer.getQuestsCompleted().toString());
							}

							if(args[1].equalsIgnoreCase("name")){
								player.sendMessage("Your name in the system is: " + player.getName());
							}
						}else{
							player.sendMessage(ChatColor.RED + "You don't have permission to do that.");
						}
						return true;
					}

					//Spawn villager
					if(args[0].equalsIgnoreCase("questentity")){
						if(epicPlayer.hasPermission("epicquest.admin.questentity")){

							if(args[1].equalsIgnoreCase("create")){	
								
								if(args.length == 3 && !EpicSystem.useCitizens()){
									player.sendMessage("/q questentity create <name> <questnumber>");
									return true;
								}else if(args.length == 4 && EpicSystem.useCitizens()){
									player.sendMessage("/q questentity create <questnumber>");
									return true;
								}
								
								int quest = Integer.parseInt(args[args.length - 1]);
								
								if(EpicSystem.useCitizens()){
									PlayerInteractListener.createNewQuestEntity = player;
									PlayerInteractListener.createNewQuestEntityQuest = quest;
									player.sendMessage(ChatColor.GREEN + "Right click a Citizen to make it a quest giver.");
								}else{
									String name = "";
									for(int i = 2; i < args.length - 1; i++){
										if(i == args.length - 1)
											name += args[i];
										else
											name += args[i] + " ";
									}
									name = name.trim();
									
									QuestEntityHandler.SpawnVillager(player.getWorld(), player.getLocation(), name);
									QuestEntityHandler.GetQuestEntity(QuestEntityHandler.GetEntity(player.getWorld(), name)).SetBasics(quest);
									
									player.sendMessage(ChatColor.GREEN + "A villager with the name " + name + " has been created with quest " + quest +".");
								}

							}else if(args.length == 3 && args[1].equalsIgnoreCase("remove")){

								//Remove
								String name = args[2];
								name = name.trim();
								if(!QuestEntityHandler.RemoveVillager(player.getWorld(), name)){
									player.sendMessage(ChatColor.RED + "A villager with the name " + name +" has not been found in this world.");
								}else{
									player.sendMessage(ChatColor.GREEN + "The villager with the name " + name + " has been removed.");
								}

							}
						}else{
							player.sendMessage(ChatColor.RED + "You don't have permission to do that.");
						}
						return true;
					}

					//Save
					if(args[0].equalsIgnoreCase("save")){
						if(epicPlayer.hasPermission("epicquest.admin.save")){
							saveAll(false);
						}else{
							player.sendMessage(ChatColor.RED + "You don't have permission to do that.");
						}
						return true;
					}

					//Reload quests
					if(args[0].equalsIgnoreCase("reload")){
						if(epicPlayer.hasPermission("epicquest.admin.reload")){
							EpicQuestDatabase.ClearDatabase();
							QuestLoader.loadQuests();
							EpicQuest.ResetQuestTaskInfo();
							player.sendMessage(ChatColor.GREEN + "Succesfully reloaded the quest database.");
						}else{
							player.sendMessage(ChatColor.RED + "You don't have permission to do that.");
						}
						return true;
					}

					//Quest block
					if(args[0].equalsIgnoreCase("questblock")){
						if(epicPlayer.hasPermission("epicquest.admin.questblock")){
							if(args[1].equalsIgnoreCase("give")){
								if(args[2].equalsIgnoreCase("random")){
									Location loc = player.getTargetBlock(null, 25).getLocation();
									loc.setWorld(null);
									EpicSystem.getSignList().add(new EpicSign(-1, loc));
									player.sendMessage("Questblock created that gives random quests.");
								} else {
									int quest = Integer.parseInt(args[2]);
									Location loc = player.getTargetBlock(null, 25).getLocation();
									loc.setWorld(null);
									EpicSystem.getSignList().add(new EpicSign(quest, loc));
									player.sendMessage("Questblock created that gives quest " + quest + ".");
								}
							} if(args[1].equalsIgnoreCase("turnin")){
								Location loc = player.getTargetBlock(null, 25).getLocation();
								loc.setWorld(null);
								EpicSystem.getSignList().add(new EpicSign(-2, loc));
								player.sendMessage("Questblock created that turns in quests.");
							}
						}else{
							player.sendMessage(ChatColor.RED + "You don't have permission to do that.");
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	private void startTimer(){

		//Start timer, triggers every second
		timerTask = new TimerTask() {
			public void run() {

				//Change time in the config
				EpicSystem.modifyTime(1);
				EpicSystem.modifySaveTime(1);

				//If timer has ran a full day, reset block list, timer and daily quest counters
				if(EpicSystem.getTime() >= 86400){

					//Adjust all quest timers of every player
					List<EpicPlayer> playerList = EpicSystem.getPlayerList();
					for(int i = 0; i < playerList.size(); i ++){
						EpicPlayer epicPlayer = playerList.get(i);
						List<Integer> questList = epicPlayer.getQuestTimerList();
						for(int e = 0; e < questList.size(); e++){
							epicPlayer.checkTimer(questList.get(e), true);
						}

						epicPlayer.setQuestDailyLeft(EpicSystem.getDailyLimit());
					}

					EpicSystem.setTime(0);
					EpicSystem.setBlockList(new ArrayList<Location>());
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
							tempPlayer.hasPartyInvitation.getPlayer().sendMessage(""+ChatColor.ITALIC + ChatColor.RED + tempPlayer.getPlayerName() + " declined your party invitation.");
							tempPlayer.hasPartyInvitation = null;

							tempPlayer.getPlayer().sendMessage(""+ChatColor.ITALIC + ChatColor.RED + "You declined " + tempPlayer.hasPartyInvitation.getPlayerName() + "'s party invitation.");
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

	private void saveAll(boolean isShutDown){
		try {
			SaveLoader.save(isShutDown);

			if(isShutDown){
				EpicSystem.getPlayerList().clear();
			}
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static EpicMain getInstance(){
		return instance;
	}
}
