package randy.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import randy.engine.EpicSign;
import randy.engine.EpicSystem;
import randy.quests.EpicQuestDatabase;

public class SignListener implements Listener {
	
	@EventHandler
	public void onSignChange(SignChangeEvent event){
		
		//Get variables of the player and sign position
		Player player = event.getPlayer();
		Sign sign = (Sign) event.getBlock().getState();
		int x = sign.getX();
		int y = sign.getY();
		int z = sign.getZ();
		
		if(EpicSystem.getEpicPlayer(player).hasPermission("epicquest.admin.sign")){
			
			//Check which options were written down
			if(event.getLine(0).equalsIgnoreCase("[EpicQuest]")){
				if(event.getLine(1).equalsIgnoreCase("[Give]")){
					if(event.getLine(2).equalsIgnoreCase("Random")){
						
						//Random quest!
						event.setLine(0, "");
						event.setLine(1, "Quest:");
						event.setLine(2, "Random quest!");
						event.setLine(3, "");
						EpicSign epicSign = new EpicSign("EpicQuest_Internal_Random", new Location(null, x, y, z));
						EpicSystem.getSignList().add(epicSign);
						player.sendMessage("Set sign at "+ x + ":"+ y + ":"+ z + " with random quests.");
					}else{
						
						//Specified quest!
						String quest = event.getLine(2);
						if(EpicQuestDatabase.getQuestTags().contains(quest)){
							event.setLine(0, "");
							event.setLine(1, "Quest:");
							event.setLine(2, EpicQuestDatabase.getQuestName(quest));
							event.setLine(3, "");
							EpicSign epicSign = new EpicSign(quest, new Location(null, x, y, z));
							EpicSystem.getSignList().add(epicSign);
							player.sendMessage("Set sign at "+ x + ":"+ y + ":"+ z + " with quest " + quest + ".");
						}else{
							player.sendMessage(ChatColor.RED + "That quest doesn't exist!");
							event.setLine(2, "Not found.");
						}
					}
				}
				
				if(event.getLine(1).equalsIgnoreCase("[Turnin]")){
					event.setLine(0, "");
					event.setLine(1, "Quest:");
					event.setLine(2, "Turn in quests here!");
					event.setLine(3, "");
					EpicSign epicSign = new EpicSign("EpicQuest_Internal_Turnin", new Location(null, x, y, z));
					EpicSystem.getSignList().add(epicSign);
					player.sendMessage("Set sign at "+ x + ":"+ y + ":"+ z + " where quests are turned in.");
				}
			}
		}
	}
}
