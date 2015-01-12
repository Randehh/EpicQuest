package randy.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import randy.engine.EpicPlayer;
import randy.engine.EpicSystem;

public class CommandHelp {
	public static void Execute(CommandSender sender, Command command, String commandName, String[] args){
		if(!(sender instanceof Player)) return;
		Player player = (Player)sender;
		EpicPlayer ePlayer = EpicSystem.getEpicPlayer(player);

		if(args.length > 1){
			if(args[1].equalsIgnoreCase("admin") && CommandListener.hasPermission(ePlayer, "epicquest.admin.help")){
				player.sendMessage(ChatColor.GOLD + "[=======  Admin help  =======]");
				player.sendMessage(ChatColor.GOLD + "/q questentity <number> - Enables Citizens quest giver mode.");
				player.sendMessage(ChatColor.GOLD + "/q questentity <name> <questnumber> - Spawn a villager with a quest.");
				player.sendMessage(ChatColor.GOLD + "/q questentity remove <name> - Removes Quest Giver with a specific name.");
				player.sendMessage(ChatColor.GOLD + "/q questblock give random - Enables random Questblock mode.");
				player.sendMessage(ChatColor.GOLD + "/q questblock give <questnumber> - Enables Questblock mode.");
				player.sendMessage(ChatColor.GOLD + "/q questblock turnin - Enables quest turn in Questblock mode.");
				player.sendMessage(ChatColor.GOLD + "/q reload - Reloads all quests.");
				player.sendMessage(ChatColor.GOLD + "[====================]");
				return;
			}
			
			if(CommandListener.hasPermission(ePlayer, "epicquest.user.help")){
				int pageNumber = Integer.parseInt(args[1]);
				if(pageNumber == 1){

					player.sendMessage(ChatColor.GOLD + "[=======  Help list (1/3) =======]");
					player.sendMessage(ChatColor.GOLD + "/q help <number> - Displays a help page.");
					player.sendMessage(ChatColor.GOLD + "/q give <questnumber> - Gives you a quest, quest number optional from questlist.");
					player.sendMessage(ChatColor.GOLD + "/q questbook <page> - Displays all the quests you have.");
					player.sendMessage(ChatColor.GOLD + "/q questbook give - Puts a questbook item in your inventory.");
					player.sendMessage(ChatColor.GOLD + "/q questlist <page> - Displays available to you.");
					player.sendMessage(ChatColor.GOLD + "/q info <questnumber> - Display info on the quest.");
					player.sendMessage(ChatColor.GOLD + "/q stats <playername> - Display stats on the player.");
					player.sendMessage(ChatColor.GOLD + "/q turnin - Turn in your quests.");
					player.sendMessage(ChatColor.GOLD + "[=======================]");

				}else if(pageNumber == 2){

					player.sendMessage(ChatColor.GOLD + "[=======  Help list (2/3) =======]");
					player.sendMessage(ChatColor.GOLD + "/q party - Shows who is in your party.");
					player.sendMessage(ChatColor.GOLD + "/q party questbook - Gets the party questbook.");
					player.sendMessage(ChatColor.GOLD + "/q party invite <playername> - Invites a player to your party.");
					player.sendMessage(ChatColor.GOLD + "/q party kick <playername> - Kicks player from group.");
					player.sendMessage(ChatColor.GOLD + "/q party leader <playername> - Makes another player party leader.");
					player.sendMessage(ChatColor.GOLD + "/q party leave - Leave the party.");
					player.sendMessage(ChatColor.GOLD + "/q party chat - Toggle party chat.");
					player.sendMessage(ChatColor.GOLD + "[=======================]");

				}else if(pageNumber == 3){
					player.sendMessage(ChatColor.GOLD + "[=======  Help list (3/3) =======]");
					player.sendMessage(ChatColor.GOLD + "/q leaderboard questcompleted - Shows leaderboard for most quests completed.");
					player.sendMessage(ChatColor.GOLD + "/q leaderboard taskcompleted - Shows leaderboard for most tasks completed.");
					player.sendMessage(ChatColor.GOLD + "/q leaderboard moneyearned - Shows leaderboard for most money earned.");
					player.sendMessage(ChatColor.GOLD + "[=======================]");
				}
			}
		}else{
			player.sendMessage(ChatColor.GOLD + "[=======  Help list (1/3) =======]");
			player.sendMessage(ChatColor.GOLD + "/q help <number> - Displays a help page.");
			player.sendMessage(ChatColor.GOLD + "/q give <questnumber> - Gives you a quest, quest number optional from questlist.");
			player.sendMessage(ChatColor.GOLD + "/q questbook <page> - Displays all the quests you have.");
			player.sendMessage(ChatColor.GOLD + "/q questbook give - Puts a questbook item in your inventory.");
			player.sendMessage(ChatColor.GOLD + "/q questlist <page> - Displays available to you.");
			player.sendMessage(ChatColor.GOLD + "/q info <questnumber> - Display info on the quest.");
			player.sendMessage(ChatColor.GOLD + "/q stats <playername> - Display stats on the player.");
			player.sendMessage(ChatColor.GOLD + "/q turnin - Turn in your quests.");
			player.sendMessage(ChatColor.GOLD + "[=======================]");
		}
	}
}
