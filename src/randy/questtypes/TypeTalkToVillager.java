package randy.questtypes;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import randy.epicquest.EpicPlayer;
import randy.epicquest.EpicSystem;
import randy.quests.EpicQuestTask;
import randy.quests.EpicQuestTask.TaskTypes;

public class TypeTalkToVillager  extends TypeBase implements Listener{
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event){
		Entity clickedEntity = event.getRightClicked();
		if(clickedEntity instanceof Villager){
			
			Player player = event.getPlayer();
			EpicPlayer epicPlayer = EpicSystem.getEpicPlayer(player.getName());
			List<EpicQuestTask> taskList = epicPlayer.getTasksByType(TaskTypes.TALK_TO_VILLAGER);
			Villager villager = (Villager)clickedEntity;
			
			for(EpicQuestTask task : taskList){
				String villagerName = villager.getCustomName();
				String villagerNeeded = task.getTaskID();
				
				if(villagerName.equalsIgnoreCase(villagerNeeded)){
					task.ProgressTask(1, epicPlayer);
				}
			}
		}
	}
}
