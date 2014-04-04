package randy.epicquest;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

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
	
	public static void MoveVillagersBack(){
		Object[] villagerArray = villagerList.keySet().toArray();
		for(int i = 0; i < villagerArray.length; i++){
			Villager tempVil = (Villager)villagerArray[i];
			tempVil.teleport(GetEpicVillager(tempVil).originalLocation);
		}
	}
	
	public static String GetNextOpeningSentence(Villager villager, int quest){
		Random randomGen = new Random();
		List<String> openingSentences = GetEpicVillager(villager).openingSentences.get(quest);
		return openingSentences.get(randomGen.nextInt(openingSentences.size()));
	}
	
	public static String GetRandomMiddleSentence(Villager villager, int quest){
		Random randomGen = new Random();
		List<String> middleSentences = GetEpicVillager(villager).middleSentences.get(quest);
		return middleSentences.get(randomGen.nextInt(middleSentences.size()));
	}
	
	public static String GetNextEndingSentence(Villager villager, int quest){
		Random randomGen = new Random();
		List<String> endingSentences = GetEpicVillager(villager).endingSentences.get(quest);
		return endingSentences.get(randomGen.nextInt(endingSentences.size()));
	}
	
	public static void NextInteraction(Villager villager, EpicPlayer epicPlayer){
		EpicVillager epicVillager = GetEpicVillager(villager);
		
		int currentQuest = epicVillager.currentQuest.get(epicPlayer);
		int actualQuestNo = epicVillager.questList.get(currentQuest);
		int nextSentence = epicVillager.currentSentence.get(epicPlayer) + 1;
		
		if(!epicVillager.startedQuest.get(epicPlayer) && !epicPlayer.hasQuest(actualQuestNo)){		//Player talks for the first time
			if(epicVillager.openingSentences.get(currentQuest).size() != nextSentence){				//Next sentence
				epicPlayer.getPlayer().sendMessage(GetNextOpeningSentence(villager, currentQuest));
				epicVillager.currentSentence.put(epicPlayer, nextSentence);
			}else{																					//Give quest
				epicPlayer.addQuest(new EpicQuest(epicPlayer, actualQuestNo));
				epicVillager.startedQuest.put(epicPlayer, true);
				epicVillager.currentSentence.put(epicPlayer, -1);
			}
		}else if(epicVillager.startedQuest.get(epicPlayer) && !epicPlayer.getQuest(actualQuestNo).getPlayerQuestCompleted()){
			epicPlayer.getPlayer().sendMessage(GetRandomMiddleSentence(villager, currentQuest));
		} else {
			if(epicVillager.endingSentences.get(currentQuest).size() != nextSentence){	
				epicPlayer.getPlayer().sendMessage(GetNextEndingSentence(villager, currentQuest));
				epicVillager.currentSentence.put(epicPlayer, nextSentence);
			}else{
				epicVillager.startedQuest.put(epicPlayer, false);
				epicVillager.currentSentence.put(epicPlayer, -1);
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
}
