package main.java.randy.engine;

public class PlayerStatistics {

	private EpicPlayer ePlayer;
	private float moneyEarned;
	private int questsCompleted;
	private int questsDropped;
	private int questsGet;
	private int tasksCompleted;

	public PlayerStatistics(EpicPlayer ePlayer, float moneyEarned,
			int questsCompleted, int questsDropped, int questsGet,
			int tasksCompleted) {
		this.ePlayer = ePlayer;
		this.moneyEarned = moneyEarned;
		this.questsCompleted = questsCompleted;
		this.questsDropped = questsDropped;
		this.questsGet = questsGet;
		this.tasksCompleted = tasksCompleted;
	}

	// Adding
	public void AddMoneyEarned(float money) {
		moneyEarned += money;
		EpicLeaderboard.setTopMoneyEarned(ePlayer);
	}

	public void AddQuestsCompleted(int amount) {
		questsCompleted += amount;
		EpicLeaderboard.setTopQuestsCompletedScore(ePlayer);
	}

	public void AddQuestsDropped(int amount) {
		questsDropped += amount;
	}

	public void AddQuestsGet(int amount) {
		questsGet += amount;
	}

	public void AddTasksCompleted(int amount) {
		tasksCompleted += amount;
		EpicLeaderboard.setTopTasksCompletedScore(ePlayer);
	}

	// Getting
	public float GetMoneyEarned() {
		return moneyEarned;
	}
	public int GetQuestsCompleted() {
		return questsCompleted;
	}
	public int GetQuestsDropped() {
		return questsDropped;
	}
	public int GetQuestsGet() {
		return questsGet;
	}
	public int GetTasksCompleted() {
		return tasksCompleted;
	}
}
