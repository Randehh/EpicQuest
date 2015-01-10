package randy.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import randy.engine.EpicPlayer;
import randy.engine.EpicSystem;
import randy.quests.EpicQuestTask;
import randy.quests.EpicQuestTask.TaskTypes;

public class CommandListener implements CommandExecutor {

	public CommandListener() {
	}

	//This sends a message right away, so you don't need to copy paste it all the time
	public static boolean hasPermission(EpicPlayer ePlayer, String permission){
		if(!ePlayer.hasPermission(permission)){
			ePlayer.getPlayer().sendMessage(ChatColor.RED + "You don't have permission to do that!");
			return false;
		}
		return true;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandName, String[] args){
		
		//Check if the player did a command to finish a quest first
		if(sender instanceof Player){

			Player player = (Player) sender;
			EpicPlayer epicPlayer = EpicSystem.getEpicPlayer(player.getUniqueId());

			List<EpicQuestTask> taskList = epicPlayer.getTasksByType(TaskTypes.EXECUTE_COMMAND);

			if(!taskList.isEmpty()){
				StringBuilder fullCommand = new StringBuilder();
				fullCommand.append(commandName);
				for(String arg : args){
					fullCommand.append(" ");
					fullCommand.append(arg);
				}


				for(EpicQuestTask task : taskList){
					if(task.getTaskID().equalsIgnoreCase(fullCommand.toString())){
						task.ProgressTask(1, epicPlayer);
					}
				}
			}
		}
		
		if(commandName.equalsIgnoreCase("q") || commandName.equalsIgnoreCase("quest")){
			if(args.length == 0){
				CommandEmpty.Execute(sender, command, commandName, args);
				return true;
			}

			if(args[0].equalsIgnoreCase("help")){
				CommandHelp.Execute(sender, command, commandName, args);
				return true;
			}

			if(args[0].equalsIgnoreCase("party")){
				CommandParty.Execute(sender, command, commandName, args);
				return true;
			}

			if(args[0].equalsIgnoreCase("turnin")){
				CommandTurnin.Execute(sender, command, commandName, args);
				return true;
			}

			if(args[0].equalsIgnoreCase("give")){
				CommandGive.Execute(sender, command, commandName, args);
				return true;
			}

			if(args[0].equalsIgnoreCase("drop")){
				CommandDrop.Execute(sender, command, commandName, args);
				return true;
			}

			if(args[0].equalsIgnoreCase("info")){
				CommandInfo.Execute(sender, command, commandName, args);
				return true;
			}

			if(args[0].equalsIgnoreCase("stats")){
				CommandStats.Execute(sender, command, commandName, args);
				return true;
			}

			if(args[0].equalsIgnoreCase("leaderboard")){
				CommandLeaderboard.Execute(sender, command, commandName, args);
				return true;
			}

			if(args[0].equalsIgnoreCase("questentity")){
				CommandQuestEntity.Execute(sender, command, commandName, args);;
				return true;
			}
		}
		return false;
	}

}
