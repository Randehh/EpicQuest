package main.java.randy.questtypes;

import java.util.List;

import main.java.randy.engine.EpicPlayer;
import main.java.randy.engine.EpicSystem;
import main.java.randy.quests.EpicQuestTask;
import main.java.randy.quests.EpicQuestTask.TaskTypes;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class TypePlace extends TypeBase implements Listener{

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){

		//Get player and questlist
		Player player = event.getPlayer();
		EpicPlayer epicPlayer = EpicSystem.getEpicPlayer(player.getUniqueId());
		List<EpicQuestTask> taskList = epicPlayer.getTasksByType(TaskTypes.PLACE_BLOCK);

		//Block information
		Block block = event.getBlock();
		Material blockPlaced = block.getType();
		
		//Check block list
		if(EpicSystem.getBlockList().contains(block.getLocation().toVector())) return;

		for(EpicQuestTask task : taskList){
			String blockneeded = task.getTaskID();

			if(blockPlaced == Material.matchMaterial(blockneeded)){
				task.ProgressTask(1, epicPlayer);
				EpicSystem.getBlockList().add(block.getLocation().toVector());
			}
		}
	}
}
