package main.java.randy.engine;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Utils {
	
	public static ItemStack StringToItemStack(String item, String splitChar){
		String[] split = item.split(splitChar);
		return new ItemStack(Material.getMaterial(split[0]), Integer.parseInt(split[1]));
	}
	
	@SuppressWarnings("deprecation")
	public static Player[] getOnlinePlayers(){
		List<Player> playersList = new ArrayList<Player>();
		
		for(Player player : Bukkit.getOnlinePlayers()){
			playersList.add(player);
		}
		
		return playersList.toArray(new Player[playersList.size()]);
	}
}
