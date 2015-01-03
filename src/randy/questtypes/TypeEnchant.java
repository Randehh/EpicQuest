package randy.questtypes;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

import randy.epicquest.EpicPlayer;
import randy.epicquest.EpicSystem;
import randy.quests.EpicQuestTask;
import randy.quests.EpicQuestTask.TaskTypes;

public class TypeEnchant extends TypeBase implements Listener{
	
	@EventHandler
	public void onItemEnchant(EnchantItemEvent event){
		
		//Get player and questlist
		Player player = (Player)event.getInventory().getHolder();
		EpicPlayer epicPlayer = EpicSystem.getEpicPlayer(player.getName());
		List<EpicQuestTask> taskList = epicPlayer.getTasksByType(TaskTypes.ENCHANT_ITEM);
		
		for(EpicQuestTask task : taskList){
			Material itemID = event.getItem().getType();
			String itemNeeded = task.getTaskID();
			
			if(itemID == Material.matchMaterial(itemNeeded)){
				task.ProgressTask(1, epicPlayer);
			}
		}
	}
}
