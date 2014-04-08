package randy.epicquest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class VillagerHandler {
	
	//List
	public static HashMap<Villager, EpicVillager> villagerList = new HashMap<Villager, EpicVillager>();
	
	public static boolean SpawnVillager(World world, Location loc, String name){
		
		//Check if villager exists
		if(GetVillager(world, name) != null) return false;
		
		//Set properties
		Villager villager = (Villager) world.spawnEntity(loc, EntityType.VILLAGER);
		villager.setCustomName(name);
		villager.setCustomNameVisible(true);
		villager.setAgeLock(true);
		villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000000, 100000000));
		villager.setCanPickupItems(false);
		
		EpicVillager epicVillager = new EpicVillager(villager);
		epicVillager.originalLocation = loc;
		
		villagerList.put(villager, epicVillager);
		
		return true;
	}
	
	public static boolean RemoveVillager(World world, String name){
		
		Villager vil = GetVillager(world, name);
		if(vil != null){
			villagerList.remove(vil);
			return true;
		}
		return false;
	}
	
	public static Villager GetVillager(World world, String name){
		Object[] villagerArray = villagerList.keySet().toArray();
		for(int i = 0; i < villagerArray.length; i++){
			Villager tempVil = (Villager)villagerArray[i];
			if(tempVil.getWorld().equals(world) && tempVil.getCustomName().equalsIgnoreCase(name)){
				return tempVil;
			}
		}
		return null;
	}
	
	public static EpicVillager GetEpicVillager(Villager villager){
		return villagerList.get(villager);
	}
	
	public static EpicVillager GetEpicVillager(World world, String name){
		return villagerList.get(GetVillager(world, name));
	}
	
	public static List<EpicVillager> GetEpicVillagerList(){
		Object[] villagerArray = villagerList.values().toArray();
		List<EpicVillager> villagerList = new ArrayList<EpicVillager>();
		for(int i = 0; i < villagerArray.length; i++){
			villagerList.add((EpicVillager)villagerArray[i]);
		}
		return villagerList;
	}
	
	public static void MoveVillagersBack(){
		Object[] villagerArray = villagerList.keySet().toArray();
		for(int i = 0; i < villagerArray.length; i++){
			Villager tempVil = (Villager)villagerArray[i];
			tempVil.teleport(GetEpicVillager(tempVil).originalLocation);
		}
	}
	
	public static String GetNextOpeningSentence(Villager villager, int quest, EpicPlayer epicPlayer){
		List<String> openingSentences = GetEpicVillager(villager).openingSentences.get(quest);
		return openingSentences.get(GetEpicVillager(villager).currentSentence.get(epicPlayer));
	}
	
	public static String GetRandomMiddleSentence(Villager villager, int quest){
		Random randomGen = new Random();
		List<String> middleSentences = GetEpicVillager(villager).middleSentences.get(quest);
		return middleSentences.get(randomGen.nextInt(middleSentences.size()));
	}
	
	public static String GetNextEndingSentence(Villager villager, int quest, EpicPlayer epicPlayer){
		List<String> endingSentences = GetEpicVillager(villager).endingSentences.get(quest);
		return endingSentences.get(GetEpicVillager(villager).currentSentence.get(epicPlayer));
	}
	
	public static void NextInteraction(Villager villager, EpicPlayer epicPlayer){
		EpicVillager epicVillager = GetEpicVillager(villager);
		String villagerName = villager.getCustomName();
		
		int currentQuest = epicVillager.currentQuest.get(epicPlayer);
		int actualQuestNo = epicVillager.questList.get(currentQuest);
		int nextSentence = epicVillager.currentSentence.get(epicPlayer) + 1;
		
		if(epicPlayer.hasQuest(actualQuestNo)){
			epicVillager.startedQuest.put(epicPlayer, true);
		}
		if(!epicVillager.startedQuest.get(epicPlayer)){		//Player talks for the first time
			if(epicVillager.openingSentences.get(actualQuestNo).size() != nextSentence){				//Next sentence
				epicVillager.currentSentence.put(epicPlayer, nextSentence);
				epicPlayer.getPlayer().sendMessage(ChatColor.ITALIC + villagerName + ": " + GetNextOpeningSentence(villager, actualQuestNo, epicPlayer));
			}else{																				//Give quest
				if(epicPlayer.addQuest(new EpicQuest(epicPlayer, actualQuestNo))){
					epicVillager.startedQuest.put(epicPlayer, true);
					epicVillager.currentSentence.put(epicPlayer, -1);
				}else{
					epicVillager.currentSentence.put(epicPlayer, 0);
					epicPlayer.getPlayer().sendMessage(ChatColor.ITALIC + villagerName + ": " + GetNextOpeningSentence(villager, actualQuestNo, epicPlayer));
				}
			}
		}else if(epicVillager.startedQuest.get(epicPlayer)){
			
			//Check if player dropped the quest
			if(!epicPlayer.hasQuest(actualQuestNo)){
				epicVillager.startedQuest.put(epicPlayer, false);
				epicVillager.currentSentence.put(epicPlayer, 0);
				epicPlayer.getPlayer().sendMessage(ChatColor.ITALIC + villagerName + ": " + GetNextOpeningSentence(villager, actualQuestNo, epicPlayer));
			}else{
				if(!epicPlayer.getQuest(actualQuestNo).getPlayerQuestCompleted()){
					epicPlayer.getPlayer().sendMessage(ChatColor.ITALIC + villagerName + ": " + GetRandomMiddleSentence(villager, actualQuestNo));
				}else if(epicVillager.endingSentences.get(actualQuestNo).size() != nextSentence){
					epicPlayer.getPlayer().sendMessage(ChatColor.ITALIC + villagerName + ": " + GetNextEndingSentence(villager, actualQuestNo, epicPlayer));
					epicVillager.currentSentence.put(epicPlayer, nextSentence);
				}else{
					//Finish quest
					epicVillager.startedQuest.put(epicPlayer, false);
					epicVillager.currentSentence.put(epicPlayer, -1);
					epicPlayer.getQuest(actualQuestNo).completeQuest();
					currentQuest++;
					if(currentQuest < epicVillager.questList.size()){
						epicVillager.currentQuest.put(epicPlayer, currentQuest);
					}else{
						epicVillager.currentQuest.put(epicPlayer, 0);
					}
				}
			}
		}
	}
	
	public static void RemoveLeftoverVillager(String name, World world){
		List<LivingEntity> entityList = world.getLivingEntities();
		for(int i = 0; i < entityList.size(); i++){
			if(entityList.get(i).getCustomName() == name){
				entityList.remove(entityList.get(i));
				break;
			}
		}
	}
	
	public static void SetFirstInteraction(EpicPlayer epicPlayer, EpicVillager epicVillager){
		if(epicVillager.currentQuest.get(epicPlayer) == null) epicVillager.currentQuest.put(epicPlayer, 0);
		if(epicVillager.currentSentence.get(epicPlayer) == null) epicVillager.currentSentence.put(epicPlayer, -1);
		if(epicVillager.startedQuest.get(epicPlayer) == null) epicVillager.startedQuest.put(epicPlayer, false);
	}
}
