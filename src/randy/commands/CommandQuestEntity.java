package randy.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import randy.engine.EpicPlayer;
import randy.engine.EpicSystem;
import randy.listeners.PlayerInteractListener;
import randy.questentities.QuestEntityHandler;

public class CommandQuestEntity {
	public static void Execute(CommandSender sender, Command command, String commandName, String[] args){
		if(!(sender instanceof Player)) return;
		Player player = (Player)sender;
		EpicPlayer ePlayer = EpicSystem.getEpicPlayer(player);
		
		if(!CommandListener.hasPermission(ePlayer, "epicquest.admin.questentity")) return;
		
		if(args[1].equalsIgnoreCase("create")){	
			
			if(args.length == 3 && !EpicSystem.useCitizens()){
				player.sendMessage("/q questentity create <name> <questnumber>");
				return;
			}else if(args.length == 4 && EpicSystem.useCitizens()){
				player.sendMessage("/q questentity create <questnumber>");
				return;
			}
			
			String quest = args[args.length - 1];
			
			if(EpicSystem.useCitizens()){
				PlayerInteractListener.createNewQuestEntity = player;
				PlayerInteractListener.createNewQuestEntityQuest = quest;
				player.sendMessage(ChatColor.GREEN + "Right click a Citizen to make it a quest giver.");
			}else{
				String name = "";
				for(int i = 2; i < args.length - 1; i++){
					if(i == args.length - 1)
						name += args[i];
					else
						name += args[i] + " ";
				}
				name = name.trim();
				
				QuestEntityHandler.SpawnVillager(player.getWorld(), player.getLocation(), name);
				QuestEntityHandler.GetQuestEntity(QuestEntityHandler.GetEntity(player.getWorld(), name)).SetBasics(quest);
				
				player.sendMessage(ChatColor.GREEN + "A villager with the name " + name + " has been created with quest " + quest +".");
			}

		}else if(args.length == 3 && args[1].equalsIgnoreCase("remove")){

			//Remove
			String name = args[2];
			name = name.trim();
			if(!QuestEntityHandler.RemoveVillager(player.getWorld(), name)){
				player.sendMessage(ChatColor.RED + "A villager with the name " + name +" has not been found in this world.");
			}else{
				player.sendMessage(ChatColor.GREEN + "The villager with the name " + name + " has been removed.");
			}

		}
	}
}
