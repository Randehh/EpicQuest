package randy.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import randy.engine.EpicPlayer;
import randy.engine.EpicSystem;

public class CommandTurnin {
	public static void Execute(CommandSender sender, Command command, String commandName, String[] args){
		if(!(sender instanceof Player)) return;
		Player player = (Player)sender;
		EpicPlayer ePlayer = EpicSystem.getEpicPlayer(player);
		
		if(!CommandListener.hasPermission(ePlayer, "epicquest.user.turnin")) return;
		
		if(!ePlayer.getCompleteableQuest().isEmpty()){
			ePlayer.completeAllQuests();
		}else{
			player.sendMessage(ChatColor.RED + "There are no quests to turn in.");
		}
	}
}
