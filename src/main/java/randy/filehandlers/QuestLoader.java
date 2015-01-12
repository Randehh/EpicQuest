package main.java.randy.filehandlers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import main.java.randy.engine.Utils;
import main.java.randy.quests.EpicQuestDatabase;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class QuestLoader {

	/*
	 * Load quests
	 */
	public static void loadQuests() {

		File folder = new File("plugins" + File.separator + "EpicQuest"
				+ File.separator + "Quests");
		String[] fileNames = folder.list();
		if (fileNames.length > 0) {
			for (String questTag : fileNames) {

				questTag = questTag.replace(".yml", "");
				File questFile = new File("plugins" + File.separator
						+ "EpicQuest" + File.separator + "Quests"
						+ File.separator + questTag + ".yml");
				YamlConfiguration quest = YamlConfiguration
						.loadConfiguration(questFile);

				EpicQuestDatabase.AddQuestTag(questTag);
				EpicQuestDatabase.setQuestName(questTag,
						quest.getString("Name"));
				EpicQuestDatabase.setQuestEndInfo(questTag,
						quest.getString("End_Info"));
				EpicQuestDatabase.setQuestStartInfo(questTag,
						quest.getString("Start_Info"));
				EpicQuestDatabase.setQuestResetTime(questTag,
						quest.getInt("Reset_Time"));
				EpicQuestDatabase.setQuestLocked(questTag,
						quest.getStringList("Requirements.QuestsCompleted"));
				EpicQuestDatabase.setQuestLevel(questTag,
						quest.getInt("Requirements.Level"));

				// Get reward info
				EpicQuestDatabase.setRewardMoney(questTag,
						quest.getInt("Rewards.Money"));

				// 'WOOD_SWORD=1'
				List<String> itemRewardList = quest
						.getStringList("Rewards.Item");
				List<ItemStack> itemRewards = new ArrayList<ItemStack>();
				for (String item : itemRewardList) {
					itemRewards.add(Utils.StringToItemStack(item, "="));
				}
				EpicQuestDatabase.setRewardItems(questTag, itemRewards);

				EpicQuestDatabase.setRewardCommand(questTag,
						quest.getStringList("Rewards.Command"));
				EpicQuestDatabase.setRewardHeroesExp(questTag,
						quest.getInt("Rewards.HeroesExp", 0));

				// Get tasks info
				int taskamount = quest.getConfigurationSection("Tasks")
						.getKeys(false).size();
				for (int e = 0; e < taskamount; e++) {
					EpicQuestDatabase.setTaskType(questTag, e,
							quest.getString("Tasks." + e + ".Type"));
					EpicQuestDatabase.setTaskID(questTag, e,
							quest.getString("Tasks." + e + ".id"));
					EpicQuestDatabase.setTaskAmount(questTag, e,
							quest.getInt("Tasks." + e + ".Amount"));
				}

				EpicQuestDatabase.setQuestWorlds(questTag,
						quest.getStringList("Requirements.Worlds"));

				// Get item requirement
				List<String> itemRequirementList = quest
						.getStringList("Requirements.Items");
				List<ItemStack> itemRequirementMaterials = new ArrayList<ItemStack>();
				for (String item : itemRequirementList) {
					itemRequirementMaterials.add(Utils.StringToItemStack(item,
							"="));
				}
				EpicQuestDatabase.setQuestItemsRequired(questTag,
						itemRequirementMaterials);

				EpicQuestDatabase.setQuestAutoComplete(questTag,
						quest.getBoolean("Auto_Complete"));
			}
		}
		System.out.print("[EpicQuest] Done loading " + fileNames.length
				+ " quests.");
	}
}
