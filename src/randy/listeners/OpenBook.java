package randy.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import randy.epicquest.EpicPlayer;
import randy.epicquest.EpicQuest;
import randy.epicquest.EpicSystem;

public class OpenBook implements Listener{

	@EventHandler
	public void onEditBook(PlayerItemHeldEvent event){
		
		//Get player and the action
		Player player = event.getPlayer();
		String playername = player.getName();
		EpicPlayer epicPlayer = EpicSystem.getEpicPlayer(playername);	
		ItemStack inHand = player.getInventory().getItem(event.getNewSlot());

		if(inHand != null && inHand.getType() == Material.WRITTEN_BOOK){
			
			BookMeta book = (BookMeta)inHand.getItemMeta();
			if(book.getTitle().equalsIgnoreCase("Quest Book")){
				
				//Open zeh quest book!
				inHand.setItemMeta(SetQuestBookPages(epicPlayer, book));
			}
		}
	}

	
	private BookMeta SetQuestBookPages(EpicPlayer epicPlayer, BookMeta book){
		
		//Empty the old book
		book.setPages(new ArrayList<String>());
		
		book.addPage("\n\n\n\n       Quest Book"); //Center this line
		List<EpicQuest> questList = epicPlayer.getQuestList();
		
		/*
		 * QUEST INDEX
		 */
		String pageText = "";
		for(int questNo = 0; questNo < questList.size(); questNo++){
			
			EpicQuest quest = questList.get(questNo);
			String questName = quest.getQuestName();
			
			if(questName.length() >= 21){
				questName = quest.getQuestName().substring(0, 15) + "...";
			}
			
			pageText += ""+ (questNo + 3) + ": " + questName + "\n";
			
			if(questNo == 12 || questNo == (questList.size() - 1)){
				book.addPage(pageText);				
				pageText = "";
			}
		}
		
		/*
		 * SPECIFIC QUEST INFO
		 */
		for(int questNo = 0; questNo < questList.size(); questNo++){
			
			pageText = "";
			
			EpicQuest quest = questList.get(questNo);
			String questName = quest.getQuestName();
			
			//Title
			pageText += ChatColor.GOLD + questName + "\n\n" + ChatColor.BLACK;
			
			//Description
			String questStart = quest.getQuestStart();
			pageText += questStart + "\n\n";
			
			//Objectives
			String taskInfo = "";
			int maxTasks = quest.getTaskAmount();
			for(int task = 0; task < maxTasks; task++){
				taskInfo += quest.getPlayerTaskProgressText(task) + "\n";
			}
			pageText += taskInfo;
			
			book.addPage(pageText);
		}
		
		book.setAuthor("The Almighty One");
		
		return book;
	}
}
