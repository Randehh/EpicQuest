package main.java.randy.questtypes;

import java.util.List;

import main.java.randy.engine.EpicPlayer;
import main.java.randy.engine.EpicSystem;
import main.java.randy.quests.EpicQuestTask;
import main.java.randy.quests.EpicQuestTask.TaskTypes;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;

public class TypeSmelt extends TypeBase implements Listener {

	@EventHandler
	public void OnFurnaceSmelt(FurnaceSmeltEvent event) {
		if (!EpicSystem.furnaceList.containsKey(event.getBlock().getLocation()))
			return;

		EpicPlayer epicPlayer = EpicSystem.furnaceList.get(event.getBlock()
				.getLocation());
		List<EpicQuestTask> taskList = epicPlayer
				.getTasksByType(TaskTypes.SMELT_ITEM);

		for (EpicQuestTask task : taskList) {
			Material itemID = event.getSource().getType();
			Material itemNeeded = Material.matchMaterial(task.getTaskID());

			if (itemID == itemNeeded) {
				task.ProgressTask(1, epicPlayer);
			}
		}
	}
}
