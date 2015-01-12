package main.java.randy.commands;

import main.java.randy.engine.EpicPlayer;
import main.java.randy.engine.EpicSystem;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTurnin {
	public static void Execute(CommandSender sender, Command command,
			String commandName, String[] args) {
		if (!(sender instanceof Player))
			return;
		Player player = (Player) sender;
		EpicPlayer ePlayer = EpicSystem.getEpicPlayer(player);

		if (!CommandListener.hasPermission(ePlayer, "epicquest.user.turnin"))
			return;

		if (!ePlayer.getCompleteableQuest().isEmpty()) {
			ePlayer.completeAllQuests();
		} else {
			player.sendMessage(ChatColor.RED
					+ "There are no quests to turn in.");
		}
	}
}
