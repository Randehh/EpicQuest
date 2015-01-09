package randy.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import randy.epicquest.EpicSystem;

public class DeathListener  implements Listener {
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
		for(ItemStack item : event.getDrops()){
			if(item != null && item.getType() == Material.WRITTEN_BOOK){
				BookMeta bookMeta = (BookMeta)item.getItemMeta();
				if(bookMeta.getTitle().equals("Quest Book")) event.getDrops().remove(item);
			}
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event){
		if(EpicSystem.useBook()) EpicSystem.getEpicPlayer(event.getPlayer()).giveQuestBook();
	}
}
