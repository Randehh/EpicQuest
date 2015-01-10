package randy.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import randy.engine.EpicLeaderboard;
import randy.engine.EpicPlayer;
import randy.engine.EpicSystem;

public class CommandLeaderboard {
	public static void Execute(CommandSender sender, Command command, String commandName, String[] args){
		if(!(sender instanceof Player)) return;
		Player player = (Player)sender;
		EpicPlayer ePlayer = EpicSystem.getEpicPlayer(player);
		
		if(!CommandListener.hasPermission(ePlayer, "epicquest.user.leaderboard")) return;
		
		List<String> topScores = null;
		if(args.length == 2){
			if(args[1].equalsIgnoreCase("questcompleted")){
				topScores = EpicLeaderboard.getTopQuestsCompleted();
			}else if(args[1].equalsIgnoreCase("taskcompleted")){
				topScores = EpicLeaderboard.getTopTasksCompleted();
			}else if(args[1].equalsIgnoreCase("moneyearned")){
				topScores = EpicLeaderboard.getTopMoneyEarned();
			}else{
				player.sendMessage(ChatColor.RED + "/q leaderboard <questcompleted/taskcompleted/moneyearned>");
				return;
			}
		}else{
			player.sendMessage(ChatColor.RED + "/q leaderboard <questcompleted/taskcompleted/moneyearned>");
			return;
		}
		
		if(topScores.isEmpty()){
			player.sendMessage(ChatColor.RED + "There is no score in this section yet!");
			return;
		}
		
		StringBuilder resultString = new StringBuilder();
		resultString.append(ChatColor.GOLD);
		resultString.append("[======= ");
		resultString.append(ChatColor.WHITE);
		resultString.append("Leaderboards (Top ");
		resultString.append(topScores.size());
		resultString.append(") ");
		resultString.append(ChatColor.GOLD);
		resultString.append("=======]\n");
		for(int i = 0; i < topScores.size(); i++){
			StringBuilder scoreString = new StringBuilder();
			scoreString.append(ChatColor.GOLD);
			scoreString.append("  ");
			scoreString.append(i + 1);
			scoreString.append(")  ");
			scoreString.append(ChatColor.RED);
			scoreString.append(ChatColor.ITALIC);
			scoreString.append(topScores.get(i));
			scoreString.append("\n");
			resultString.append(scoreString.toString());
		}
		resultString.append(ChatColor.GOLD);
		resultString.append("[==================================]");
		player.sendMessage(resultString.toString());
	}
}
