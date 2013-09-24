package randy.listeners;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

import randy.epicquest.EpicQuest;
import randy.epicquest.EpicSystem;

public class TypeTame extends TypeBase implements Listener{

	@EventHandler
	public void onEntityTame(EntityTameEvent event){

		//Get player and questlist
		Player player = (Player) event.getOwner();
		String playername = player.getName();

		//Check if player has a tame task
		HashMap<EpicQuest, String> questlist = checkForType(EpicSystem.getEpicPlayer(playername), "tame");
		if(!questlist.isEmpty()){
			for(int i = 0; i < questlist.size(); i++){

				//Split quest and task
				EpicQuest quest = (EpicQuest) questlist.keySet().toArray()[i];
				String[] tasks = questlist.get(quest).split(",");

				for(int e = 0; e < tasks.length; e++){
					int taskNo = Integer.parseInt(tasks[e]);

					//Check if correct entity was tamed
					String entitytamed = event.getEntityType().getName();
					String entityneeded = quest.getTaskID(taskNo);
					if(entitytamed.equalsIgnoreCase(entityneeded)){	

						//Progress task stuff
						quest.modifyTaskProgress(taskNo, 1, true);
					}
				}
			}
		}
	}
}
