package main.java.randy.quests;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import main.java.randy.engine.EpicPlayer;
import main.java.randy.engine.EpicSystem;

public class EpicQuestRequirement {
	
	public static enum RequirementTypes{
		QUESTS_COMPLETED,
		ITEMS,
		WORLDS,
		LEVEL,
		RESET_TIME
	}
	
	public RequirementTypes type;
	private Object requirement;

	public EpicQuestRequirement(RequirementTypes type, Object requirement){
		this.type = type;
		this.requirement = requirement;
	}
	
	@SuppressWarnings("unchecked")
	public boolean hasRequirement(EpicPlayer ePlayer, String questTag, boolean sendMessage){
		if(isEmpty()) return true;
		Player player = ePlayer.getPlayer();
		switch(type){
		case ITEMS:
			List<ItemStack> items = (List<ItemStack>)requirement;
			for(ItemStack item : items){
				if(!player.getInventory().contains(item.getType(), item.getAmount())){
					if(sendMessage) player.sendMessage(ChatColor.RED + "You require " + item.getAmount() + " " + item.getType().toString().toLowerCase().replace("_", " ") + " to start this quest.");
					return false;
				}
			}
			return true;
		case LEVEL:
			int level = (Integer)requirement;
			if(level > player.getLevel()){
				if(sendMessage) player.sendMessage(ChatColor.RED + "You need to be at least level " + level + " to start this quest.");
				return false;
			}
			return true;
		case QUESTS_COMPLETED:
			List<String> quests = (List<String>)requirement;
			for(String q : quests){
				if(!ePlayer.getQuestsCompleted().contains(q)){
					if(sendMessage) player.sendMessage(ChatColor.RED + "You need to finish the quest '" + q + "' before you can start this quest.");
					return false;
				}
			}
			return true;
		case WORLDS:
			List<String> worlds = (List<String>)requirement;
			boolean isInWorld = false;
			for(String world : worlds){
				if(player.getWorld().getName().equalsIgnoreCase(world)){
					isInWorld = true;
				}
			}
			return isInWorld;
			
		case RESET_TIME:
			int time = (Integer)requirement;
			if(time == -1 && ePlayer.getQuestsCompleted().contains(questTag)){
				if(sendMessage) player.sendMessage(ChatColor.RED + "You can't do this quest anymore.");
				return false;		
			}
			if(EpicSystem.getGlobalTime() - ePlayer.getQuestTimerMap().get(questTag) <= time){
				return true; 
			}else{
				if(sendMessage) player.sendMessage(ChatColor.RED + "You need to wait " + (time - (EpicSystem.getGlobalTime() - ePlayer.getQuestTimerMap().get(questTag))) + " more seconds to do this quest again.");
				return false;		
			}
		default:
			break;
		
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public boolean isEmpty(){
		switch(type){
		case ITEMS:
			List<ItemStack> items = (List<ItemStack>)requirement;
			if(items.isEmpty()) return true;
			break;
		case LEVEL:
			int level = (Integer)requirement;
			if(level <= 0) return true;
			break;
		case QUESTS_COMPLETED:
			List<String> quests = (List<String>)requirement;
			if(quests.isEmpty()) return true;
			break;
		case WORLDS:
			List<String> worlds = (List<String>)requirement;
			if(worlds.isEmpty()) return true;
			break;
			
		case RESET_TIME:
			int time = (Integer)requirement;
			if(time == 0) return true; //Only return true if the player can always get the quest
			break;
		default:
			break;
		
		}
		return false;
	}
}
