package randy.listeners;

import java.util.HashMap;
import java.util.List;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import randy.epicquest.EpicMain;
import randy.epicquest.EpicParty;
import randy.epicquest.EpicPlayer;
import randy.epicquest.EpicSign;
import randy.epicquest.EpicSystem;
import randy.filehandlers.QuestLoader;
import randy.questentities.QuestEntityHandler;
import randy.quests.EpicQuest;
import randy.quests.EpicQuestDatabase;
import randy.quests.EpicQuestTask;
import randy.quests.EpicQuestTask.TaskTypes;

public class CommandListener implements CommandExecutor {
	private HashMap<EpicPlayer, Integer> invitationTimer;
	private Economy economy;
	private EpicMain plugin;
	
	public CommandListener(HashMap<EpicPlayer, Integer> invitationTimer, Economy economy, EpicMain plugin) {
		this.invitationTimer = invitationTimer;
		this.economy = economy;
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandName, String[] args){
		if(sender instanceof Player){
			if(commandName.equalsIgnoreCase("q") || commandName.equalsIgnoreCase("quest")){
				Player player = (Player) sender;
				EpicPlayer epicPlayer = EpicSystem.getEpicPlayer(player.getUniqueId());

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
						
						//Check for admin first
						try{
							if(args[1].equalsIgnoreCase("admin") && epicPlayer.hasPermission("epicquest.admin.help")){
								player.sendMessage(ChatColor.GOLD + "[=======  Admin help  =======]");
								player.sendMessage(ChatColor.GOLD + "/q questentity <number> - Enables Citizens quest giver mode.");
								player.sendMessage(ChatColor.GOLD + "/q questentity <name> <questnumber> - Spawn a villager with a quest.");
								player.sendMessage(ChatColor.GOLD + "/q questentity remove <name> - Removes Quest Giver with a specific name.");
								player.sendMessage(ChatColor.GOLD + "/q questblock give random - Enables random Questblock mode.");
								player.sendMessage(ChatColor.GOLD + "/q questblock give <questnumber> - Enables Questblock mode.");
								player.sendMessage(ChatColor.GOLD + "/q questblock turnin - Enables quest turn in Questblock mode.");
								player.sendMessage(ChatColor.GOLD + "/q reload - Reloads all quests.");
								player.sendMessage(ChatColor.GOLD + "[====================]");
								return true;
							}
						}catch(ArrayIndexOutOfBoundsException e){}
						
						
						
						if(epicPlayer.hasPermission("epicquest.user.help")){
							if(args.length == 1 || (args.length == 2 && args[1] == ""+2)){

								player.sendMessage(ChatColor.GOLD + "[=======  Help list (1/3) =======]");
								player.sendMessage(ChatColor.GOLD + "/q help <number> - Displays a help page.");
								player.sendMessage(ChatColor.GOLD + "/q give <questnumber> - Gives you a quest, quest number optional from questlist.");
								player.sendMessage(ChatColor.GOLD + "/q questbook <page> - Displays all the quests you have.");
								player.sendMessage(ChatColor.GOLD + "/q questlist <page> - Displays available to you.");
								player.sendMessage(ChatColor.GOLD + "/q info <questnumber> - Display info on the quest.");
								player.sendMessage(ChatColor.GOLD + "/q stats <playername> - Display stats on the player.");
								player.sendMessage(ChatColor.GOLD + "/q turnin - Turn in your quests.");
								player.sendMessage(ChatColor.GOLD + "[=======================]");

							}else if(args.length == 2){
								if(Integer.parseInt(args[1]) == 2){

									player.sendMessage(ChatColor.GOLD + "[=======  Help list (2/3) =======]");
									player.sendMessage(ChatColor.GOLD + "/q party - Shows who is in your party.");
									player.sendMessage(ChatColor.GOLD + "/q party questbook - Gets the party questbook.");
									player.sendMessage(ChatColor.GOLD + "/q party invite <playername> - Invites a player to your party.");
									player.sendMessage(ChatColor.GOLD + "/q party kick <playername> - Kicks player from group.");
									player.sendMessage(ChatColor.GOLD + "/q party leader <playername> - Makes another player party leader.");
									player.sendMessage(ChatColor.GOLD + "/q party leave - Leave the party.");
									player.sendMessage(ChatColor.GOLD + "/q party chat - Toggle party chat.");
									player.sendMessage(ChatColor.GOLD + "[=======================]");

								}else if(Integer.parseInt(args[1]) == 3){
									player.sendMessage(ChatColor.GOLD + "[=======  Help list (3/3) =======]");
									player.sendMessage(ChatColor.GOLD + "/q leaderboard");
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

														player.sendMessage("" + ChatColor.ITALIC + ChatColor.GREEN + "You invited " + invitedEpicPlayer.getPlayer().getName() + " to your party.");
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
											player.sendMessage(ChatColor.GREEN + "You have joined " + invitationPlayer.getPlayer().getName() + "'s party!");
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
											if(party.getLeader() == epicPlayer){
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
											if(party.getLeader() == epicPlayer){
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
									List<String> availableQuests = epicPlayer.getObtainableQuests();

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
											if(list.get(quest).isCompleted()){
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
								List<String> availableQuests = epicPlayer.getObtainableQuests();

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
								EpicPlayer epicPlayer2 = EpicSystem.getEpicPlayer(player2.getUniqueId());

								if(epicPlayer2 != null){
									player.sendMessage(ChatColor.YELLOW + "Statistics for player '" + player2.getName() + "'.");
									player.sendMessage(ChatColor.GOLD + "Quests get: " + epicPlayer2.getStatQuestGet() + ".");
									player.sendMessage(ChatColor.GOLD + "Quests finished: " + epicPlayer2.getStatQuestCompleted() + ".");
									player.sendMessage(ChatColor.GOLD + "Quests dropped: " + epicPlayer2.getStatQuestDropped() + ".");
									player.sendMessage(ChatColor.GOLD + economy.currencyNamePlural() + " earned: " + epicPlayer2.getStatMoneyEarned() + ".");
									player.sendMessage(ChatColor.GOLD + "Tasks completed: " + epicPlayer2.getStatTaskCompleted() + ".");
								}else{
									player.sendMessage(ChatColor.RED + "That player doesn't exist!");
								}
							}else if (args.length == 1){
								player.sendMessage(ChatColor.YELLOW + "Statistics for player '" + player.getName() + "'.");
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
					
					if(args[0].equalsIgnoreCase("leaderboard")){
						if(epicPlayer.hasPermission("epicquest.user.leaderboard")){
							String amount;
							Object[] top3 = null;
							List<Object> leaderboard = EpicSystem.getLeaderboard();
							int position = 0;
							
							if(leaderboard.size() >= 3){ amount = "3"; }else{ amount = String.valueOf(leaderboard.size()); } 
							if(amount == "3"){
								top3 = new Object[]{leaderboard.get(0), leaderboard.get(1), leaderboard.get(2)};
							}else if(amount == "2"){
								top3 = new Object[]{leaderboard.get(0), leaderboard.get(1), "-----------"};
							}else{
								top3 = new Object[]{leaderboard.get(0), "-----------", "-----------"};
							}
							
							for(int i=0;i<leaderboard.size();i++){
								EpicPlayer lePlayer = (EpicPlayer)leaderboard.get(i);
								
								if(lePlayer.getPlayer().getName() == player.getDisplayName()) position = i;
							}
							
							player.sendMessage(ChatColor.GOLD + "[=======  Leaderboards (Top " + amount +"=======]");
							player.sendMessage(ChatColor.GOLD + "     1) " + top3[0]);
							player.sendMessage(ChatColor.GOLD + "     2) " + top3[1]);
							player.sendMessage(ChatColor.GOLD + "     3) " + top3[2]);
							player.sendMessage(ChatColor.GOLD + "[===================================]");
							player.sendMessage(ChatColor.GOLD + "");
							player.sendMessage(ChatColor.GOLD + "Your current position is " + String.valueOf(position) + ".");
						}
					}					

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
								
								String quest = args[args.length - 1];
								
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
							plugin.saveAll(false);
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
							QuestEntityHandler.Reload();
							player.sendMessage(ChatColor.GREEN + "Succesfully reloaded the quest database and Quest Givers.");
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
									EpicSystem.getSignList().add(new EpicSign("EpicQuest_Internal_Random", loc));
									player.sendMessage("Questblock created that gives random quests.");
								} else {
									String quest = args[2];
									Location loc = player.getTargetBlock(null, 25).getLocation();
									loc.setWorld(null);
									EpicSystem.getSignList().add(new EpicSign(quest, loc));
									player.sendMessage("Questblock created that gives quest " + quest + ".");
								}
							} if(args[1].equalsIgnoreCase("turnin")){
								Location loc = player.getTargetBlock(null, 25).getLocation();
								loc.setWorld(null);
								EpicSystem.getSignList().add(new EpicSign("EpicQuest_Internal_Turnin", loc));
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

}
