package randy.questtypes;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import randy.engine.EpicPlayer;
import randy.engine.EpicSystem;
import randy.questentities.QuestEntityHandler;
import randy.quests.EpicQuestTask;
import randy.quests.EpicQuestTask.TaskTypes;

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
				task.ProgressTask(1, epicPlayer);
			}
		}
	}
}
