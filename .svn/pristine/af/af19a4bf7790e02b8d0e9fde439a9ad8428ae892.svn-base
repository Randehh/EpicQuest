package randy.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import randy.epicquest.EpicQuest;
import randy.epicquest.EpicSystem;

public class TypeKill extends TypeBase implements Listener{
	
	//Create objects to hold tagged entities and temporary player name
	HashMap<String, ArrayList<UUID>> tags = new HashMap<String, ArrayList<UUID>>();
	HashMap<UUID, String> tempnames = new HashMap<UUID, String>();
	
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
				String playername = player.getName();
				
				//Check if player has a tag list, and add the tagged entity to the list
				UUID targetid = event.getEntity().getUniqueId();
				if(!tags.containsKey(playername)){
					tags.put(playername, new ArrayList<UUID>());
				}
				if(!tags.get(playername).contains(targetid)){
					tags.get(playername).add(targetid);
					tempnames.put(targetid, playername);
				}
			}			
		}
	}
	
	/*
	 * Entity death
	 */
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event){
		LivingEntity entity = event.getEntity();
		UUID targetid = entity.getUniqueId();
		String entityname = entity.getType().toString();
		
		//Check if target is tagged and the the player if true
		if(tempnames.containsKey(targetid)){
			String playername = tempnames.get(targetid);
			
			//Check if player has a kill task
			HashMap<EpicQuest, String> questlist = checkForType(EpicSystem.getEpicPlayer(playername), "kill");
			if(!questlist.isEmpty()){
				for(int i = 0; i < questlist.size(); i++){
							
					//Split quest and task
					EpicQuest quest = (EpicQuest) questlist.keySet().toArray()[i];
					String[] tasks = questlist.get(quest).split(",");

					for(int e = 0; e < tasks.length; e++){
						int taskNo = Integer.parseInt(tasks[e]);

						//Check if correct entity was killed
						if(quest.getTaskID(taskNo).equalsIgnoreCase(entityname)){

							//Progress task stuff
							quest.modifyTaskProgress(taskNo, 1, true);
						}
					}
				}
			}
			
			//Player kill
			//Check if player has a kill task
			questlist = checkForType(EpicSystem.getEpicPlayer(playername), "killplayer");
			if(!questlist.isEmpty()){
				for(int i = 0; i < questlist.size(); i++){
							
					//Split quest and task
					EpicQuest quest = (EpicQuest) questlist.keySet().toArray()[i];
					String[] tasks = questlist.get(i).split(",");

					for(int e = 0; e < tasks.length; e++){
						int taskNo = Integer.parseInt(tasks[e]);

						//Check if correct entity was killed
						if(entity instanceof Player){
							Player enemyplayer = (Player) entity;
							String enemyplayername = enemyplayer.getName();
							
							if(quest.getTaskID(taskNo).equalsIgnoreCase(enemyplayername)){
							
								//Progress task stuff
								quest.modifyTaskProgress(taskNo, 1, true);
							}
						}
					}
				}
			}
			
			//Kill any player
			questlist = checkForType(EpicSystem.getEpicPlayer(playername), "killanyplayer");
			if(!questlist.isEmpty()){
				for(int i = 0; i < questlist.size(); i++){
							
					//Split quest and task
					EpicQuest quest = (EpicQuest) questlist.keySet().toArray()[i];
					String[] tasks = questlist.get(i).split(",");

					for(int e = 0; e < tasks.length; e++){
						int taskNo = Integer.parseInt(tasks[e]);

						//Check if correct entity was killed
						if(entity instanceof Player){	
							
							//Progress task stuff
							quest.modifyTaskProgress(taskNo, 1, true);
							
						}
					}
				}
			}
		}
	}
}