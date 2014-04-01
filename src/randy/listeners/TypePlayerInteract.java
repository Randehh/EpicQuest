package randy.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import randy.epicquest.EpicPlayer;
import randy.epicquest.EpicQuest;
import randy.epicquest.EpicSign;
import randy.epicquest.EpicSystem;
import randy.epicquest.VillagerHandler;

public class TypePlayerInteract implements Listener{
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		
		//Get player and the action
		Player player = event.getPlayer();
		String playername = player.getName();
		EpicPlayer epicPlayer = EpicSystem.getEpicPlayer(playername);
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
			
			ItemStack inHand = player.getItemInHand();

			if(inHand.getTypeId() == 387){
				
				BookMeta book = (BookMeta)inHand.getItemMeta();
				if(book.getTitle().equalsIgnoreCase("Quest Book")){
					
					//Open zeh quest book!
					inHand.setItemMeta(SetQuestBookPages(epicPlayer, book));
				}
				
			}else{
				
				//Get the block and check if it's a sign
				List<EpicSign> signList = EpicSystem.getSignList();
				BlockState block = event.getClickedBlock().getState();
				for(int i = 0; i < signList.size(); i++){
					EpicSign sign = signList.get(i);
					Location signLoc = sign.getLocation();
					Location blockLoc = block.getLocation();
					blockLoc.setWorld(null);
					
					if(signLoc.equals(blockLoc)){
						if(sign.getQuest() == -1){
							if(epicPlayer.canGetQuest()){
								epicPlayer.addQuestRandom();
							}else{
								player.sendMessage(ChatColor.RED + "There are no more quests available.");
							}
						} else if(sign.getQuest() == -2){
							if(!epicPlayer.getCompleteableQuest().isEmpty()){
								epicPlayer.completeAllQuests();
							}else{
								player.sendMessage(ChatColor.RED + "There are no quests to turn in.");
							}
						} else {
							if(epicPlayer.getObtainableQuests().contains(sign.getQuest())){
								epicPlayer.addQuest(new EpicQuest(epicPlayer, sign.getQuest()));
							}else{
								player.sendMessage(ChatColor.RED + "You can't get that quest.");
							}
						}
					}
				}
				
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event){
		Entity clickedEntity = event.getRightClicked();
		if(clickedEntity instanceof Villager){
			Villager villager = (Villager)clickedEntity;
			if(VillagerHandler.villagerList.containsKey(villager)){
				
				EpicPlayer epicPlayer = EpicSystem.getEpicPlayer(event.getPlayer());
				List<Integer> questList = VillagerHandler.villagerList.get(villager);
				
				if(epicPlayer.hasQuest(questList.get(0))){
					EpicQuest quest = epicPlayer.getQuest(questList.get(0));
					if(quest.getPlayerQuestCompleted()){
						quest.completeQuest();
					}else{
						event.getPlayer().sendMessage(ChatColor.RED + villager.getCustomName() + ": " + ChatColor.ITALIC + VillagerHandler.GetRandomSentence(villager));
					}
				}else{
					epicPlayer.addQuest(new EpicQuest(epicPlayer, questList.get(0)));
				}
				event.setCancelled(true);
			}
		}
	}
	
	private BookMeta SetQuestBookPages(EpicPlayer epicPlayer, BookMeta book){
	
		List<String> emptyList = new ArrayList<String>();
		book.setPages(emptyList);
		
		epicPlayer.getPlayer().sendMessage("Setting questbook");
		//21 breed
		//13 hoog 
		
		book.addPage("\n\n\n\n       Quest Book"); //Center this line
		List<EpicQuest> questList = epicPlayer.getQuestList();
		
		/*
		 * QUEST INDEX
		 */
		String pageText = "";
		for(int questNo = 0; questNo < questList.size(); questNo++){
			
			System.out.print("Adding quest number " + questNo);
			
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
			
			//book.addPage(pageText);
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
