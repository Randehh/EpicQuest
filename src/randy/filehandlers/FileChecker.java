package randy.filehandlers;

import java.io.File;
import java.io.IOException;

public class FileChecker {
	
	/*
	 * Get all the files
	 */
	static File epicquestfolder = new File("plugins" + File.separator + "EpicQuest");
	static File playersfolder = new File("plugins" + File.separator + "EpicQuest" + File.separator + "Players");
	static File questfile = new File("plugins" + File.separator + "EpicQuest" + File.separator + "quests.yml");
	static File configfile = new File("plugins" + File.separator + "EpicQuest" + File.separator + "config.yml");
	static File blockfile = new File("plugins" + File.separator + "EpicQuest" + File.separator + "block.yml");
	static File signfile = new File("plugins" + File.separator + "EpicQuest" + File.separator + "signs.yml");
	
	
	/*
	 * Check files
	 */
	public static boolean checkFiles() throws IOException{
		if(!epicquestfolder.exists()){
			System.out.print("EpicQuest could not find the EpicQuest folder. Disabling plugin.");
			return false;
		}
		if(!playersfolder.exists()){
			playersfolder.mkdir();
		}
		if(!questfile.exists()){
			System.out.print("EpicQuest could not find the quest file. Disabling plugin.");
			return false;
		}
		if(!configfile.exists()){
			System.out.print("EpicQuest could not find the config file. Disabling plugin.");
			return false;
		}
		if(!blockfile.exists()){
			blockfile.createNewFile();
			System.out.print("EpicQuest could not find the block file. Creating.");
		}
		if(!signfile.exists()){
			signfile.createNewFile();
			System.out.print("EpicQuest could not find the signs file. Creating.");
		}
		return true;
	}
}
