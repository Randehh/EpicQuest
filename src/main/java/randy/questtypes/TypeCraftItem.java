package main.java.randy.questtypes;

import java.util.List;

import main.java.randy.engine.EpicPlayer;
import main.java.randy.engine.EpicSystem;
import main.java.randy.quests.EpicQuestTask;
import main.java.randy.quests.EpicQuestTask.TaskTypes;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class TypeCraftItem extends TypeBase implements Listener{
	
	@EventHandler
	public void onCraftItem(CraftItemEvent event){
		
		Player player = (Player)event.getInventory().getHolder();
		EpicPlayer epicPlayer = EpicSystem.getEpicPlayer(player.getUniqueId());
		List<EpicQuestTask> taskList = epicPlayer.getTasksByType(TaskTypes.CRAFT_ITEM);
		
		for(EpicQuestTask task : taskList){
			Material itemID = event.getRecipe().getResult().getType();
			String itemNeeded = task.getTaskID();
			
			if(itemID == Material.matchMaterial(itemNeeded)){
				task.ProgressTask(1, epicPlayer, true);
			}
		}
	}
}
