package main.java.randy.listeners;

import main.java.randy.engine.EpicPlayer;
import main.java.randy.engine.EpicSystem;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event){
		Player player = event.getPlayer();
		EpicPlayer epicPlayer = EpicSystem.getEpicPlayer(player.getUniqueId());
		
		if(epicPlayer.partyChat){
			epicPlayer.getParty().sendMessage(epicPlayer, event.getMessage());
			event.setCancelled(true);
		}
	}
}
