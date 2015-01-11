package randy.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import randy.engine.EpicPlayer;
import randy.engine.EpicSystem;
import randy.quests.EpicQuest;

public class CommandGive {
	public static void Execute(CommandSender sender, Command command, String commandName, String[] args){
		if(!(sender instanceof Player)) return;
		Player player = (Player)sender;
		EpicPlayer ePlayer = EpicSystem.getEpicPlayer(player);
		
		if(!CommandListener.hasPermission(ePlayer, "epicquest.user.give")) return;
		
		//Random quest
		if(args.length == 1){
			if(!ePlayer.canGetQuest()){
				player.sendMessage(ChatColor.RED + "There are no more quests available.");
				return;
			}
			
			ePlayer.addQuestRandom();
			return;
		}else if(args.length == 2){
			//Specific quest
			int quest = Integer.getInteger(args[1], -1);
			List<String> availableQuests = ePlayer.getObtainableQuests();
			
			if(quest == -1 || quest > availableQuests.size() - 1){
				player.sendMessage(ChatColor.RED + "That isn't a valid quest number.");
				return;
			}
			
			ePlayer.addQuest(new EpicQuest(ePlayer, availableQuests.get(quest)));
			return;
		}
	}
}
