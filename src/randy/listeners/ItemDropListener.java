package randy.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import randy.epicquest.EpicSystem;

public class ItemDropListener  implements Listener{
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event){
		if(!EpicSystem.useBook()) return;
		
		ItemStack droppedItem = event.getItemDrop().getItemStack();
		if(droppedItem.getType() == Material.WRITTEN_BOOK){
			BookMeta book = (BookMeta)droppedItem.getItemMeta();
			if(book.getTitle().equalsIgnoreCase("Quest Book")){
				event.setCancelled(true);
			}
		}
	}
}
