package randy.listeners;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

import randy.epicquest.EpicQuest;
import randy.epicquest.EpicSystem;

public class TypeEnchant extends TypeBase implements Listener{
	
	@EventHandler
	public void onItemEnchant(EnchantItemEvent event){
		
		//Get player and questlist
		Player player = event.getEnchanter();
		String playername = player.getName();
		
		//Check if player has a enchant task
		HashMap<EpicQuest, String> questlist = checkForType(EpicSystem.getEpicPlayer(playername), "enchant");
		if(!questlist.isEmpty()){
			for(int i = 0; i < questlist.size(); i++){
						
				//Split quest and task
				EpicQuest quest = (EpicQuest) questlist.keySet().toArray()[i];
				String[] tasks = questlist.get(quest).split(",");
				
				for(int e = 0; e < tasks.length; e++){
					int taskNo = Integer.parseInt(tasks[e]);
					
					//Check if correct item was enchanted
					Material item = event.getItem().getType();
					String itemneeded = quest.getTaskID(taskNo);
					if(item == Material.matchMaterial(itemneeded)){
									
						//Progress task stuff
						quest.modifyTaskProgress(taskNo, 1, true);
					}
				}
			}
		}
	}
}
