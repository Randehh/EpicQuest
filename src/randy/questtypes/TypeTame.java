package randy.questtypes;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

import randy.epicquest.EpicPlayer;
import randy.epicquest.EpicSystem;
import randy.quests.EpicQuestTask;
import randy.quests.EpicQuestTask.TaskTypes;

public class TypeTame extends TypeBase implements Listener{

	@EventHandler
	public void onEntityTame(EntityTameEvent event){

		Player player = (Player)event.getOwner();
		EpicPlayer epicPlayer = EpicSystem.getEpicPlayer(player.getName());
		List<EpicQuestTask> taskList = epicPlayer.getTasksByType(TaskTypes.TAME_MOB);
		
		for(EpicQuestTask task : taskList){
			//Check if correct entity was tamed
			String entitytamed = event.getEntityType().name();
			String entityneeded = task.getTaskID();
			
			if(entitytamed.equalsIgnoreCase(entityneeded)){	
				task.ProgressTask(1, epicPlayer);
			}
		}
	}
}
