package randy.questtypes;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import randy.epicquest.EpicPlayer;
import randy.epicquest.EpicSystem;
import randy.quests.EpicQuestTask;
import randy.quests.EpicQuestTask.TaskTypes;
import randy.villagers.VillagerHandler;

public class TypeKill extends TypeBase implements Listener{
	
	//Create objects to hold tagged entities
	HashMap<UUID, DamageTag> tagList = new HashMap<UUID, DamageTag>();
	
	/*
	 * Entity damaged
	 */
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event){
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent entEvent = (EntityDamageByEntityEvent)event;
			if(entEvent.getDamager() instanceof Player){
				
				//Get information on player and target
				Player player = (Player)entEvent.getDamager();
				UUID targetid = event.getEntity().getUniqueId();

				DamageTag tag = getDamageTag(targetid);
				tag.addPlayer(EpicSystem.getEpicPlayer(player));
			}
		}
		
		Entity ent = event.getEntity();
		if(ent instanceof Villager){
			Villager villager = (Villager)ent;
			if(VillagerHandler.villagerList.containsKey(villager)) event.setCancelled(true);
		}
	}
	
	private DamageTag getDamageTag(UUID id){
		if(!tagList.containsKey(id)) tagList.put(id, new DamageTag());
		return tagList.get(id);
	}
	
	/*
	 * Entity death
	 */
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event){
		LivingEntity entity = event.getEntity();
		UUID targetid = entity.getUniqueId();
		String entityname = entity.getType().toString();
		
		if(!tagList.containsKey(targetid)) return;
		
		for(EpicPlayer player : tagList.get(targetid).getPlayerList()){
			
			//Kill mob
			List<EpicQuestTask> taskList = player.getTasksByType(TaskTypes.KILL_MOB);
			for(EpicQuestTask task : taskList){				
				if(entityname.equalsIgnoreCase(task.getTaskID())){
					task.ProgressTask(1, player);
				}
			}
			
			//Kill any player
			taskList = player.getTasksByType(TaskTypes.KILL_ANY_PLAYER);
			for(EpicQuestTask task : taskList){				
				if(entity instanceof Player){
					task.ProgressTask(1, player);
				}
			}
			
			//Kill specific player
			taskList = player.getTasksByType(TaskTypes.KILL_PLAYER);
			for(EpicQuestTask task : taskList){				
				if(entity instanceof Player && ((Player)entity).getName().equalsIgnoreCase(task.getTaskID())){
					task.ProgressTask(1, player);
				}
			}
			
			//Kill any mob
			taskList = player.getTasksByType(TaskTypes.KILL_ANY_MOB);
			for(EpicQuestTask task : taskList){				
				if(!(entity instanceof Player)){
					task.ProgressTask(1, player);
				}
			}
			
			//Kill mob by name
			taskList = player.getTasksByType(TaskTypes.KILL_MOB_BY_NAME);
			for(EpicQuestTask task : taskList){				
				if(!(entity instanceof Player)){
					String name = entity.getType().toString();
					if(entity.getCustomName() != null) name = entity.getCustomName();
					name = ChatColor.stripColor(name);
					if(name.equalsIgnoreCase(task.getTaskID())){
						task.ProgressTask(1, player);
					}
				}
			}
		}
		
		tagList.remove(tagList.get(targetid));
	}
}