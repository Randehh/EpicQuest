package randy.engine;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Utils {
	
	public static ItemStack StringToItemStack(String item, String splitChar){
		String[] split = item.split(splitChar);
		return new ItemStack(Material.getMaterial(split[0]), Integer.parseInt(split[1]));
	}
}
