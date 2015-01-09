package randy.questtypes;

import java.util.ArrayList;
import java.util.List;

import randy.engine.EpicPlayer;

public class DamageTag {
	
	private List<EpicPlayer> playerList = new ArrayList<EpicPlayer>();
	
	public DamageTag(){}
	
	public void addPlayer(EpicPlayer player){
		if(playerList.contains(player)) return;
		playerList.add(player);
	}
	
	public List<EpicPlayer> getPlayerList(){
		return playerList;
	}
}
