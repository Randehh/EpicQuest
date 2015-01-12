package main.java.randy.engine;

import java.util.HashMap;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;

public class EpicAnnouncer {

	public static HashMap<String, String> questAmountCompletedText = new HashMap<String, String>();
	public static HashMap<String, String> questCompletedText = new HashMap<String, String>();

	public static void SendQuestCompletedText(EpicPlayer player, String quest) {
		if (questAmountCompletedText.containsKey(quest)) {
			Bukkit.broadcastMessage(ChatColor.GOLD
					+ questAmountCompletedText.get(quest).replace("<player>",
							player.getPlayer().getName()));
		}

		if (questCompletedText.containsKey(quest)) {
			Bukkit.broadcastMessage(ChatColor.GOLD
					+ questCompletedText.get(quest).replace("<player>",
							player.getPlayer().getName()));
		}
	}
}
