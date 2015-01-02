package randy.villagers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class VillagerHandler {
	
	//List
	public static HashMap<Villager, EpicVillager> villagerList = new HashMap<Villager, EpicVillager>();
	public static List<EpicVillager> newVillagers = new ArrayList<EpicVillager>();
	
	public static boolean SpawnVillager(World world, Location loc, String name){
		
		//Check if villager exists
		if(GetVillager(world, name) != null){
			RemoveVillager(world, name);
		}
		
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
			vil.remove();
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
		
		List<Entity> entityList = world.getEntities();
		for(Entity entity : entityList){
			if(entity instanceof Villager){
				Villager tempVil = (Villager)entity;
				if(tempVil.getCustomName().equalsIgnoreCase(name)){
					return tempVil;
				}
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
