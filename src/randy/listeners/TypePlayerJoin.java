package randy.listeners;

import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import randy.epicquest.EpicPlayer;
import randy.epicquest.EpicSystem;
import randy.epicquest.EpicVillager;
import randy.epicquest.VillagerHandler;
import randy.filehandlers.SaveLoader;

public class TypePlayerJoin implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		EpicSystem.addFirstStart(event.getPlayer().getName());
		
		//Set basic stuff for villager
		List<EpicVillager> villagerList = VillagerHandler.GetEpicVillagerList();
		for(int i = 0; i < villagerList.size(); i++){
			VillagerHandler.SetFirstInteraction(EpicSystem.getEpicPlayer(event.getPlayer()), villagerList.get(i));
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		if(EpicSystem.getEpicPlayer(event.getPlayer().getName()) != null){
			EpicPlayer epicPlayer = EpicSystem.getEpicPlayer(event.getPlayer().getName());
			SaveLoader.savePlayer(epicPlayer);
		}
	}
}
