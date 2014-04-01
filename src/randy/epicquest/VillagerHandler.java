package randy.epicquest;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class VillagerHandler {
	
	//List
	public static HashMap<Villager, List<Integer>> villagerList = new HashMap<Villager, List<Integer>>();
	private static HashMap<Villager, Location> originalLocation = new HashMap<Villager, Location>();
	private static HashMap<Villager, List<String>> sentenceList = new HashMap<Villager, List<String>>();
	
	public static boolean SpawnVillager(World world, Location loc, String name, List<Integer> questList){
		
		//Check if villager exists
		if(GetVillager(world, name) != null) return false;
		
		//Set properties
		Villager villager = (Villager) world.spawnEntity(loc, EntityType.VILLAGER);
		villager.setCustomName(name);
		villager.setCustomNameVisible(true);
		villager.setAgeLock(true);
		villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000000, 100000000));
		villager.setCanPickupItems(false);
		
		villagerList.put(villager, questList);
		originalLocation.put(villager, loc);
		
		return true;
	}
	
	public static boolean RemoveVillager(World world, String name){
		
		Villager vil = GetVillager(world, name);
		if(vil != null){
			villagerList.remove(vil);
			originalLocation.remove(vil);
			sentenceList.remove(vil);
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
	
	public static void MoveVillagersBack(){
		Object[] villagerArray = villagerList.keySet().toArray();
		for(int i = 0; i < villagerArray.length; i++){
			Villager tempVil = (Villager)villagerArray[i];
			tempVil.teleport(originalLocation.get(tempVil));
		}
	}
	
	public static String GetRandomSentence(Villager villager){
		Random randomGen = new Random();
		return sentenceList.get(villager).get(randomGen.nextInt(sentenceList.get(villager).size()));
	}
	
	public static void SetRandomSentences(Villager villager, List<String> sentencelist){
		sentenceList.put(villager, sentencelist);
	}
	
	public static List<String> GetRandomSentenceList(Villager villager){
		return sentenceList.get(villager);
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
