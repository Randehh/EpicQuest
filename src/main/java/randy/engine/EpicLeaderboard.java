package main.java.randy.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;

public class EpicLeaderboard {

	// All the lists
	public static HashMap<UUID, Float> questsCompleted = new HashMap<UUID, Float>();
	public static HashMap<UUID, Float> tasksCompleted = new HashMap<UUID, Float>();
	public static HashMap<UUID, Float> moneyEarned = new HashMap<UUID, Float>();

	// Generic functions
	private static List<String> getTopScores(final HashMap<UUID, Float> map) {
		List<UUID> idList = new ArrayList<UUID>();
		List<String> scoreList = new ArrayList<String>();
		if (map.isEmpty())
			return scoreList;
		for (UUID id : map.keySet()) {
			idList.add(id);
		}
		Collections.sort(idList, new Comparator<UUID>() {
			@Override
			public int compare(UUID o1, UUID o2) {
				return (int) (map.get(o2) - map.get(o1));
			}
		});

		for (UUID id : idList) {
			scoreList.add(Bukkit.getOfflinePlayer(id).getName() + ": "
					+ map.get(id).intValue());
		}
		return scoreList;
	}

	private static UUID getLowestScore(HashMap<UUID, Float> map) {
		if (map.isEmpty())
			return null;

		Float lowestScore = Float.MAX_VALUE;
		UUID lowestID = null;
		for (UUID id : map.keySet()) {
			if (map.get(id) < lowestScore) {
				lowestID = id;
			}
		}
		return lowestID;
	}

	/*
	 * QUESTS COMPLETED
	 */
	public static List<String> getTopQuestsCompleted() {
		return getTopScores(questsCompleted);
	}
	public static void setTopQuestsCompletedScore(EpicPlayer ePlayer) {
		Float playerScore = Float.valueOf(ePlayer.playerStatistics
				.GetQuestsCompleted());
		if (questsCompleted.isEmpty()) {
			questsCompleted.put(ePlayer.getPlayerID(), playerScore);
			return;
		}

		UUID lowestID = getLowestScore(questsCompleted);
		if (playerScore > questsCompleted.get(lowestID)) {
			if (questsCompleted.size() == 3)
				questsCompleted.remove(lowestID);
			questsCompleted.put(ePlayer.getPlayerID(), playerScore);
		}
	}

	/*
	 * TASKS COMPLETED
	 */
	public static List<String> getTopTasksCompleted() {
		return getTopScores(tasksCompleted);
	}
	public static void setTopTasksCompletedScore(EpicPlayer ePlayer) {
		Float playerScore = Float.valueOf(ePlayer.playerStatistics
				.GetTasksCompleted());
		if (tasksCompleted.isEmpty()) {
			tasksCompleted.put(ePlayer.getPlayerID(), playerScore);
			return;
		}

		UUID lowestID = getLowestScore(tasksCompleted);
		if (playerScore > tasksCompleted.get(lowestID)) {
			if (tasksCompleted.size() == 3)
				tasksCompleted.remove(lowestID);
			tasksCompleted.put(ePlayer.getPlayerID(), playerScore);
		}
	}

	/*
	 * MONEY EARNED
	 */
	public static List<String> getTopMoneyEarned() {
		return getTopScores(moneyEarned);
	}
	public static void setTopMoneyEarned(EpicPlayer ePlayer) {
		Float playerScore = Float.valueOf(ePlayer.playerStatistics
				.GetMoneyEarned());
		if (moneyEarned.isEmpty()) {
			moneyEarned.put(ePlayer.getPlayerID(), playerScore);
			return;
		}

		UUID lowestID = getLowestScore(moneyEarned);
		if (playerScore > moneyEarned.get(lowestID)) {
			if (moneyEarned.size() == 3)
				moneyEarned.remove(lowestID);
			moneyEarned.put(ePlayer.getPlayerID(), playerScore);
		}
	}
}
