package randy.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import randy.engine.EpicPlayer;
import randy.engine.EpicSystem;
import randy.filehandlers.QuestLoader;
import randy.questentities.QuestEntityHandler;
import randy.quests.EpicQuest;
import randy.quests.EpicQuestDatabase;

public class CommandReload {
	public static void Execute(CommandSender sender, Command command, String commandName, String[] args){
		if(!(sender instanceof Player)) return;
		Player player = (Player)sender;
		EpicPlayer ePlayer = EpicSystem.getEpicPlayer(player);
		
		if(!CommandListener.hasPermission(ePlayer, "epicquest.admin.reload")) return;
		
		EpicQuestDatabase.ClearDatabase();
		QuestLoader.loadQuests();
		EpicQuest.ResetQuestTaskInfo();
		QuestEntityHandler.Reload();
		player.sendMessage(ChatColor.GREEN + "Succesfully reloaded the quest database and Quest Givers.");
	}
}
