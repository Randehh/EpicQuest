package main.java.randy.commands;

import java.util.List;

import main.java.randy.engine.EpicPlayer;
import main.java.randy.engine.EpicSystem;
import main.java.randy.quests.EpicQuest;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandQuestBook {
	public static void Execute(CommandSender sender, Command command, String commandName, String[] args){
		if(!(sender instanceof Player)) return;
		Player player = (Player)sender;
		EpicPlayer ePlayer = EpicSystem.getEpicPlayer(player);

		if(!CommandListener.hasPermission(ePlayer, "epicquest.user.questbook")) return;

		if(args.length == 2 && args[1].equalsIgnoreCase("give")){
			ePlayer.giveQuestBook();
			player.sendMessage(ChatColor.GREEN + "You have been given a Questbook.");
			return;
		}

		int pageNumber = Integer.getInteger(args[1], 1) - 1;
		List<EpicQuest> questList = ePlayer.getQuestList();
		if(questList.isEmpty()){
			player.sendMessage(ChatColor.RED + "You don't have any quests.");
			return;
		}

		int maxpages = questList.size() / 10 + Math.min(1, questList.size() % 10);

		if(!(pageNumber < maxpages && pageNumber >= 0)){
			player.sendMessage(ChatColor.RED + "That quest page doesn't exist.");
			return;
		}


		//Send the messages
		StringBuilder pageString = new StringBuilder();
		pageString.append(ChatColor.GOLD);
		pageString.append("[=======  Questbook  ");
		pageString.append(pageNumber + 1);
		pageString.append("/");
		pageString.append(maxpages);
		pageString.append("  =======]\n");
		for(int quest = pageNumber * 10; quest < questList.size() && quest < pageNumber * 10; quest++){
			StringBuilder questString =  new StringBuilder();
			if(questList.get(quest).isCompleted()){
				questString.append(ChatColor.GREEN + "");
			}else{
				questString.append(ChatColor.RED + "");
			}
			questString.append(quest);
			questString.append(": ");
			questString.append(questList.get(quest).getQuestName());
			questString.append("\n");
			pageString.append(questString.toString());
		}
		pageString.append(ChatColor.GOLD);
		pageString.append("[============================]");
		player.sendMessage(pageString.toString());
	}
}
