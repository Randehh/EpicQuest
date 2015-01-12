package main.java.randy.questentities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.java.randy.engine.EpicSystem;
import main.java.randy.filehandlers.SaveLoader;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.ChatColor;

public class QuestEntityHandler {

	// List
	public static HashMap<Entity, QuestEntity> entityList = new HashMap<Entity, QuestEntity>();
	public static List<QuestEntity> newEntities = new ArrayList<QuestEntity>();

	/*
	 * General methods
	 */
	public static Entity GetEntity(World world, String name) {

		Object[] entityArray = entityList.keySet().toArray();
		for (int i = 0; i < entityArray.length; i++) {
			Entity tempEntity = (Entity) entityArray[i];
			if (tempEntity.getWorld().equals(world)
					&& getEntityName(tempEntity).equalsIgnoreCase(name)) {
				return tempEntity;
			}
		}

		List<Entity> entityList = world.getEntities();
		for (Entity entity : entityList) {
			if (getEntityName(entity).equalsIgnoreCase(name)) {
				return entity;
			}
		}
		return null;
	}

	public static QuestEntity GetQuestEntity(Entity entity) {
		return entityList.get(entity);
	}

	public static QuestEntity GetQuestEntity(World world, String name) {
		return entityList.get(GetEntity(world, name));
	}

	public static List<QuestEntity> GetQuestEntityList() {
		Object[] entityArray = entityList.values().toArray();
		List<QuestEntity> entityList = new ArrayList<QuestEntity>();
		for (int i = 0; i < entityArray.length; i++) {
			entityList.add((QuestEntity) entityArray[i]);
		}
		return entityList;
	}

	public static String getEntityName(Entity entity) {

		String entityName = null;
		if (!EpicSystem.useCitizens()) {
			if (entity instanceof Villager)
				entityName = ((Villager) entity).getCustomName();
		} else {
			if (CitizensAPI.getNPCRegistry().isNPC(entity)) {
				NPC citizensNPC = CitizensAPI.getNPCRegistry().getNPC(entity);
				entityName = ChatColor.stripColor(citizensNPC.getName());
			}
		}
		if (entityName == null)
			entityName = entity.getType().toString();
		return entityName;
	}

	public static void Reload() {
		try {
			SaveLoader.saveQuestEntities(false);
		} catch (IOException e) {
			e.printStackTrace();
		}

		entityList.clear();
		newEntities.clear();

		SaveLoader.loadQuestEntities();
	}

	/*
	 * Villager specific methods
	 */
	public static Villager SpawnVillager(World world, Location loc, String name) {
		if (EpicSystem.useCitizens())
			return null;

		// Check if villager exists
		if (GetEntity(world, name) != null) {
			RemoveVillager(world, name);
		}

		// Set properties
		Villager villager = (Villager) world.spawnEntity(loc,
				EntityType.VILLAGER);
		villager.setCustomName(name);
		villager.setCustomNameVisible(true);
		villager.setAgeLock(true);
		villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,
				1000000000, 100000000));
		villager.setCanPickupItems(false);

		QuestEntity epicVillager = new QuestEntity(villager);
		epicVillager.originalLocation = loc;

		entityList.put(villager, epicVillager);

		return villager;
	}

	public static boolean RemoveVillager(World world, String name) {
		if (EpicSystem.useCitizens())
			return false;
		Entity entity = GetEntity(world, name);
		if (entity != null) {
			entityList.remove(entity);
			entity.remove();
			return true;
		}
		return false;
	}

	public static void MoveVillagersBack() {
		if (EpicSystem.useCitizens())
			return;

		Object[] villagerArray = entityList.keySet().toArray();
		for (Object entity : villagerArray) {
			if (!(entity instanceof Villager))
				continue;
			Villager tempVil = (Villager) entity;
			QuestEntity qEntity = GetQuestEntity(tempVil);

			if (qEntity != null && qEntity.originalLocation != null)
				tempVil.teleport(qEntity.originalLocation);
		}
	}

	public static void RemoveLeftoverVillager(String name, World world) {
		if (EpicSystem.useCitizens())
			return;
		List<LivingEntity> entityList = world.getLivingEntities();
		for (int i = 0; i < entityList.size(); i++) {
			if (entityList.get(i).getCustomName() == name) {
				entityList.remove(entityList.get(i));
				break;
			}
		}
	}
}
