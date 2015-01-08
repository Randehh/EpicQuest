package randy.quests;

import me.confuser.barapi.BarAPI;

import org.bukkit.ChatColor;

import randy.epicquest.EpicPlayer;
import randy.epicquest.EpicSystem;

public class EpicQuestTask {
	
	public static enum TaskTypes{
		CRAFT_ITEM,
		DESTROY_BLOCK,
		ENCHANT_ITEM,
		GO_TO,
		KILL_MOB,
		KILL_PLAYER,
		KILL_ANY_PLAYER,
		KILL_ANY_MOB,
		KILL_MOB_BY_NAME,
		LEVEL_UP,
		PLACE_BLOCK,
		SMELT_ITEM,
		TAME_MOB,
		TALK_TO_VILLAGER,
		REPAIR_ITEM,
		CLICK_BLOCK,
		EXECUTE_COMMAND
	}
	
	private TaskTypes taskType;
	private int taskGoal;
	private int taskCurrent = 0;
	private String taskID;
	private EpicQuest quest;
	
	public EpicQuestTask(TaskTypes taskType, String taskID, int taskGoal, EpicQuest quest){
		this.taskType = taskType;
		this.taskID = taskID;
		this.taskGoal = taskGoal;
		this.quest = quest;
	}

	public void ProgressTask(int amount, EpicPlayer player){
		taskCurrent += amount;
		
		if(player != null && SendText()){
			if(EpicSystem.useBarAPI()){
				BarAPI.setMessage(player.getPlayer(), getPlayerTaskProgressText(), (float)getTaskProgress()/getTaskGoal());
			}else{
				player.getPlayer().sendMessage(getPlayerTaskProgressText());
			}
			
			if(IsComplete() && quest.getQuestAutoComplete() && quest.isCompleted()){
				quest.completeQuest();
			}
		}
	}
	
	public boolean IsComplete(){
		if(taskCurrent >= taskGoal) return true;
		return false;
	}
	
	public boolean SendText(){
		if(taskCurrent > taskGoal) return false;
		return true;
	}
	
	public TaskTypes getType(){
		return taskType;
	}
	
	public String getTaskID(){
		return taskID;
	}
	
	public int getTaskProgress(){
		return taskCurrent;
	}
	
	public int getTaskGoal(){
		return taskGoal;
	}
	
	public void setTaskGoal(int goal){
		taskGoal = goal;
	}
	
	public void setTaskID(String id){
		taskID = id;
	}
	
	public EpicQuest getQuest(){
		return quest;
	}
	
	//Progress text
	public String getPlayerTaskProgressText() {
		//Get information to send the progress
		// ie: Kill 2/10 wolves
		// ie2: Killed 2 wolves
		TaskTypes questtype = getType();
		int taskmax = getTaskGoal();
		String taskID = getTaskID();
		
		if(IsComplete()){
			switch(questtype){
			case CRAFT_ITEM:
				return ChatColor.GREEN + "Crafted " + taskmax + " " + taskID.toLowerCase().replace("_", " ");
			case DESTROY_BLOCK:
				return ChatColor.GREEN + "Destroyed " + taskmax + " " + taskID.toLowerCase().replace("_", " ");
			case ENCHANT_ITEM:
				return ChatColor.GREEN + "Enchanted " + taskmax + " " + taskID.toLowerCase().replace("_", " ");
			case GO_TO:
				return ChatColor.GREEN + "Went to " + taskID;
			case KILL_ANY_PLAYER:
				return ChatColor.GREEN + "Killed " + taskmax + " players";
			case KILL_MOB:
				return ChatColor.GREEN + "Killed " + taskmax + " " + taskID.toLowerCase().replace("_", " ") + "s";
			case KILL_PLAYER:
				return ChatColor.GREEN + "Killed " + taskID.toLowerCase().replace("_", " ") + " " + taskmax + " times";
			case LEVEL_UP:
				return ChatColor.GREEN + "Leveled up " + taskmax + " times";
			case PLACE_BLOCK:
				return ChatColor.GREEN + "Placed " + taskmax + " " + taskID.toLowerCase().replace("_", " ");
			case SMELT_ITEM:
				return ChatColor.GREEN + "Smelted " + taskmax + " " + taskID.toLowerCase().replace("_", " ");
			case TALK_TO_VILLAGER:
				return ChatColor.GREEN + "Talked to " + taskID.toLowerCase().replace("_", " ");
			case TAME_MOB:
				return ChatColor.GREEN + "Tamed " + taskmax + " " + taskID.toLowerCase().replace("_", " ");
			case KILL_ANY_MOB:
				return ChatColor.GREEN + "Killed " + taskmax + " mobs";
			case KILL_MOB_BY_NAME:
				return ChatColor.GREEN + "Killed " + taskmax + " " + taskID;
			case REPAIR_ITEM:
				return ChatColor.GREEN + "Repaired " + taskmax + " " + taskID.toLowerCase().replace("_", " ");
			case CLICK_BLOCK:
				return ChatColor.GREEN + "Clicked " + taskID;
			case EXECUTE_COMMAND:
				return ChatColor.GREEN + "Executed command  " + taskID;
			default:
				return null;
			}
		}else{
			int taskprogress = getTaskProgress();
			switch(questtype){
			case CRAFT_ITEM:
				return ChatColor.RED + "Craft " + taskprogress + "/" + taskmax + " " + taskID.toLowerCase().replace("_", " ");
			case DESTROY_BLOCK:
				return ChatColor.RED + "Destroy " + taskprogress + "/" + taskmax + " " + taskID.toLowerCase().replace("_", " ");
			case ENCHANT_ITEM:
				return ChatColor.RED + "Enchant " + taskprogress + "/" + taskmax + " " + taskID.toLowerCase().replace("_", " ");
			case GO_TO:
				return ChatColor.RED + "Go to " + taskID;
			case KILL_ANY_PLAYER:
				return ChatColor.RED + "Kill " + taskprogress + "/" + taskmax + " players";
			case KILL_MOB:
				return ChatColor.RED + "Kill " + taskprogress + "/" + taskmax + " " + taskID.toLowerCase().replace("_", " ") + "s";
			case KILL_PLAYER:
				return ChatColor.RED + "Kill " + taskID.toLowerCase().replace("_", " ") + " " + taskprogress + "/" + taskmax + " times";
			case LEVEL_UP:
				return ChatColor.RED + "Level up " + taskprogress + "/" + taskmax + " times";
			case PLACE_BLOCK:
				return ChatColor.RED + "Place " + taskprogress + "/" + taskmax + " " + taskID.toLowerCase().replace("_", " ");
			case SMELT_ITEM:
				return ChatColor.RED + "Smelt " + taskprogress + "/" + taskmax + " " + taskID.toLowerCase().replace("_", " ");
			case TALK_TO_VILLAGER:
				return ChatColor.RED + "Talk to " + taskID.toLowerCase().replace("_", " ");
			case TAME_MOB:
				return ChatColor.RED + "Tame " + taskprogress + "/" + taskmax + " " + taskID.toLowerCase().replace("_", " ");
			case KILL_ANY_MOB:
				return ChatColor.RED + "Kill " + taskprogress + "/" + taskmax + " mobs";
			case KILL_MOB_BY_NAME:
				return ChatColor.RED + "Kill " + taskprogress + "/" + taskmax + " " + taskID;
			case REPAIR_ITEM:
				return ChatColor.RED + "Repair " + taskprogress + "/" + taskmax + " " + taskID.toLowerCase().replace("_", " ");
			case CLICK_BLOCK:
				return ChatColor.RED + "Click " + taskID;
			case EXECUTE_COMMAND:
				return ChatColor.RED + "Execute command  " + taskID;
			default:
				return null;
			}
		}
	}
	
	//Text to enum
	public static TaskTypes getTaskTypeFromText(String text){
		if(text.equalsIgnoreCase("kill")) return TaskTypes.KILL_MOB;
		if(text.equalsIgnoreCase("killplayer")) return TaskTypes.KILL_PLAYER;
		if(text.equalsIgnoreCase("killanyplayer")) return TaskTypes.KILL_ANY_PLAYER;
		if(text.equalsIgnoreCase("destroy")) return TaskTypes.DESTROY_BLOCK;
		if(text.equalsIgnoreCase("place")) return TaskTypes.PLACE_BLOCK;
		if(text.equalsIgnoreCase("levelup")) return TaskTypes.LEVEL_UP;
		if(text.equalsIgnoreCase("enchant")) return TaskTypes.ENCHANT_ITEM;
		if(text.equalsIgnoreCase("craft")) return TaskTypes.CRAFT_ITEM;
		if(text.equalsIgnoreCase("tame")) return TaskTypes.TAME_MOB;
		if(text.equalsIgnoreCase("smelt")) return TaskTypes.SMELT_ITEM;
		if(text.equalsIgnoreCase("talktovillager")) return TaskTypes.TALK_TO_VILLAGER;
		if(text.equalsIgnoreCase("killanymob")) return TaskTypes.KILL_ANY_MOB;
		if(text.equalsIgnoreCase("killmobbyname")) return TaskTypes.KILL_MOB_BY_NAME;
		if(text.equalsIgnoreCase("repair")) return TaskTypes.REPAIR_ITEM;
		if(text.equalsIgnoreCase("clickblock")) return TaskTypes.CLICK_BLOCK;
		if(text.equalsIgnoreCase("executecommand")) return TaskTypes.EXECUTE_COMMAND;
		if(text.equalsIgnoreCase("goto")) return TaskTypes.GO_TO;
		return null;
	}
}
