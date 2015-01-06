package randy.filehandlers;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import randy.epicquest.EpicSystem;

public class ConfigLoader {

	/*
	 * Get files
	 */
	static File configfile = new File("plugins" + File.separator + "EpicQuest" + File.separator + "config.yml");
	static FileConfiguration configuration = YamlConfiguration.loadConfiguration(configfile);
	
	/*
	 * Load config
	 */
	public static void loadConfig(){
		
		//Set quest limit
		if(!configuration.contains("Quest_Limit")){
			configuration.set("Quest_Limit", 10);
			EpicSystem.setQuestLimit(10);
		}else{
			EpicSystem.setQuestLimit(configuration.getInt("Quest_Limit"));
		}
		
		//Set time
		if(!configuration.contains("Time")){
			configuration.set("Time", 0);
			EpicSystem.setTime(0);
		}else{
			EpicSystem.setTime(configuration.getInt("Time"));
		}
		
		if(!configuration.contains("Daily_Limit")){
			configuration.set("Daily_Limit", -1);
			EpicSystem.setDailyLimit(-1);
		}else{
			EpicSystem.setDailyLimit(configuration.getInt("Daily_Limit"));
		}
		
		if(!configuration.contains("Max_Party_Size")){
			configuration.set("Max_Party_Size", 5);
			EpicSystem.setMaxPartySize(5);
		}else{
			EpicSystem.setMaxPartySize(configuration.getInt("Max_Party_Size"));
		}
		
		if(!configuration.contains("Use_Permissions")){
			configuration.set("Use_Permissions", true);
			EpicSystem.setUsePermissions(true);
		}else{
			EpicSystem.setUsePermissions(configuration.getBoolean("Use_Permissions"));
		}
		
		if(!configuration.contains("Use_Book")){
			configuration.set("Use_Book", true);
			EpicSystem.setUseBook(true);
		}else{
			EpicSystem.setUseBook(configuration.getBoolean("Use_Book"));
		}
		
		if(!configuration.contains("Use_Heroes")){
			configuration.set("Use_Heroes", true);
			EpicSystem.setUseHeroes(true);
		}else{
			EpicSystem.setUseHeroes(configuration.getBoolean("Use_Heroes"));
		}
		
		if(!configuration.contains("Use_Citizens")){
			configuration.set("Use_Citizens", true);
			EpicSystem.setUseCitizens(true);
		}else{
			EpicSystem.setUseCitizens(configuration.getBoolean("Use_Citizens"));
		}
		
		if(!configuration.contains("Use_Bar_API")){
			configuration.set("Use_Bar_API", false);
			EpicSystem.setUseBarAPI(false);
		}else{
			EpicSystem.setUseBarAPI(configuration.getBoolean("Use_Bar_API"));
		}
		
		try {
			configuration.save(configfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
