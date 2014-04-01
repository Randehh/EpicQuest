package randy.listeners;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

import randy.epicquest.EpicQuest;
import randy.epicquest.EpicSystem;

public class TypeCraftItem extends TypeBase implements Listener{
	
	@EventHandler
	public void onCraftItem(CraftItemEvent event){
		
		Player player = (Player)event.getInventory().getHolder();
		HashMap<EpicQuest, String> questlist = checkForType(EpicSystem.getEpicPlayer(player.getName()), "craft");
		
		if(!questlist.isEmpty()){
			
			for(int i = 0; i < questlist.size(); i++){

				//Split quest and task
				EpicQuest quest = (EpicQuest) questlist.keySet().toArray()[i];
				String[] tasks = questlist.get(quest).split(",");

				for(int e = 0; e < tasks.length; e++){
					int taskNo = Integer.parseInt(tasks[e]);
					
					Material itemID = event.getRecipe().getResult().getType();
					String itemNeeded = quest.getTaskID(taskNo);
					
					if(itemID == Material.matchMaterial(itemNeeded)){	
						
						//Progress task stuff
						quest.modifyTaskProgress(taskNo, 1, true);
						
					}
				}
			}
		}
	}
}
