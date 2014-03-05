package randy.listeners;

import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;

import randy.epicquest.EpicSystem;

public class InventoryDrag implements Listener{

	@EventHandler
	public void onInventoryMoveItme(InventoryMoveItemEvent event){
		if(event.getDestination().getType() == InventoryType.FURNACE && event.getSource().getType() == InventoryType.PLAYER){
			Furnace furnace = (Furnace)event.getDestination();
			Player player = (Player)event.getSource().getHolder();
			EpicSystem.furnaceList.put(furnace.getLocation(), EpicSystem.getEpicPlayer(player));
		}
	}
}
