package main.java.randy.quests;

import java.util.ArrayList;
import java.util.List;

import main.java.randy.engine.EpicAnnouncer;
import main.java.randy.engine.EpicPlayer;
import main.java.randy.engine.EpicSystem;
import main.java.randy.quests.EpicQuestTask.TaskTypes;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class EpicQuest {
	
	private EpicPlayer epicPlayer;
	
	private String questTag;
	private List<EpicQuestTask> questTasks = new ArrayList<EpicQuestTask>();
	
	public EpicQuest(String questTag){
		this.questTag = questTag;
	}
	
	public void setEpicPlayer(EpicPlayer epicPlayer){
		this.epicPlayer = epicPlayer;
		int totalTaskAmount = EpicQuestDatabase.getTaskTotal(questTag);
		for(int i = 0; i < totalTaskAmount; i ++){	
			TaskTypes taskType = EpicQuestDatabase.getTaskType(questTag, i);
			
			EpicQuestTask task = new EpicQuestTask(
					taskType,
					EpicQuestDatabase.getTaskID(questTag, i), 
					EpicQuestDatabase.getTaskAmount(questTag, i),
					this);
			questTasks.add(task);
			epicPlayer.addTask(task);
		}
	}
	
	/*
	 * 
	 * Standard quest info applicable to every quest
	 * 
	 */
	public EpicPlayer getEpicPlayer(){ return epicPlayer; }
	public List<EpicQuestTask> getTasks(){ return questTasks; }
	public String getQuestTag(){ return questTag; }
	public String getQuestName(){ return EpicQuestDatabase.getQuestName(questTag); }
	public String getQuestStart(){ return EpicQuestDatabase.getQuestStartInfo(questTag); }
	public String getQuestEnd(){ return EpicQuestDatabase.getQuestEndInfo(questTag); }
	public List<EpicQuestReward> getQuestRewards(){ return EpicQuestDatabase.getRewards(questTag); }
	public Boolean getQuestAutoComplete() { return EpicQuestDatabase.getQuestAutoComplete(questTag); }
	public void completeQuest(){
		
		//Basics
		Player player = epicPlayer.getPlayer();
		
		for(EpicQuestReward reward : getQuestRewards()){
			reward.GiveRewards(epicPlayer);
		}
		
		//Send ending text
		player.sendMessage(""+ChatColor.GRAY + ChatColor.ITALIC + getQuestEnd());
		
		EpicAnnouncer.SendQuestCompletedText(epicPlayer, this.questTag);
		
		epicPlayer.playerStatistics.AddQuestsCompleted(1);
		
		if(!epicPlayer.getQuestsCompleted().contains(questTag)) epicPlayer.getQuestsCompleted().add(questTag);
		epicPlayer.setQuestTimer(questTag, EpicSystem.getGlobalTime());
		epicPlayer.getQuestList().remove(this);
	}
	
	/*
	 * 
	 * Player progress
	 * 
	 */
	public boolean isCompleted(){ for(int i = 0; i < questTasks.size(); i++){ if( !questTasks.get(i).IsComplete() ){ return false; } } return true; }
	
	/*
	 * 
	 * Calculating methods
	 * 
	 */
	public int getTaskAmount(){ return questTasks.size(); }
	
	/*
	 * 
	 * Misc
	 * 
	 */
	public static void ResetQuestTaskInfo(){
		for(EpicPlayer player : EpicSystem.getPlayerList()){
			for(EpicQuest quest : player.getQuestList()){
				for(int i = 0; i < quest.getTaskAmount(); i++){
					EpicQuestTask task = quest.getTasks().get(i);
					task.setTaskID(EpicQuestDatabase.getTaskID(quest.getQuestTag(), i));
					task.setTaskGoal(EpicQuestDatabase.getTaskAmount(quest.getQuestTag(), i));
				}
			}
		}
	}
}
