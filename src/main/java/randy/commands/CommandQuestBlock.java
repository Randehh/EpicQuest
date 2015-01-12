package main.java.randy.commands;

import main.java.randy.engine.EpicPlayer;
import main.java.randy.engine.EpicSign;
import main.java.randy.engine.EpicSystem;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandQuestBlock {

	@SuppressWarnings("deprecation")
	public static void Execute(CommandSender sender, Command command,
			String commandName, String[] args) {
		if (!(sender instanceof Player))
			return;
		Player player = (Player) sender;
		EpicPlayer ePlayer = EpicSystem.getEpicPlayer(player);

		if (!CommandListener.hasPermission(ePlayer,
				"epicquest.admin.questblock"))
			return;

		if (args[1].equalsIgnoreCase("give")) {
			if (args[2].equalsIgnoreCase("random")) {
				Location loc = player.getTargetBlock(null, 25).getLocation();
				loc.setWorld(null);
				EpicSystem.getSignList().add(
						new EpicSign("EpicQuest_Internal_Random", loc));
				player.sendMessage("Questblock created that gives random quests.");
			} else {
				String quest = args[2];
				Location loc = player.getTargetBlock(null, 25).getLocation();
				loc.setWorld(null);
				EpicSystem.getSignList().add(new EpicSign(quest, loc));
				player.sendMessage("Questblock created that gives quest "
						+ quest + ".");
			}
		}
		if (args[1].equalsIgnoreCase("turnin")) {
			Location loc = player.getTargetBlock(null, 25).getLocation();
			loc.setWorld(null);
			EpicSystem.getSignList().add(
					new EpicSign("EpicQuest_Internal_Turnin", loc));
			player.sendMessage("Questblock created that turns in quests.");
		}
	}
}
