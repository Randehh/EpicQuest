package randy.questentities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import randy.epicquest.EpicPlayer;
import randy.epicquest.EpicSystem;
import randy.quests.EpicQuest;

public class QuestEntity {
	
	public enum QuestPhase{
		INTRO_TALK,
		BUSY,
		ENDING_TALK
	}
	
	public Entity entity;
	public HashMap<Integer, SentenceBatch> openingSentences = new HashMap<Integer, SentenceBatch>();
	public HashMap<Integer, SentenceBatch> middleSentences = new HashMap<Integer, SentenceBatch>();
	public HashMap<Integer, SentenceBatch> endingSentences = new HashMap<Integer, SentenceBatch>();
	public List<Integer> questList = new ArrayList<Integer>();
	
	public HashMap<EpicPlayer, Integer> currentQuest = new HashMap<EpicPlayer, Integer>();
	public Location originalLocation;
	
	public HashMap<EpicPlayer, QuestPhase> questPhases = new HashMap<EpicPlayer, QuestPhase>();
	
	public QuestEntity(Entity entity){
		this.entity = entity;
	}
	
	@SuppressWarnings("deprecation")
	public void SetBasics(int questNumber){
		List<Integer> questList = new ArrayList<Integer>();
		questList.add(questNumber);

		List<String> openingList = new ArrayList<String>();
		openingList.add("Hey there, could you help me out?");
		List<String> middleList = new ArrayList<String>();
		middleList.add("Come back to me when you are done.");
		List<String> endingList = new ArrayList<String>();
		endingList.add("Awesome, thanks a bunch!");

		openingSentences.put(questList.get(0), new SentenceBatch(openingList));
		middleSentences.put(questList.get(0), new SentenceBatch(middleList));
		endingSentences.put(questList.get(0), new SentenceBatch(endingList));
		this.questList = questList;

		//Set basic vars for every online player
		Player[] players = Bukkit.getOnlinePlayers();
		for(int i = 0; i < players.length; i++){
			EpicPlayer ep = EpicSystem.getEpicPlayer(players[i]);
			SetFirstInteraction(ep);
		}
		
		QuestEntityHandler.entityList.put(entity, this);
		QuestEntityHandler.newEntities.add(this);
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
			if(!epicPlayer.getQuestByNumber(actualQuestNo).isCompleted()){
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
		if(questPhases.get(epicPlayer) == null){
			QuestPhase phase = QuestPhase.INTRO_TALK;
			int questNo = this.questList.get(0);
			if(epicPlayer.hasQuest(questNo)){
				if(epicPlayer.getQuest(questNo).isCompleted()){
					phase = QuestPhase.ENDING_TALK;
				}else{
					phase = QuestPhase.BUSY;
				}
			}
			questPhases.put(epicPlayer, phase);
		}
	}
	
	private String formatMessage(String message){
		return ChatColor.ITALIC + QuestEntityHandler.getEntityName(entity) + ": " + message;
	}
}
