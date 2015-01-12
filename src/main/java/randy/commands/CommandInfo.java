package main.java.randy.commands;

import java.util.List;

import main.java.randy.engine.EpicPlayer;
import main.java.randy.engine.EpicSystem;
import main.java.randy.quests.EpicQuest;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandInfo {
	public static void Execute(CommandSender sender, Command command, String commandName, String[] args){
		if(!(sender instanceof Player)) return;
		Player player = (Player)sender;
		EpicPlayer ePlayer = EpicSystem.getEpicPlayer(player);

		if(!CommandListener.hasPermission(ePlayer, "epicquest.user.info")) return;

		List<EpicQuest> questList = ePlayer.getQuestList();
		int questNumber = Integer.getInteger(args[1], -1);
		if(questNumber == -1 || questNumber > questList.size()){
			player.sendMessage(ChatColor.RED + "That questbook number is not valid.");
			return;
		}
		
		EpicQuest quest = questList.get(questNumber);

		//Send quest name and start info
		player.sendMessage(""+ChatColor.GOLD + quest.getQuestName());
		player.sendMessage(""+ChatColor.GRAY + ChatColor.ITALIC + quest.getQuestStart());

		//Get tasks
		for(int task = 0; task < quest.getTasks().size(); task++){
			player.sendMessage(quest.getTasks().get(task).getPlayerTaskProgressText());
		}
	}
}
