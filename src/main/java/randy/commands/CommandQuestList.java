package main.java.randy.commands;

import java.util.List;

import main.java.randy.engine.EpicPlayer;
import main.java.randy.engine.EpicSystem;
import main.java.randy.quests.EpicQuestDatabase;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandQuestList {
	public static void Execute(CommandSender sender, Command command,
			String commandName, String[] args) {
		if (!(sender instanceof Player))
			return;
		Player player = (Player) sender;
		EpicPlayer ePlayer = EpicSystem.getEpicPlayer(player);

		if (!CommandListener.hasPermission(ePlayer, "epicquest.user.questlist"))
			return;

		// Get all available quests
		List<String> availableQuests = ePlayer.getObtainableQuests();
		if (availableQuests.isEmpty()) {
			player.sendMessage(ChatColor.RED
					+ "There are no more quests available for you!");
			return;
		}

		int pageNumber = Integer.getInteger(args[1], 1) - 1;
		int maxpages = availableQuests.size() / 10
				+ Math.min(1, availableQuests.size() % 10);

		if (!(pageNumber < maxpages && pageNumber >= 0)) {
			player.sendMessage(ChatColor.RED + "That quest page doesn't exist.");
			return;
		}

		// Send the messages
		StringBuilder pageString = new StringBuilder();
		pageString.append(ChatColor.GOLD);
		pageString.append("[=======  Questbook  ");
		pageString.append(pageNumber + 1);
		pageString.append("/");
		pageString.append(maxpages);
		pageString.append("  =======]\n");
		for (int quest = pageNumber * 10; quest < availableQuests.size()
				&& quest < pageNumber * 10; quest++) {
			StringBuilder questString = new StringBuilder();
			questString.append(ChatColor.GREEN);
			questString.append(quest);
			questString.append(": ");
			questString.append(EpicQuestDatabase.getQuestName(availableQuests
					.get(quest)));
			questString.append("\n");
			pageString.append(questString.toString());
		}
		pageString.append(ChatColor.GOLD);
		pageString.append("[============================]");
		player.sendMessage(pageString.toString());
	}
}
