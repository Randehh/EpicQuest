package main.java.randy.questtypes;

import java.util.List;

import main.java.randy.engine.EpicPlayer;
import main.java.randy.engine.EpicSystem;
import main.java.randy.questentities.QuestEntityHandler;
import main.java.randy.quests.EpicQuestTask;
import main.java.randy.quests.EpicQuestTask.TaskTypes;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class TypeTalkToVillager  extends TypeBase implements Listener{

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event){

		Player player = event.getPlayer();
		EpicPlayer epicPlayer = EpicSystem.getEpicPlayer(player.getUniqueId());
		List<EpicQuestTask> taskList = epicPlayer.getTasksByType(TaskTypes.TALK_TO_VILLAGER);
		Entity entity = event.getRightClicked();

		for(EpicQuestTask task : taskList){
			String entityName = QuestEntityHandler.getEntityName(entity);
			String entityNeeded = task.getTaskID();

			if(entityName.equalsIgnoreCase(entityNeeded)){
				task.ProgressTask(1, epicPlayer, true);
			}
		}
	}
}
