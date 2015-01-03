package randy.questtypes;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import randy.epicquest.EpicPlayer;
import randy.epicquest.EpicSystem;
import randy.quests.EpicQuestTask;
import randy.quests.EpicQuestTask.TaskTypes;

public class TypeGoTo extends TypeBase implements Listener {
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event){
		
		//Get player and questlist
		Player player = event.getPlayer();
		EpicPlayer epicPlayer = EpicSystem.getEpicPlayer(player.getName());
		List<EpicQuestTask> taskList = epicPlayer.getTasksByType(TaskTypes.GO_TO);
		
		for(EpicQuestTask task : taskList){			
			String[] locations = task.getTaskID().split("=");
			String[] loc1 = locations[0].split(":");
			String[] loc2 = locations[2].split(":");
			
			Vector pos1 = new Vector(Integer.parseInt(loc1[0]), Integer.parseInt(loc1[1]), Integer.parseInt(loc1[2]));
			Vector pos2 = new Vector(Integer.parseInt(loc2[0]), Integer.parseInt(loc2[1]), Integer.parseInt(loc2[2]));;
			
			if(player.getLocation().toVector().isInAABB(pos1, pos2)){
				task.ProgressTask(1, epicPlayer);
			}
		}
	}
}
