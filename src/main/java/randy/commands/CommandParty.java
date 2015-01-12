package main.java.randy.commands;

import java.util.HashMap;

import main.java.randy.engine.EpicParty;
import main.java.randy.engine.EpicPlayer;
import main.java.randy.engine.EpicSystem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandParty {

	private static HashMap<EpicPlayer, Integer> invitationTimer = new HashMap<EpicPlayer, Integer>();

	public static void Execute(CommandSender sender, Command command, String commandName, String[] args){
		if(!(sender instanceof Player)) return;
		Player player = (Player)sender;
		EpicPlayer ePlayer = EpicSystem.getEpicPlayer(player);

		//You need this permission for all commands, so check it at the start
		if(!CommandListener.hasPermission(ePlayer, "epicquest.user.party")) return;

		//Show party info
		if(args.length == 1){
			
			//Show party formation
			EpicParty party = ePlayer.getParty();
			if(party != null){
				party.sendPartyMembers(ePlayer);
			}else{
				player.sendMessage(ChatColor.RED + "You are not in a party.");
			}
			return;
		}
		
		
		//Accept invitation
		if(args[1].equalsIgnoreCase("accept")){
			if(ePlayer.hasPartyInvitation != null && invitationTimer.containsKey(ePlayer)){

				EpicPlayer invitationPlayer = ePlayer.hasPartyInvitation;
				if(invitationPlayer.getParty() == null){
					new EpicParty(invitationPlayer, ePlayer);
					ePlayer.hasPartyInvitation = null;

					invitationPlayer.getPlayer().sendMessage(ChatColor.GREEN + player.getDisplayName() + " has joined your party!");
					player.sendMessage(ChatColor.GREEN + "You have joined " + invitationPlayer.getPlayer().getName() + "'s party!");
				}else{
					ePlayer.hasPartyInvitation.getParty().addPlayer(ePlayer);
					ePlayer.hasPartyInvitation = null;
				}

				//Remove player from timer
				invitationTimer.remove(ePlayer);
			}else{
				player.sendMessage(ChatColor.RED + "You don't have a party invitation.");
			}
			return;
		}
		
		//Invite player
		if(args[1].equalsIgnoreCase("invite")){
			if(args.length != 3){
				player.sendMessage("/q party invite <playername>");
				return;
			}
			
			//Is player found/online?
			Player invitedPlayer = Bukkit.getPlayer(args[2]);
			if(invitedPlayer == null){
				player.sendMessage(ChatColor.RED + "That player couldn't be found.");
				return;
			}
			
			//Invite self
			if(invitedPlayer == player){
				player.sendMessage(ChatColor.RED + "You can't invite yourself.");
				return;
			}
			
			//Is player in party?
			EpicPlayer invitedEPlayer = EpicSystem.getEpicPlayer(player);
			if(invitedEPlayer.getParty() != null){
				player.sendMessage(ChatColor.RED + "That player is already in a party.");
				return;
			}
			
			//Check party size with new party members, including the invited ones!
			int partySize = 1;
			if(ePlayer.getParty() != null){
				partySize = ePlayer.getParty().getPartyMembers().size();
				for(EpicPlayer p : invitationTimer.keySet()){
					if(p.hasPartyInvitation == ePlayer) partySize++;
				}
			}
			if(partySize >= EpicSystem.getMaxPartySize()){
				player.sendMessage(ChatColor.RED + "Your party is already full!");
				return;
			}
			
			//All clear! Invite the player
			invitedPlayer.sendMessage(ChatColor.GREEN + player.getName() + " invited you to his party (" + partySize + ").");
			invitedPlayer.sendMessage(ChatColor.GREEN + "Type '/q party accept' to accept the invitation.");
			invitedEPlayer.hasPartyInvitation = ePlayer;

			invitationTimer.put(invitedEPlayer, 15);

			player.sendMessage("" + ChatColor.ITALIC + ChatColor.GREEN + "You invited " + invitedPlayer.getName() + " to your party.");
			return;
		}
		
		//Kick player
		if(args[1].equalsIgnoreCase("kick")){
			if(args.length != 3){
				player.sendMessage("/q party kick <playername>");
				return;
			}
			
			EpicParty party = ePlayer.getParty();
			if(party == null){
				player.sendMessage(ChatColor.RED + "You are not in a party.");
				return;
			}
			
			if(party.getLeader() != ePlayer){
				player.sendMessage(ChatColor.RED + "You are not the party leader.");
				return;
			}
			
			Player kickPlayer = Bukkit.getPlayer(args[2]);
			if(kickPlayer == null){
				player.sendMessage(ChatColor.RED + "That player couldn't be found.");
				return;
			}
			
			EpicPlayer kickEPlayer = EpicSystem.getEpicPlayer(kickPlayer);
			if(!party.getPartyMembers().contains(kickEPlayer)){
				player.sendMessage(ChatColor.RED + "That player isn't in your party.");
				return;
			}
			
			party.removePlayer(kickEPlayer, true);
			return;
		}
		
		//Change party leader
		if(args[1].equalsIgnoreCase("leader")){
			if(args.length != 3){
				player.sendMessage("/q party leader <playername>");
				return;
			}
			
			EpicParty party = ePlayer.getParty();
			if(party == null){
				player.sendMessage(ChatColor.RED + "You are not in a party.");
				return;
			}
			
			if(party.getLeader() != ePlayer){
				player.sendMessage(ChatColor.RED + "You are not the party leader.");
				return;
			}
			
			Player leaderPlayer = Bukkit.getPlayer(args[2]);
			if(leaderPlayer == null){
				player.sendMessage(ChatColor.RED + "That player couldn't be found.");
				return;
			}
			
			EpicPlayer leaderEPlayer = EpicSystem.getEpicPlayer(leaderPlayer);
			if(!party.getPartyMembers().contains(leaderEPlayer)){
				player.sendMessage(ChatColor.RED + "That player isn't in your party.");
				return;
			}
			
			party.setPartyLeader(leaderEPlayer);
			return;
		}
		
		//Leave party
		if(args[1].equalsIgnoreCase("leave")){
			EpicParty party = ePlayer.getParty();
			if(party == null){
				player.sendMessage(ChatColor.RED + "You are not in a party.");
				return;
			}
			
			party.removePlayer(ePlayer, false);
		}
		
		//Toggle party chat
		if(args[1].equalsIgnoreCase("chat")){
			EpicParty party = ePlayer.getParty();
			if(party == null){
				player.sendMessage(ChatColor.RED + "You are not in a party.");
				return;
			}
			
			ePlayer.partyChat = !ePlayer.partyChat;
		}
	}
}
