package randy.villagers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Villager;

import randy.epicquest.EpicPlayer;
import randy.quests.EpicQuest;

public class EpicVillager {
	
	public enum QuestPhase{
		INTRO_TALK,
		BUSY,
		ENDING_TALK
	}
	
	public Villager villager;
	public HashMap<Integer, SentenceBatch> openingSentences = new HashMap<Integer, SentenceBatch>();
	public HashMap<Integer, SentenceBatch> middleSentences = new HashMap<Integer, SentenceBatch>();
	public HashMap<Integer, SentenceBatch> endingSentences = new HashMap<Integer, SentenceBatch>();
	public List<Integer> questList = new ArrayList<Integer>();
	
	public HashMap<EpicPlayer, Integer> currentQuest = new HashMap<EpicPlayer, Integer>();
	public Location originalLocation;
	
	public HashMap<EpicPlayer, QuestPhase> questPhases = new HashMap<EpicPlayer, QuestPhase>();
	
	public EpicVillager(Villager villager){
		this.villager = villager;
	}
	
	public void NextInteraction(EpicPlayer epicPlayer){
		int currentQuest = this.currentQuest.get(epicPlayer);
		int actualQuestNo = this.questList.get(currentQuest);
		
		QuestPhase currentPhase = questPhases.get(epicPlayer);
		SentenceBatch sentences = null;
		boolean loop = true;
		while(loop){
		switch(currentPhase){
		case INTRO_TALK:
			sentences = openingSentences.get(actualQuestNo);
			epicPlayer.getPlayer().sendMessage(formatMessage(sentences.Next(epicPlayer)));
			if(sentences.IsLast(epicPlayer)){
				epicPlayer.addQuest(new EpicQuest(epicPlayer, actualQuestNo));
				questPhases.put(epicPlayer, QuestPhase.BUSY);
			}
			loop = false;
			break;
		case BUSY:
			//Dropped quest?
			if(!epicPlayer.hasQuest(actualQuestNo)){
				questPhases.put(epicPlayer, QuestPhase.INTRO_TALK);
				currentPhase = QuestPhase.INTRO_TALK;
				continue;
			}
			
			//Not completed quest
			if(!epicPlayer.getQuestByNumber(actualQuestNo).getPlayerQuestCompleted()){
				sentences = middleSentences.get(actualQuestNo);
				epicPlayer.getPlayer().sendMessage(formatMessage(sentences.Random(epicPlayer)));
				loop = false;
				break;
			}
			
			//Completed quest
			questPhases.put(epicPlayer, QuestPhase.ENDING_TALK);
			currentPhase = QuestPhase.ENDING_TALK;		
			continue;
		case ENDING_TALK:
			sentences = endingSentences.get(actualQuestNo);
			epicPlayer.getPlayer().sendMessage(formatMessage(sentences.Next(epicPlayer)));
			if(sentences.IsLast(epicPlayer)){
				epicPlayer.completeQuest(epicPlayer.getQuestByNumber(actualQuestNo));
				questPhases.put(epicPlayer, QuestPhase.INTRO_TALK);
			}
			loop = false;
			break;
		default:
			loop = false;
			break;
		}
		}
	}
	
	public void SetFirstInteraction(EpicPlayer epicPlayer){
		if(currentQuest.get(epicPlayer) == null) currentQuest.put(epicPlayer, 0);
		if(questPhases.get(epicPlayer) == null) questPhases.put(epicPlayer, QuestPhase.INTRO_TALK);
	}
	
	private String formatMessage(String message){
		return ChatColor.ITALIC + villager.getCustomName() + ": " + message;
	}
}
