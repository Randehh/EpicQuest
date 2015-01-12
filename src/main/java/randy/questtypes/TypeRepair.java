package main.java.randy.questtypes;

import java.util.List;

import main.java.randy.engine.EpicPlayer;
import main.java.randy.engine.EpicSystem;
import main.java.randy.quests.EpicQuestTask;
import main.java.randy.quests.EpicQuestTask.TaskTypes;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

public class TypeRepair extends TypeBase implements Listener {

	@EventHandler
	public void onCraftItem(CraftItemEvent event) {

		Player player = (Player) event.getInventory().getHolder();
		EpicPlayer epicPlayer = EpicSystem.getEpicPlayer(player.getUniqueId());
		List<EpicQuestTask> taskList = epicPlayer
				.getTasksByType(TaskTypes.REPAIR_ITEM);

		for (EpicQuestTask task : taskList) {
			if (event.getInventory().getType() == InventoryType.CRAFTING) {
				CraftingInventory craftInventory = (CraftingInventory) event
						.getInventory();
				ItemStack[] content = craftInventory.getContents();

				String itemNeeded = task.getTaskID();
				Material craftedItem = content[0].getType();

				if (craftedItem != Material.matchMaterial(itemNeeded))
					return;

				int itemsFound = 0;
				for (int i = 1; i < 5; i++) {
					if (content[i].getType() == craftedItem)
						itemsFound++;
				}

				if (itemsFound == 2)
					task.ProgressTask(1, epicPlayer);
				return;
			}
		}
	}

	@EventHandler
	public static void onInventoryClick(InventoryClickEvent e) {
		if (e.isCancelled())
			return;

		HumanEntity ent = e.getWhoClicked();
		if (!(ent instanceof Player))
			return;

		Player player = (Player) ent;
		Inventory inv = e.getInventory();
		if (!(inv instanceof AnvilInventory))
			return;

		EpicPlayer epicPlayer = EpicSystem.getEpicPlayer(player.getUniqueId());
		List<EpicQuestTask> taskList = epicPlayer
				.getTasksByType(TaskTypes.REPAIR_ITEM);

		for (EpicQuestTask task : taskList) {

			AnvilInventory anvil = (AnvilInventory) inv;
			InventoryView view = e.getView();
			int rawSlot = e.getRawSlot();

			if (rawSlot == view.convertSlot(rawSlot)) {

				// 2 = result slot
				if (rawSlot != 2)
					return;

				ItemStack[] items = anvil.getContents();
				ItemStack item1 = items[0];
				ItemStack item3 = e.getCurrentItem();
				// if(item1 == null || item2 == null || item3 == null) return;

				Material id1 = item1.getType();
				Material id3 = item3.getType();

				if (id1 != id3)
					return;

				// See if correct item is repaired
				if (id3 == Material.matchMaterial(task.getTaskID())) {

					ItemMeta meta = item3.getItemMeta();
					if (meta == null)
						return;

					if (meta instanceof Repairable) {
						Repairable repairable = (Repairable) meta;
						int repairCost = repairable.getRepairCost();

						if (player.getLevel() >= repairCost) {
							task.ProgressTask(1, epicPlayer);
						}
					}
				}
			}
		}
	}
}
