package randy.tests;

import static org.junit.Assert.*;

import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class CommandTests {
	private static Player player;
	
	@Before
	public void setUp() throws Exception {
		player = Mockito.mock(Player.class);
	}

	@Test
	public void test() {
		assertEquals(player, player);
	}

}
