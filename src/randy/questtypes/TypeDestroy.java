package randy.questtypes;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import randy.epicquest.EpicPlayer;
import randy.epicquest.EpicSign;
import randy.epicquest.EpicSystem;
import randy.quests.EpicQuestTask;
import randy.quests.EpicQuestTask.TaskTypes;

public class TypeDestroy extends TypeBase implements Listener{

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		
		//Get player and questlist
		Player player = event.getPlayer();
		EpicPlayer epicPlayer = EpicSystem.getEpicPlayer(player.getName());
		List<EpicQuestTask> taskList = epicPlayer.getTasksByType(TaskTypes.DESTROY_BLOCK);
		
		//Block information
		Block block = event.getBlock();
		Material blockdestroyed = block.getType();
		
		//Check for sign		
		List<EpicSign> signList = EpicSystem.getSignList();
		for(int i = 0; i < signList.size(); i++ ){
			EpicSign sign = signList.get(i);
			if(sign.getLocation().equals(block.getLocation())){
				if(epicPlayer.hasPermission("epicquest.admin.sign")){
					signList.remove(sign);
					return;
				}else{
					event.setCancelled(true);
				}
			}
		}
		
		//Check block list
		if(EpicSystem.getBlockList().contains(block.getLocation().toVector())) return;
		
		for(EpicQuestTask task : taskList){
			String blockneeded = task.getTaskID();
			
			if(blockdestroyed == Material.matchMaterial(blockneeded)){
				task.ProgressTask(1, epicPlayer);
				EpicSystem.getBlockList().add(block.getLocation().toVector());
			}
		}
	}
}
