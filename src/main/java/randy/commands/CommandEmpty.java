package main.java.randy.commands;

import main.java.randy.engine.EpicSystem;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandEmpty {
	public static void Execute(CommandSender sender, Command command,
			String commandName, String[] args) {
		if (!(sender instanceof Player))
			return;
		Player player = (Player) sender;

		player.sendMessage(ChatColor.GOLD
				+ "[-------Welcome to EpicQuest!-------]");
		player.sendMessage(ChatColor.GREEN
				+ "EpicQuest is developed by Randy Schouten (Impossible24) with some additional code by bigbeno37");
		player.sendMessage(ChatColor.GREEN + "This version is currently "
				+ ChatColor.YELLOW + String.valueOf(EpicSystem.getVersion()));
		player.sendMessage("");
		player.sendMessage(ChatColor.GREEN
				+ "For help with how to use EpicQuest, use " + ChatColor.WHITE
				+ "'/q help'");
	}
}
