package main.java.randy.questentities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.java.randy.engine.EpicPlayer;
import main.java.randy.engine.EpicSystem;
import main.java.randy.engine.Utils;
import main.java.randy.quests.EpicQuest;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class QuestEntity {
	
	public enum QuestPhase{
		INTRO_TALK,
		BUSY,
		ENDING_TALK
	}
	
	public Entity entity;
	public HashMap<String, SentenceBatch> openingSentences = new HashMap<String, SentenceBatch>();
	public HashMap<String, SentenceBatch> middleSentences = new HashMap<String, SentenceBatch>();
	public HashMap<String, SentenceBatch> endingSentences = new HashMap<String, SentenceBatch>();
	public SentenceBatch neutralSentences;
	public List<String> questList = new ArrayList<String>();
	
	public HashMap<EpicPlayer, String> currentQuest = new HashMap<EpicPlayer, String>();
	public Location originalLocation;
	
	public HashMap<EpicPlayer, QuestPhase> questPhases = new HashMap<EpicPlayer, QuestPhase>();
	
	public QuestEntity(Entity entity){
		this.entity = entity;
	}
	
	public void SetBasics(String questTag){
		List<String> questList = new ArrayList<String>();
		questList.add(questTag);

		List<String> openingList = new ArrayList<String>();
		openingList.add("Hey there, could you help me out?");
		List<String> middleList = new ArrayList<String>();
		middleList.add("Come back to me when you are done.");
		List<String> endingList = new ArrayList<String>();
		endingList.add("Awesome, thanks a bunch!");
		List<String> neutralList = new ArrayList<String>();
		neutralList.add("How ya doin'?");

		openingSentences.put(questList.get(0), new SentenceBatch(openingList));
		middleSentences.put(questList.get(0), new SentenceBatch(middleList));
		endingSentences.put(questList.get(0), new SentenceBatch(endingList));
		neutralSentences = new SentenceBatch(neutralList);
		this.questList = questList;

		//Set basic vars for every online player
		Player[] players = Utils.getOnlinePlayers();
		for(int i = 0; i < players.length; i++){
			EpicPlayer ep = EpicSystem.getEpicPlayer(players[i]);
			SetFirstInteraction(ep);
		}
		
		QuestEntityHandler.entityList.put(entity, this);
		QuestEntityHandler.newEntities.add(this);
	}
	
	public void NextInteraction(EpicPlayer epicPlayer){
		//Just in case somehow the players doesn't have a quest, assume the player didn't talk to the NPC yet
		if(!currentQuest.containsKey(epicPlayer) || 
				(currentQuest.containsKey(epicPlayer) && currentQuest.get(epicPlayer) == null)) currentQuest.put(epicPlayer, questList.get(0));
		
		//Check for quests the player can complete first
		List<EpicQuest> completableQuests = epicPlayer.getCompleteableQuest(this);
		if(!completableQuests.isEmpty()){
			for(EpicQuest quest : completableQuests){
				quest.completeQuest();
			}
			return;
		}
		
		String currentQuest = this.currentQuest.get(epicPlayer);
		QuestPhase currentPhase = questPhases.get(epicPlayer);
		SentenceBatch sentences = null;
		
		//epicPlayer.getPlayer().sendMessage("Player info. Quest: " + currentQuest + ", Phase: " + currentPhase.toString());
		boolean loop = true;
		while(loop){
		switch(currentPhase){
		case INTRO_TALK:
			if(!canGetQuest(epicPlayer)){
				sentences = neutralSentences;
				epicPlayer.getPlayer().sendMessage(formatMessage(sentences.Random(epicPlayer)));
				return;
			}
			
			sentences = openingSentences.get(currentQuest);
			epicPlayer.getPlayer().sendMessage(formatMessage(sentences.Next(epicPlayer)));
			if(sentences.IsLast(epicPlayer)){
				epicPlayer.addQuest(new EpicQuest(currentQuest));
				questPhases.put(epicPlayer, QuestPhase.BUSY);
			}
			loop = false;
			break;
		case BUSY:
			//Dropped quest?
			if(!epicPlayer.hasQuest(currentQuest)){
				questPhases.put(epicPlayer, QuestPhase.INTRO_TALK);
				currentPhase = QuestPhase.INTRO_TALK;
				continue;
			}
			
			//Not completed quest
			if(!epicPlayer.getQuestByTag(currentQuest).isCompleted()){
				sentences = middleSentences.get(currentQuest);
				epicPlayer.getPlayer().sendMessage(formatMessage(sentences.Random(epicPlayer)));
				loop = false;
				break;
			}
			
			//Completed quest
			questPhases.put(epicPlayer, QuestPhase.ENDING_TALK);
			currentPhase = QuestPhase.ENDING_TALK;		
			continue;
		case ENDING_TALK:
			sentences = endingSentences.get(currentQuest);
			epicPlayer.getPlayer().sendMessage(formatMessage(sentences.Next(epicPlayer)));
			if(sentences.IsLast(epicPlayer)){
				epicPlayer.completeQuest(epicPlayer.getQuestByTag(currentQuest));
				NextQuest(epicPlayer);
			}
			loop = false;
			break;
		default:
			loop = false;
			break;
		}
		}
	}
	
	private boolean canGetQuest(EpicPlayer ePlayer){
		return ePlayer.canGetQuest(this.currentQuest.get(ePlayer), false);
	}
	
	private void NextQuest(EpicPlayer epicPlayer){
		for(int i = 0; i < questList.size() - 1; i++){
			if(questList.get(i).equals(currentQuest.get(epicPlayer))){
				int quest = i;
				if(quest == questList.size()) quest = 0;
				else quest++;
				currentQuest.put(epicPlayer, questList.get(quest));
			}
		}
		questPhases.put(epicPlayer, QuestPhase.INTRO_TALK);
	}
	
	public void SetFirstInteraction(EpicPlayer epicPlayer){
		if(!currentQuest.containsKey(epicPlayer) || 
				(currentQuest.containsKey(epicPlayer) && currentQuest.get(epicPlayer) == null)) currentQuest.put(epicPlayer, questList.get(0));
		if(!questPhases.containsKey(epicPlayer)){
			QuestPhase phase = QuestPhase.INTRO_TALK;
			String questTag = this.questList.get(0);
			if(epicPlayer.hasQuest(questTag)){
				if(epicPlayer.getQuestByTag(questTag).isCompleted()){
					phase = QuestPhase.ENDING_TALK;
				}else{
					phase = QuestPhase.BUSY;
				}
			}
			questPhases.put(epicPlayer, phase);
		}
	}
	
	private String formatMessage(String message){
		return ""+ ChatColor.GRAY + ChatColor.ITALIC + QuestEntityHandler.getEntityName(entity) + ": " + ChatColor.WHITE + message;
	}
}
