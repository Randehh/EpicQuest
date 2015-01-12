package main.java.randy.questtypes;

import java.util.List;

import main.java.randy.engine.EpicPlayer;
import main.java.randy.engine.EpicSystem;
import main.java.randy.quests.EpicQuestTask;
import main.java.randy.quests.EpicQuestTask.TaskTypes;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

public class TypeTame extends TypeBase implements Listener {

	@EventHandler
	public void onEntityTame(EntityTameEvent event) {

		Player player = (Player) event.getOwner();
		EpicPlayer epicPlayer = EpicSystem.getEpicPlayer(player.getUniqueId());
		List<EpicQuestTask> taskList = epicPlayer
				.getTasksByType(TaskTypes.TAME_MOB);

		for (EpicQuestTask task : taskList) {
			// Check if correct entity was tamed
			String entitytamed = event.getEntityType().name();
			String entityneeded = task.getTaskID();

			if (entitytamed.equalsIgnoreCase(entityneeded)) {
				task.ProgressTask(1, epicPlayer);
			}
		}
	}
}
