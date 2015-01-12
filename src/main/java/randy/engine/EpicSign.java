package main.java.randy.engine;

import org.bukkit.Location;

public class EpicSign {

	String questTag;
	Location location;

	public EpicSign(String questTag, Location loc) {
		this.questTag = questTag;
		this.location = loc;
	}

	public String getQuest() {
		return questTag;
	}
	public Location getLocation() {
		return location;
	}

}
