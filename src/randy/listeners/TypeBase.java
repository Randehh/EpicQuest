package randy.listeners;

import java.util.HashMap;
import java.util.List;

import randy.epicquest.EpicPlayer;
import randy.epicquest.EpicQuest;

public class TypeBase {

	//Returns a list containing quest:task
	protected static HashMap<EpicQuest, String> checkForType(EpicPlayer epicPlayer, String type){
		HashMap<EpicQuest, String> matchlist = new HashMap<EpicQuest, String>();

		// Get quest list and check if empty
		List<EpicQuest> questlist = epicPlayer.getQuestList();
		if(!questlist.isEmpty()){

			//Scan the list
			for(int i = 0; i < questlist.size(); i++){
				EpicQuest quest = questlist.get(i);

				if(quest.getQuestWorlds().contains(epicPlayer.getPlayer().getWorld().getName()) ||
						quest.getQuestWorlds().get(0).equalsIgnoreCase("any")){
					//Scan the tasks
					String taskString = null;
					int taskamount = quest.getTaskAmount();
					for(int task = 0; task < taskamount; task++){

						//Don't check the quest if the task is finished
						if(!quest.getPlayerTaskCompleted(task)){
							if(quest.getTaskType(task).equalsIgnoreCase(type)){

								//Add the task to the list
								if(taskString == null){
									taskString = ""+task;
								}else{
									taskString += "," + task;
								}

							}
						}
					}

					if(taskString != null){
						matchlist.put(quest, taskString);
					}
				}
			}
		}
		
		if(matchlist.isEmpty()) return null;
		return matchlist;
	}
}
