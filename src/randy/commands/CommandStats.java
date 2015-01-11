package randy.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import randy.engine.EpicPlayer;
import randy.engine.EpicSystem;
import randy.epicquest.EpicMain;

public class CommandStats {
	public static void Execute(CommandSender sender, Command command, String commandName, String[] args){
		if(!(sender instanceof Player)) return;
		Player player = (Player)sender;
		EpicPlayer ePlayer = EpicSystem.getEpicPlayer(player);
		
		if(!CommandListener.hasPermission(ePlayer, "epicquest.user.stats")) return;
		
		EpicPlayer statPlayer = ePlayer;
		if(args.length == 2){
			Player player2 = Bukkit.getPlayer(args[1]);
			statPlayer = EpicSystem.getEpicPlayer(player2.getUniqueId());
			if(statPlayer == null){
				player.sendMessage(ChatColor.RED + "That player couldn't be found.");
				return;
			}
		}
		
		player.sendMessage(ChatColor.YELLOW + "Statistics for player '" + statPlayer.getPlayer().getName() + "'.");
		player.sendMessage(ChatColor.GOLD + "Quests get: " + statPlayer.playerStatistics.GetQuestsGet() + ".");
		player.sendMessage(ChatColor.GOLD + "Quests finished: " + statPlayer.playerStatistics.GetQuestsCompleted() + ".");
		player.sendMessage(ChatColor.GOLD + "Quests dropped: " + statPlayer.playerStatistics.GetQuestsDropped() + ".");
		if(EpicSystem.enabledMoneyRewards())
			player.sendMessage(ChatColor.GOLD + EpicMain.economy.currencyNamePlural() + " earned: " + statPlayer.playerStatistics.GetMoneyEarned() + ".");
		player.sendMessage(ChatColor.GOLD + "Tasks completed: " + statPlayer.playerStatistics.GetTasksCompleted() + ".");
	}
}
