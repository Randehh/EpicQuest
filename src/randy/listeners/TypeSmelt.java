package randy.listeners;

import java.util.HashMap;

<<<<<<< HEAD
import org.bukkit.Material;
=======
>>>>>>> 8a82a9e34f4bbc9b0780432073f9ac9f1f8fe45e
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;

import randy.epicquest.EpicPlayer;
import randy.epicquest.EpicQuest;
import randy.epicquest.EpicSystem;

public class TypeSmelt extends TypeBase implements Listener{
	
	@EventHandler
	public void OnFurnaceSmelt(FurnaceSmeltEvent event){
		
		EpicPlayer player = EpicSystem.furnaceList.get(event.getBlock().getLocation());
		HashMap<EpicQuest, String> questlist = checkForType(EpicSystem.getEpicPlayer(player.getPlayerName()), "smelt");
		
		if(player != null && !questlist.isEmpty()){
			for(int i = 0; i < questlist.size(); i++){

				//Split quest and task
				EpicQuest quest = (EpicQuest) questlist.keySet().toArray()[i];
				String[] tasks = questlist.get(quest).split(",");

				for(int e = 0; e < tasks.length; e++){
					int taskNo = Integer.parseInt(tasks[e]);
<<<<<<< HEAD
					
					Material itemID = event.getSource().getType();
					Material itemNeeded = Material.matchMaterial(quest.getTaskID(taskNo));
=======

					int itemID = event.getSource().getType().getId();
					int itemNeeded = Integer.parseInt(quest.getTaskID(taskNo));
>>>>>>> 8a82a9e34f4bbc9b0780432073f9ac9f1f8fe45e
					
					if(itemID == itemNeeded){	

						//Progress task stuff
						quest.modifyTaskProgress(taskNo, 1, true);
					}
				}
			}
		}
	}
}
