package randy.epicquest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Villager;

public class EpicVillager {
	
	Villager villager;
	public HashMap<Integer, List<String>> openingSentences = new HashMap<Integer, List<String>>();
	public HashMap<Integer, List<String>> middleSentences = new HashMap<Integer, List<String>>();
	public HashMap<Integer, List<String>> endingSentences = new HashMap<Integer, List<String>>();
	public List<Integer> questList = new ArrayList<Integer>();
	
	public HashMap<EpicPlayer, Integer> currentQuest = new HashMap<EpicPlayer, Integer>();
	public HashMap<EpicPlayer, Integer> currentSentence = new HashMap<EpicPlayer, Integer>();
	public Location originalLocation;
	
	public HashMap<EpicPlayer, Boolean> startedQuest = new HashMap<EpicPlayer, Boolean>();
	
	public EpicVillager(Villager villager){
		this.villager = villager;
	}

}
