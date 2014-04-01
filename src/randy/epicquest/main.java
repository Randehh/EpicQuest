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

import randy.filehandlers.QuestLoader;
import randy.filehandlers.FileChecker;
import randy.filehandlers.ConfigLoader;
import randy.filehandlers.SaveLoader;
import randy.listeners.InventoryDrag;
<<<<<<< HEAD
import randy.listeners.OpenBook;
import randy.listeners.PartyMessage;
import randy.listeners.TypeCraftItem;
=======
import randy.listeners.PartyMessage;
>>>>>>> 8a82a9e34f4bbc9b0780432073f9ac9f1f8fe45e
import randy.listeners.TypePlayerJoin;
import randy.listeners.TypeDestroy;
import randy.listeners.TypeEnchant;
import randy.listeners.TypeKill;
import randy.listeners.TypeLevelUp;
import randy.listeners.TypePlace;
import randy.listeners.TypePlayerInteract;
import randy.listeners.TypeSignChange;
import randy.listeners.TypeSmelt;
import randy.listeners.TypeTame;

public class main extends JavaPlugin{


	//Set a few variables needed throughout the start-up
	String pluginversion = "3.2";
	String pluginname = "EpicQuest";
	static Plugin epicQuestPlugin = Bukkit.getPluginManager().getPlugin("EpicQuest");
	public static Permission permission = null;
	public static Economy economy = null;

	//Set the event classes
	private final TypePlayerJoin joinListener = new TypePlayerJoin();
	private final TypeKill killListener = new TypeKill();
	private final TypeTame tameListener = new TypeTame();
	private final TypeDestroy destroyListener = new TypeDestroy();
	private final TypePlace placeListener = new TypePlace();
	private final TypeEnchant enchantListener = new TypeEnchant();
	private final TypeLevelUp levelupListener = new TypeLevelUp();
	private final TypeSignChange signChangeListener = new TypeSignChange();
<<<<<<< HEAD
	private final TypePlayerInteract playerInteractEntityListener = new TypePlayerInteract();
	private final PartyMessage partyMessageListener = new PartyMessage();
	private final OpenBook openBook = new OpenBook();
	private final TypeSmelt smeltListener = new TypeSmelt();
	private final InventoryDrag inventoryDrag = new InventoryDrag();
	private final TypeCraftItem itemCraftListenever = new TypeCraftItem();
=======
	private final TypePlayerInteractEntity playerInteractEntityListener = new TypePlayerInteractEntity();
	private final PartyMessage partyMessageListener = new PartyMessage();
	private final TypeSmelt smeltListener = new TypeSmelt();
	private final InventoryDrag inventoryDrag = new InventoryDrag();
>>>>>>> 8a82a9e34f4bbc9b0780432073f9ac9f1f8fe45e
	
	//Party timers
	HashMap<EpicPlayer, Integer> invitationTimer = new HashMap<EpicPlayer, Integer>();

	public void onDisable() {
		saveAll(true);
		System.out.print(pluginname + " succesfully disabled.");
	}

	public void onEnable() {

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
<<<<<<< HEAD
		getServer().getPluginManager().registerEvents(openBook, this);
		getServer().getPluginManager().registerEvents(itemCraftListenever, this);
=======
>>>>>>> 8a82a9e34f4bbc9b0780432073f9ac9f1f8fe45e

		/*
		 * Check all files before trying to load the plugin
		 */

		try {
			if(!FileChecker.checkFiles()){
				Bukkit.getPluginManager().disablePlugin(epicQuestPlugin);
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		 * Load configs
		 */
		ConfigLoader.loadConfig();
		QuestLoader.loadQuests();
		SaveLoader.load();

		//Check all players and see if it is first start
		Player[] players = getServer().getOnlinePlayers();
		if(players.length > 0){
			for(int i = 0; i < players.length; i++){
				EpicSystem.addFirstStart(players[i].getName());
			}
		}

		/*
		 * Setup economy and permissions
		 */
		setupPermissions();
		setupEconomy();

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

	/*
	 * 
	 * Commands
	 * 
	 */

	public boolean onCommand(CommandSender sender, Command command, String commandName, String[] args){
		if(sender instanceof Player){
			if(commandName.equalsIgnoreCase("q") || commandName.equalsIgnoreCase("quest")){
				Player player = (Player) sender;
				String playername = player.getName();
				EpicPlayer epicPlayer = EpicSystem.getEpicPlayer(playername);
				
				if(args.length > 0){

					/*
					 * Help command
					 */
					if(args[0].equalsIgnoreCase("help")){
						if(player.hasPermission("epicquest.user.help")){
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
						if(player.hasPermission("epicquest.user.party")){
							
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
						if(player.hasPermission("epicquest.user.turnin")){
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
							if(player.hasPermission("epicquest.user.give")){
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
							if(player.hasPermission("epicquest.user.drop")){

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
							if(player.hasPermission("epicquest.user.questbook")){

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
						if(player.hasPermission("epicquest.user.info")){
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
									int maxTasks = quest.getTaskAmount();
									for(int task = 0; task < maxTasks; task++){
										player.sendMessage(quest.getPlayerTaskProgressText(task));
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
							if(player.hasPermission("epicquest.user.questlist")){

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
						if(player.hasPermission("epicquest.user.stats")){
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
					if(player.hasPermission("epicquest.user.leaderboard")){
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
						if(player.hasPermission("epicquest.admin.debug")){
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
					if(args[0].equalsIgnoreCase("villager")){
						if(player.hasPermission("epicquest.admin.villager")){
							if(args.length >= 4 && args[1].equalsIgnoreCase("spawn")){
								
								//Spawn
								String name = "";
								for(int i = 2; i < args.length - 1; i++){
									if(i == args.length - 1)
										name += args[i];
									else
										name += args[i] + " ";
								}
								
								int quest = Integer.parseInt(args[args.length - 1]);
								List<Integer> questList = new ArrayList<Integer>();
								questList.add(quest);
								if(VillagerHandler.SpawnVillager(player.getWorld(), player.getLocation(), name, questList)){
									player.sendMessage(ChatColor.GREEN + "A villager with the name " + name + " has been spawned with quest " + quest +".");
									List<String> sentenceList = new ArrayList<String>();
									sentenceList.add("Come back to me when you are done.");
									VillagerHandler.SetRandomSentences(VillagerHandler.GetVillager(player.getWorld(), name), sentenceList);
								}else{
									player.sendMessage(ChatColor.RED + "A villager with the name " + name +" has already been found in this world.");
								}
								
							}else if(args.length == 3 && args[1].equalsIgnoreCase("remove")){
								
								//Remove
								String name = args[2];
								if(!VillagerHandler.RemoveVillager(player.getWorld(), name)){
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
						if(player.hasPermission("epicquest.admin.save")){
							saveAll(false);
						}else{
							player.sendMessage(ChatColor.RED + "You don't have permission to do that.");
						}
						return true;
					}

					//Quest block
					if(args[0].equalsIgnoreCase("questblock")){
						if(player.hasPermission("epicquest.admin.questblock")){
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
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
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
<<<<<<< HEAD
				
				//Move villagers back
				VillagerHandler.MoveVillagersBack();
=======
>>>>>>> 8a82a9e34f4bbc9b0780432073f9ac9f1f8fe45e
			}
		}, 1000, 1000);
	}

	private void saveAll(boolean isShutDown){
		try {
			SaveLoader.save(isShutDown);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
}
