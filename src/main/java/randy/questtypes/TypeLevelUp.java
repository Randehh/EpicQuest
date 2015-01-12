package main.java.randy.questtypes;

import java.util.List;

import main.java.randy.engine.EpicPlayer;
import main.java.randy.engine.EpicSystem;
import main.java.randy.quests.EpicQuestTask;
import main.java.randy.quests.EpicQuestTask.TaskTypes;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;

public class TypeLevelUp extends TypeBase implements Listener{

	@EventHandler
	public void onPlayerLevelChange(PlayerLevelChangeEvent event){

		Player player = event.getPlayer();
		EpicPlayer epicPlayer = EpicSystem.getEpicPlayer(player.getUniqueId());
		List<EpicQuestTask> taskList = epicPlayer.getTasksByType(TaskTypes.LEVEL_UP);
		
		for(EpicQuestTask task : taskList){
			int oldlevel = event.getOldLevel();
			int newlevel = event.getNewLevel();
			if(newlevel > oldlevel){
				task.ProgressTask(1, epicPlayer);
			}
		}
	}
}

