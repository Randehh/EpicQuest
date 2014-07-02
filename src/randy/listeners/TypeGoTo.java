package randy.listeners;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import randy.epicquest.EpicQuest;
import randy.epicquest.EpicSystem;

public class TypeGoTo extends TypeBase implements Listener {
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event){
		
		//Get player and questlist
		Player player = event.getPlayer();
		String playername = player.getName();

		//Check if player has a enchant task
		HashMap<EpicQuest, String> questlist = checkForType(EpicSystem.getEpicPlayer(playername), "goto");
		if(!questlist.isEmpty()){
			for(int i = 0; i < questlist.size(); i++){

				//Split quest and task
				EpicQuest quest = (EpicQuest) questlist.keySet().toArray()[i];
				String[] tasks = questlist.get(quest).split(",");

				for(int e = 0; e < tasks.length; e++){
					int taskNo = Integer.parseInt(tasks[e]);

					//Check if player is in the specified place
					String[] locations = quest.getTaskID(taskNo).split("=");
					String[] loc1 = locations[0].split(":");
					String[] loc2 = locations[2].split(":");
					
					Vector pos1 = new Vector(Integer.parseInt(loc1[0]), Integer.parseInt(loc1[1]), Integer.parseInt(loc1[2]));
					Vector pos2 = new Vector(Integer.parseInt(loc2[0]), Integer.parseInt(loc2[1]), Integer.parseInt(loc2[2]));;
					
					if(player.getLocation().toVector().isInAABB(pos1, pos2)){

						//Progress task stuff
						quest.modifyTaskProgress(taskNo, 1, true);
					}
				}
			}
		}
	}
}
