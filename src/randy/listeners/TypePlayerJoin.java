package randy.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import randy.epicquest.EpicPlayer;
import randy.epicquest.EpicSystem;
import randy.filehandlers.SaveLoader;

public class TypePlayerJoin implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		EpicSystem.addFirstStart(event.getPlayer().getName());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		if(EpicSystem.getEpicPlayer(event.getPlayer().getName()) != null){
			EpicPlayer epicPlayer = EpicSystem.getEpicPlayer(event.getPlayer().getName());
			SaveLoader.savePlayer(epicPlayer);
		}
	}
}
