package main.java.randy.commands;

import java.util.List;

import main.java.randy.engine.EpicPlayer;
import main.java.randy.engine.EpicSystem;
import main.java.randy.quests.EpicQuest;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDrop {
	public static void Execute(CommandSender sender, Command command, String commandName, String[] args){
		if(!(sender instanceof Player)) return;
		Player player = (Player)sender;
		EpicPlayer ePlayer = EpicSystem.getEpicPlayer(player);
		
		if(!CommandListener.hasPermission(ePlayer, "epicquest.user.drop")) return;
		
		if(args.length != 2){
			player.sendMessage("/q drop <questbook number>");
			return;
		}
		
		List<EpicQuest> questList = ePlayer.getQuestList();
		if(questList.isEmpty()){
			player.sendMessage(ChatColor.RED + "You don't have any quests.");
			return;
		}
		
		int quest = Integer.getInteger(args[1], -1);
		if(quest == -1 || quest > questList.size() - 1){
			player.sendMessage(ChatColor.RED + "That isn't a valid questbook number.");
			return;
		}
		
		EpicQuest eQuest = questList.get(quest);
		ePlayer.removeQuest(eQuest);
		ePlayer.playerStatistics.AddQuestsDropped(1);
		player.sendMessage(ChatColor.GREEN + "Quest '" + eQuest.getQuestName() + "' succesfully dropped.");
	}
}
