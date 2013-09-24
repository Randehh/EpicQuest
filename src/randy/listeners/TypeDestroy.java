package randy.listeners;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import randy.epicquest.EpicQuest;
import randy.epicquest.EpicSign;
import randy.epicquest.EpicSystem;

public class TypeDestroy extends TypeBase implements Listener{

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		
		//Get player and questlist
		Player player = event.getPlayer();
		String playername = player.getName();
		
		//Block information
		Block block = event.getBlock();
		int blockdestroyed = block.getTypeId();
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		Location loc = new Location(null, x, y, z);
		
		//Check if player has a destroy task
		HashMap<EpicQuest, String> questlist = checkForType(EpicSystem.getEpicPlayer(playername), "destroy");
		if(!questlist.isEmpty()){
			for(int i = 0; i < questlist.size(); i++){
						
				//Split quest and task
				EpicQuest quest = (EpicQuest) questlist.keySet().toArray()[i];
				String[] tasks = questlist.get(quest).split(",");
				
				for(int e = 0; e < tasks.length; e++){

					//Check if correct item was destroyed
					int taskNo = Integer.parseInt(tasks[e]);
					int blockneeded = Integer.parseInt(quest.getTaskID(taskNo));
					if(blockdestroyed == blockneeded &&
							!EpicSystem.getBlockList().contains(loc)){

						//Progress task stuff
						quest.modifyTaskProgress(taskNo, 1, true);

						//Add block to the blocklist
						EpicSystem.getBlockList().add(loc);
					}
				}
			}
		}
		
		/*
		 * Get sign
		 * 
		 * Sign ID = 63, 68, 232
		 */
		List<EpicSign> signList = EpicSystem.getSignList();
		for(int i = 0; i < signList.size(); i++ ){
			EpicSign sign = signList.get(i);
			if(sign.getLocation().equals(loc)){
				signList.remove(sign);
			}
		}
	}
}
