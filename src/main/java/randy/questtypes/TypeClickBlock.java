package main.java.randy.questtypes;

import java.util.List;

import main.java.randy.engine.EpicPlayer;
import main.java.randy.engine.EpicSystem;
import main.java.randy.quests.EpicQuestTask;
import main.java.randy.quests.EpicQuestTask.TaskTypes;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class TypeClickBlock extends TypeBase implements Listener {

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		
		//Get player and the action
		Player player = event.getPlayer();
		EpicPlayer epicPlayer = EpicSystem.getEpicPlayer(player.getUniqueId());
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
			
			List<EpicQuestTask> taskList = epicPlayer.getTasksByType(TaskTypes.CLICK_BLOCK);
			
			for(EpicQuestTask task : taskList){
				String[] locArray = task.getTaskID().split("=");
				Location loc = new Location(player.getWorld(),
						Integer.parseInt(locArray[0]),
						Integer.parseInt(locArray[1]),
						Integer.parseInt(locArray[2]));
				
				if(event.getClickedBlock().getLocation().equals(loc)){
					task.ProgressTask(1, epicPlayer, true);
				}
			}
		}
	}
}
