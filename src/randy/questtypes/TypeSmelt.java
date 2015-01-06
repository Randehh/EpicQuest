package randy.questtypes;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;

import randy.epicquest.EpicPlayer;
import randy.epicquest.EpicSystem;
import randy.quests.EpicQuestTask;
import randy.quests.EpicQuestTask.TaskTypes;

public class TypeSmelt extends TypeBase implements Listener{
	
	@EventHandler
	public void OnFurnaceSmelt(FurnaceSmeltEvent event){
		if(!EpicSystem.furnaceList.containsKey(event.getBlock().getLocation())) return;
		
		EpicPlayer epicPlayer = EpicSystem.furnaceList.get(event.getBlock().getLocation());
		List<EpicQuestTask> taskList = epicPlayer.getTasksByType(TaskTypes.SMELT_ITEM);
		
		for(EpicQuestTask task : taskList){
			Material itemID = event.getSource().getType();
			Material itemNeeded = Material.matchMaterial(task.getTaskID());
			
			if(itemID == itemNeeded){
				task.ProgressTask(1, epicPlayer);
			}
		}
	}
}
