package randy.listeners;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import randy.epicquest.EpicPlayer;
import randy.epicquest.EpicQuest;
import randy.epicquest.EpicSign;
import randy.epicquest.EpicSystem;
import randy.epicquest.VillagerHandler;

public class TypePlayerInteract implements Listener{
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		
		//Get player and the action
		Player player = event.getPlayer();
		String playername = player.getName();
		EpicPlayer epicPlayer = EpicSystem.getEpicPlayer(playername);
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
			
			//Get the block and check if it's a sign
			List<EpicSign> signList = EpicSystem.getSignList();
			BlockState block = event.getClickedBlock().getState();
			for(int i = 0; i < signList.size(); i++){
				EpicSign sign = signList.get(i);
				Location signLoc = sign.getLocation();
				Location blockLoc = block.getLocation();
				blockLoc.setWorld(null);
				
				if(signLoc.equals(blockLoc)){
					if(sign.getQuest() == -1){
						if(epicPlayer.canGetQuest()){
							epicPlayer.addQuestRandom();
						}else{
							player.sendMessage(ChatColor.RED + "There are no more quests available.");
						}
					} else if(sign.getQuest() == -2){
						if(!epicPlayer.getCompleteableQuest().isEmpty()){
							epicPlayer.completeAllQuests();
						}else{
							player.sendMessage(ChatColor.RED + "There are no quests to turn in.");
						}
					} else {
						if(epicPlayer.getObtainableQuests().contains(sign.getQuest())){
							epicPlayer.addQuest(new EpicQuest(epicPlayer, sign.getQuest()));
						}else{
							player.sendMessage(ChatColor.RED + "You can't get that quest.");
						}
					}
				}
				
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event){
		Entity clickedEntity = event.getRightClicked();
		if(clickedEntity instanceof Villager){
			Villager villager = (Villager)clickedEntity;
			if(VillagerHandler.villagerList.containsKey(villager)){
				EpicPlayer epicPlayer = EpicSystem.getEpicPlayer(event.getPlayer());
				VillagerHandler.NextInteraction(villager, epicPlayer);
				event.setCancelled(true);
			}
		}
	}
}
