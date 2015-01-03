package randy.questtypes;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

import randy.epicquest.EpicPlayer;
import randy.epicquest.EpicSystem;
import randy.quests.EpicQuestTask;
import randy.quests.EpicQuestTask.TaskTypes;

public class TypeCraftItem extends TypeBase implements Listener{
	
	@EventHandler
	public void onCraftItem(CraftItemEvent event){
		
		Player player = (Player)event.getInventory().getHolder();
		EpicPlayer epicPlayer = EpicSystem.getEpicPlayer(player.getName());
		List<EpicQuestTask> taskList = epicPlayer.getTasksByType(TaskTypes.CRAFT_ITEM);
		
		for(EpicQuestTask task : taskList){
			Material itemID = event.getRecipe().getResult().getType();
			String itemNeeded = task.getTaskID();
			
			if(itemID == Material.matchMaterial(itemNeeded)){
				task.ProgressTask(1, epicPlayer);
			}
		}
	}
}
