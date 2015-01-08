package randy.epicquest;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class EpicParty {
	
	List<EpicPlayer> playerList = new ArrayList<EpicPlayer>();
	EpicPlayer partyLeader;
	
	public EpicParty(EpicPlayer player1, EpicPlayer player2){
		playerList.add(player1);
		playerList.add(player2);
		
		player1.setParty(this);
		player2.setParty(this);
		partyLeader = player1;
	}
	
	/*
	 * Party management
	 */
	
	public void addPlayer(EpicPlayer player){
		if(player.getParty() == null){
			player.setParty(this);
			playerList.add(player);
			sendMessage(null, ""+ChatColor.ITALIC + ChatColor.GREEN + player.getPlayer().getName() + " has joined the party!");
		}
	}
	
	public void removePlayer(EpicPlayer player, boolean kicked){
		if(playerList.contains(player)){
			
			if(kicked) player.getPlayer().sendMessage(ChatColor.RED + "You were kicked out of the party by " + partyLeader.getPlayer().getName() + ".");
			else sendMessage(null, ""+ChatColor.ITALIC + ChatColor.RED + player.getPlayer().getName() + " has left the party.");
			
			//Disband group if there's one left
			if(playerList.size() == 2){
				Disband();
			}else{
				playerList.remove(player);
				player.partyChat = false;
				player.setParty(null);
			
				if(partyLeader == player)
					partyLeader = playerList.get(0);
			}
		}
	}
	
	public EpicPlayer getLeader(){
		return partyLeader;
	}
	
	public void Disband(){
		for(int i = 0; i < playerList.size(); i++){
			EpicPlayer epicPlayer = playerList.get(i);
			epicPlayer.getPlayer().sendMessage(""+ChatColor.RED + ChatColor.ITALIC + "Your party is disbanded.");
			epicPlayer.partyChat = false;
			epicPlayer.setParty(null);
		}
		playerList.clear();
		partyLeader = null;
	}
	
	public List<EpicPlayer> getPartyMembers(){
		return playerList;
	}
	
	public void setPartyLeader(EpicPlayer newLeader){
		partyLeader = newLeader;
		sendMessage(null, ""+ChatColor.ITALIC + ChatColor.GREEN + newLeader.getPlayer().getName() + " is now party leader.");
	}
	
	public int getSize(){
		return playerList.size();
	}
	
	/*
	 * Chat functions
	 */
	
	public void sendPartyMembers(EpicPlayer originalPlayer){
		Player player = originalPlayer.getPlayer();
		player.sendMessage(ChatColor.ITALIC + partyLeader.getPlayer().getName() + ChatColor.RESET + ChatColor.GREEN + " (Leader)");
		
		for(int i = 0; i < playerList.size(); i++){
			
			EpicPlayer partyMember = playerList.get(i);
			if(partyMember != partyLeader){
				player.sendMessage(ChatColor.ITALIC + partyMember.getPlayer().getName());
			}
		}
	}
	
	public void sendMessage(EpicPlayer epicPlayer, String message){
		if(epicPlayer != null){
			for(int i = 0; i < playerList.size(); i++){
				String playerTag;
				if(epicPlayer == partyLeader) playerTag = "(LDR)";
				else playerTag = "(MBR)";
				playerList.get(i).getPlayer().sendMessage(ChatColor.ITALIC + "" + ChatColor.GREEN + playerTag + ChatColor.WHITE + epicPlayer.getPlayer().getName() + ": " + message);
			}
		}else{
			for(int i = 0; i < playerList.size(); i++){
				playerList.get(i).getPlayer().sendMessage(message);
			}
		}
	}
	
	/*
	 * Questing functions
	 */
	
	public List<EpicPlayer> getClosePlayers(EpicPlayer originalPlayer, float proximity){
		List<EpicPlayer> closePlayers = new ArrayList<EpicPlayer>();
		Location originalPlayerLocation = originalPlayer.getPlayer().getLocation();
		for(int i = 0; i < playerList.size(); i++){
			EpicPlayer player = playerList.get(i);
			if(player.getPlayer().getLocation().distance(originalPlayerLocation) < proximity){
				closePlayers.add(player);
			}
		}
		
		return closePlayers;
	}
}
