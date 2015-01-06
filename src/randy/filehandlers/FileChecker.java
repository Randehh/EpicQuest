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
	static File questfile = new File("plugins" + File.separator + "EpicQuest" + File.separator + "quests.yml");
	static File blockfile = new File("plugins" + File.separator + "EpicQuest" + File.separator + "block.yml");
	static File signfile = new File("plugins" + File.separator + "EpicQuest" + File.separator + "signs.yml");
	static File entitiesfile = new File("plugins" + File.separator + "EpicQuest" + File.separator + "questentities.yml");
	
	
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
		if(!questfile.exists()){
			EpicMain.getInstance().saveResource("quests.yml", false);
		}
		if(!blockfile.exists()){
			blockfile.createNewFile();
			System.out.print("EpicQuest could not find the block file. Creating.");
		}
		if(!signfile.exists()){
			signfile.createNewFile();
			System.out.print("EpicQuest could not find the signs file. Creating.");
		}
		if(!entitiesfile.exists()){
			entitiesfile.createNewFile();
			System.out.print("EpicQuest could not find the quest entities file. Creating.");
		}
		return true;
	}
}
