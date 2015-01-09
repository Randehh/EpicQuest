package randy.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import randy.engine.EpicSystem;
import randy.epicquest.EpicMain;

public class DeathListener implements Listener {
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
		if(event.getDrops().isEmpty()){
			return;
		}
		ItemStack itemToRemove = null;
		for(ItemStack item : event.getDrops()){
			if(item != null && item.getType() == Material.WRITTEN_BOOK){
				BookMeta bookMeta = (BookMeta)item.getItemMeta();
				if(bookMeta.getTitle().equals("Quest Book")){
					itemToRemove = item;
				}
			}
		}
		if(itemToRemove != null) event.getDrops().remove(itemToRemove);
	}
	
	@EventHandler
	public void onPlayerRespawn(final PlayerRespawnEvent event){
		Bukkit.getScheduler().scheduleSyncDelayedTask(EpicMain.getInstance(), new Runnable(){
			@Override
			public void run() {
				if(EpicSystem.useBook()){
					EpicSystem.getEpicPlayer(event.getPlayer()).giveQuestBook();
				}
			}
		}, 1);
	}
}
