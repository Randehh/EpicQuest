package randy.epicquest;

import java.util.HashMap;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;

public class EpicAnnouncer {
	
	public static HashMap<Integer, String> questAmountCompletedText = new HashMap<Integer, String>();
	public static HashMap<Integer, String> questCompletedText = new HashMap<Integer, String>();
	
	public static void SendQuestCompletedText(EpicPlayer player, int quest){
		if(questAmountCompletedText.containsKey(quest)){
			Bukkit.broadcastMessage(ChatColor.GOLD + questAmountCompletedText.get(quest).replace("<player>", player.getPlayerName()));
		}
		
		if(questCompletedText.containsKey(quest)){
			Bukkit.broadcastMessage(ChatColor.GOLD + questCompletedText.get(quest).replace("<player>", player.getPlayerName()));
		}
	}
}
