package randy.filehandlers;

import java.io.File;
import java.io.IOException;

import randy.epicquest.EpicMain;

public class FileChecker {
	
	/*
	 * Get all the files
	 */
	static File epicquestfolder = new File("plugins" + File.separator + "EpicQuest");
	static File playersfolder = new File("plugins" + File.separator + "EpicQuest" + File.separator + "Players");
	static File entitiesfolder = new File("plugins" + File.separator + "EpicQuest" + File.separator + "QuestEntities");
	static File questsfolder = new File("plugins" + File.separator + "EpicQuest" + File.separator + "Quests");
	static File questfile = new File("plugins" + File.separator + "EpicQuest" + File.separator + "quests.yml");
	static File blockfile = new File("plugins" + File.separator + "EpicQuest" + File.separator + "block.yml");
	static File signfile = new File("plugins" + File.separator + "EpicQuest" + File.separator + "signs.yml");
	static File announcerfile = new File("plugins" + File.separator + "EpicQuest" + File.separator + "announcer.yml");
	static File leaderboardfile = new File("plugins" + File.separator + "EpicQuest" + File.separator + "leaderboard.yml");
	
	/*
	 * Check files
	 */
	public static boolean checkFiles() throws IOException{
		if(!epicquestfolder.exists()){
			System.out.print("EpicQuest could not find the EpicQuest folder. Creating.");
			epicquestfolder.mkdir();
		}
		if(!playersfolder.exists()){
			playersfolder.mkdir();
		}
		if(!entitiesfolder.exists()){
			entitiesfolder.mkdir();
		}
		if(!questsfolder.exists()){
			questsfolder.mkdir();
			EpicMain.getInstance().saveResource("Quests" + File.separator + "DefaultArea0_0.yml", false);
			EpicMain.getInstance().saveResource("Quests" + File.separator + "DefaultArea0_1.yml", false);
			EpicMain.getInstance().saveResource("Quests" + File.separator + "DefaultArea0_2.yml", false);
			EpicMain.getInstance().saveResource("Quests" + File.separator + "DefaultArea0_3.yml", false);
			EpicMain.getInstance().saveResource("Quests" + File.separator + "DefaultArea0_4.yml", false);
			EpicMain.getInstance().saveResource("Quests" + File.separator + "DefaultArea1_0.yml", false);
			EpicMain.getInstance().saveResource("Quests" + File.separator + "DefaultArea1_1.yml", false);
			EpicMain.getInstance().saveResource("Quests" + File.separator + "DefaultArea1_2.yml", false);
		}
		if(!questfile.exists()){
			EpicMain.getInstance().saveResource("quests.yml", false);
		}
		if(!announcerfile.exists()){
			EpicMain.getInstance().saveResource("announcer.yml", false);
		}
		if(!blockfile.exists()){
			blockfile.createNewFile();
			System.out.print("EpicQuest could not find the block file. Creating.");
		}
		if(!signfile.exists()){
			signfile.createNewFile();
			System.out.print("EpicQuest could not find the signs file. Creating.");
		}
		if(!leaderboardfile.exists()){
			leaderboardfile.createNewFile();
			System.out.print("EpicQuest could not find the leaderboard file. Creating.");
		}
		return true;
	}
}
