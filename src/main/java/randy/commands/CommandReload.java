package main.java.randy.commands;

import main.java.randy.engine.EpicPlayer;
import main.java.randy.engine.EpicSystem;
import main.java.randy.filehandlers.QuestLoader;
import main.java.randy.questentities.QuestEntityHandler;
import main.java.randy.quests.EpicQuest;
import main.java.randy.quests.EpicQuestDatabase;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandReload {
	public static void Execute(CommandSender sender, Command command,
			String commandName, String[] args) {
		if (!(sender instanceof Player))
			return;
		Player player = (Player) sender;
		EpicPlayer ePlayer = EpicSystem.getEpicPlayer(player);

		if (!CommandListener.hasPermission(ePlayer, "epicquest.admin.reload"))
			return;

		EpicQuestDatabase.ClearDatabase();
		QuestLoader.loadQuests();
		EpicQuest.ResetQuestTaskInfo();
		QuestEntityHandler.Reload();
		player.sendMessage(ChatColor.GREEN
				+ "Succesfully reloaded the quest database and Quest Givers.");
	}
}
