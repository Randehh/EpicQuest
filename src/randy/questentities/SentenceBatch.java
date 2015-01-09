package randy.questentities;

import java.util.HashMap;
import java.util.List;

import randy.engine.EpicPlayer;
import randy.engine.EpicSystem;

public class SentenceBatch {
	
	private HashMap<EpicPlayer, Integer> sentenceTracker = new HashMap<EpicPlayer, Integer>();
	public List<String> sentences;
	
	public SentenceBatch(List<String> sentences){
		this.sentences = sentences;
	}
	
	//This method loops through the sentences
	public String Next(EpicPlayer p){
		CheckPlayer(p);
		
		int currentSentence = sentenceTracker.get(p);
		String nextSentence = sentences.get(currentSentence);
		
		if(currentSentence + 1 == sentences.size()) currentSentence = 0;
		else currentSentence++;
		sentenceTracker.put(p, currentSentence);
		
		return nextSentence;
	}
	
	//Get a random message
	public String Random(EpicPlayer p){
		return sentences.get(EpicSystem.random.nextInt(sentences.size()));
	}
	
	public boolean IsLast(EpicPlayer p){
		CheckPlayer(p);
		if(sentenceTracker.get(p) + 1 == sentences.size()) return true;
		return false;
	}
	
	private void CheckPlayer(EpicPlayer p){
		if(!sentenceTracker.containsKey(p)) sentenceTracker.put(p, 0);
	}
	
	public List<String> getSentences(){
		return sentences;
	}
}
