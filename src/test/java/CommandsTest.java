/*package test.java;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import randy.commands.CommandListener;
import randy.engine.EpicPlayer;
import randy.engine.EpicSystem;

@RunWith(MockitoJUnitRunner.class)
public class CommandsTest {
	Player player;
	EpicPlayer epicPlayer;
	CommandListener listener;
	
	@Before
	public void setUp() throws Exception {
		player = Mockito.mock(Player.class);
		listener = new CommandListener();
		epicPlayer = new EpicPlayer(player.getUniqueId());
	}

	@Test
	public void testSlashQ() {		
		listener.onCommand(player, null, "q", new String[0]);
		
		Mockito.verify(player).sendMessage(ChatColor.GOLD + "[-------Welcome to EpicQuest!-------]");
		Mockito.verify(player).sendMessage(ChatColor.GREEN + "EpicQuest is developed by Randy Schouten (Impossible24) with some additional code by bigbeno37");
		Mockito.verify(player).sendMessage(ChatColor.GREEN + "This version is currently " + ChatColor.YELLOW + String.valueOf(EpicSystem.getVersion()));
		Mockito.verify(player).sendMessage("");
		Mockito.verify(player).sendMessage(ChatColor.GREEN + "For help with how to use EpicQuest, use " + ChatColor.WHITE + "'/q help'");
	}

}
*/