package main.java.randy.commands;

import main.java.randy.engine.EpicPlayer;
import main.java.randy.engine.EpicSystem;
import main.java.randy.epicquest.EpicMain;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSave {
	public static void Execute(CommandSender sender, Command command,
			String commandName, String[] args) {
		if (!(sender instanceof Player))
			return;
		Player player = (Player) sender;
		EpicPlayer ePlayer = EpicSystem.getEpicPlayer(player);

		if (!CommandListener.hasPermission(ePlayer, "epicquest.admin.save"))
			return;

		EpicMain.getInstance().saveAll(false);
		player.sendMessage(ChatColor.GREEN + "Succesfully saved all data.");
	}
}
