package main.java.randy.quests;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import main.java.randy.engine.EpicPlayer;

public class EpicQuestRequirement {
	
	public static enum RequirementTypes{
		QUESTS_COMPLETED,
		ITEMS,
		WORLDS,
		LEVEL
	}
	
	public RequirementTypes type;
	private Object requirement;

	public EpicQuestRequirement(RequirementTypes type, Object requirement){
		this.type = type;
		this.requirement = requirement;
	}
	
	@SuppressWarnings("unchecked")
	public boolean hasRequirement(EpicPlayer ePlayer, boolean sendMessage){
		if(isEmpty()) return true;
		Player player = ePlayer.getPlayer();
		switch(type){
		case ITEMS:
			List<ItemStack> items = (List<ItemStack>)requirement;
			for(ItemStack item : items){
				if(!player.getInventory().contains(item.getType(), item.getAmount())){
					player.sendMessage(ChatColor.RED + "You require " + item.getAmount() + " " + item.getType().toString().toLowerCase().replace("_", " ") + " to start this quest.");
					return false;
				}
			}
			return true;
		case LEVEL:
			int level = (Integer)requirement;
			if(level > player.getLevel()){
				player.sendMessage(ChatColor.RED + "You need to be at least level " + level + " to start this quest.");
				return false;
			}
			return true;
		case QUESTS_COMPLETED:
			List<String> quests = (List<String>)requirement;
			for(String quest : quests){
				if(!ePlayer.getQuestsCompleted().contains(quest)){
					player.sendMessage(ChatColor.RED + "You need to finish the quest '" + quest + "' before you can start this quest.");
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
		default:
			break;
		
		}
		return false;
	}
}
