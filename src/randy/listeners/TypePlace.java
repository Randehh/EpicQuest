package randy.listeners;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import randy.epicquest.EpicQuest;
import randy.epicquest.EpicSystem;

public class TypePlace extends TypeBase implements Listener{

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){

		//Get player and questlist
		Player player = event.getPlayer();
		String playername = player.getName();

		//Block information
		Block block = event.getBlock();
		Material blockplaced = block.getType();
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		Location loc = new Location(null, x, y, z);

		//Check if player has a destroy task
		HashMap<EpicQuest, String> questlist = checkForType(EpicSystem.getEpicPlayer(playername), "place");
		if(!questlist.isEmpty()){
			for(int i = 0; i < questlist.size(); i++){

				//Split quest and task
				EpicQuest quest = (EpicQuest) questlist.keySet().toArray()[i];
				String[] tasks = questlist.get(quest).split(",");

				for(int e = 0; e < tasks.length; e++){

					//Check if correct item was destroyed
					int taskNo = Integer.parseInt(tasks[e]);
					String blockneeded = quest.getTaskID(taskNo);
					if(blockplaced == Material.matchMaterial(blockneeded) &&
							!EpicSystem.getBlockList().contains(loc)){

						//Progress task stuff
						quest.modifyTaskProgress(taskNo, 1, true);

						//Add block to the blocklist
						EpicSystem.getBlockList().add(loc);
					}
				}
			}
		}
	}
}
