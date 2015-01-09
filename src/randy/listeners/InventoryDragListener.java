package randy.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.FurnaceInventory;

import randy.engine.EpicSystem;

public class InventoryDragListener implements Listener{	
	@EventHandler
	public void onInventoryMoveItem(InventoryClickEvent event){
		if(event.getInventory().getType().equals(InventoryType.FURNACE) && event.getRawSlot() == 0){
			
			Player player = (Player)event.getWhoClicked();
			FurnaceInventory fi = (FurnaceInventory)event.getInventory();
			EpicSystem.furnaceList.put(fi.getHolder().getLocation(), EpicSystem.getEpicPlayer(player));
		}
	}
}
