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
			String[] locationArray = task.getTaskID().split("=");
			Vector location = new Vector(Integer.parseInt(locationArray[0]), Integer.parseInt(locationArray[1]), Integer.parseInt(locationArray[2]));
			
			if(player.getLocation().toVector().distance(location) < 20){
				task.ProgressTask(1, epicPlayer);
			}
		}
	}
}
