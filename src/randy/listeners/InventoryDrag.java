package randy.listeners;

<<<<<<< HEAD
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.FurnaceInventory;

import randy.epicquest.EpicSystem;

public class InventoryDrag implements Listener{	
	@EventHandler
	public void onInventoryMoveItem(InventoryClickEvent event){
		if(event.getInventory().getType().equals(InventoryType.FURNACE) && event.getRawSlot() == 0){
			
			Player player = (Player)event.getWhoClicked();
			FurnaceInventory fi = (FurnaceInventory)event.getInventory();
			EpicSystem.furnaceList.put(fi.getHolder().getLocation(), EpicSystem.getEpicPlayer(player));
=======
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
>>>>>>> 8a82a9e34f4bbc9b0780432073f9ac9f1f8fe45e
		}
	}
}
