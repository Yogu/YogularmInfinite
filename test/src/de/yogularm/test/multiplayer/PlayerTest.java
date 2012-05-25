package de.yogularm.test.multiplayer;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;

import java.util.Observer;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import de.yogularm.multiplayer.Match;
import de.yogularm.multiplayer.Player;

public class PlayerTest {
	@Test
	public void testConstructor() {
		Player player = new Player("thename");
		assertThat(player.getName(), equalTo("thename"));
	}
	
	@Test
	public void testEquality() {
		Player p1 = new Player("firstname");
		Player p2 = new Player("firstname");
		assertTrue(p1.equals(p2));
		assertFalse(p1.equals(new Player("secondname")));
		
		// equals(Player) should return false if Player is null
		p2 = null;
		assertFalse(p1.equals(p2));
	}
	
	@Test
	public void testEqualityCaseInsensitive() {
		assertTrue(new Player("abc").equals(new Player("ABC")));
	}
	
	@Test
	public void testEqualsObject() {
		Player p1 = new Player("firstname");
		Object p2 = new Player("firstname");
		Object p3 = new Player("secondname"); // does not equal p1
		assertTrue(p1.equals(p2));
		assertFalse(p1.equals(p3));
		assertFalse(p1.equals(new Object()));
		Object nullObj = null;
		assertFalse(p1.equals(nullObj));
	}
	
	@Test
	public void testIsValidName() {
		assertTrue(Player.isValidName("simplename"));
		assertTrue(Player.isValidName("abcABC123___--adf23"));
		
		assertFalse(Player.isValidName("containing spaces"));
		assertFalse(Player.isValidName("  leadingspaces"));
		assertFalse(Player.isValidName("abc~def"));
		assertFalse(Player.isValidName(""));
		assertFalse(Player.isValidName("    "));
	}
	
	@Test
	public void testJoinMatch() {
		Match match = mock(Match.class);
		Player player = new Player("theplayername");
		Observer observer = mock(Observer.class);
		player.addObserver(observer);
		
		player.joinMatch(match);
		
		assertThat(player.getCurrentMatch(), equalTo(match));
		verify(match).addPlayer(player);
		ArgumentCaptor<Player.ChangeEvent> captor = ArgumentCaptor.forClass(Player.ChangeEvent.class);
		verify(observer).update(eq(player), captor.capture());
		Player.ChangeEvent event = captor.getValue();
		assertThat(event.getPlayer(), equalTo(player));
		assertThat(event.getMatch(), equalTo(match));
		assertThat(event.getAction(), equalTo(Player.ChangeAction.JOINED_MATCH));
	}
	
	@Test
	public void testJoinMatchWhenAlreadyJoined() {
		Match match1 = mock(Match.class);
		Match match2 = mock(Match.class);
		Player player = new Player("theplayername");
		player.joinMatch(match1); // before addObserver()
		Observer observer = mock(Observer.class);
		player.addObserver(observer);
		
		try {
			player.joinMatch(match2);
			Assert.fail("Did not throw IllegalStateException");
		} catch (IllegalStateException e) {
			// expected
		}
		
		assertThat(player.getCurrentMatch(), equalTo(match1));
		verify(match1, never()).removePlayer(player);
		verify(match2, never()).addPlayer(player);
		verify(observer, never()).update(eq(player), any());
	}
	
	@Test
	public void testLeaveMatch() {
		Match match = mock(Match.class);
		Player player = new Player("theplayername");
		player.joinMatch(match);
		Observer observer = mock(Observer.class);
		player.addObserver(observer);
		
		player.leaveMatch();
		
		assertThat(player.getCurrentMatch(), is(nullValue()));
		verify(match).removePlayer(player);
		ArgumentCaptor<Player.ChangeEvent> captor = ArgumentCaptor.forClass(Player.ChangeEvent.class);
		verify(observer).update(eq(player), captor.capture());
		Player.ChangeEvent event = captor.getValue();
		assertThat(event.getPlayer(), equalTo(player));
		assertThat(event.getMatch(), equalTo(match));
		assertThat(event.getAction(), equalTo(Player.ChangeAction.LEFT_MATCH));
	}
	
	@Test
	public void testLeaveSpecificMatch() {
		Match match = mock(Match.class);
		Player player = new Player("theplayername");
		player.joinMatch(match);
		Observer observer = mock(Observer.class);
		player.addObserver(observer);
		
		player.leaveMatch(match);
		
		assertThat(player.getCurrentMatch(), is(nullValue()));
		verify(match).removePlayer(player);
		ArgumentCaptor<Player.ChangeEvent> captor = ArgumentCaptor.forClass(Player.ChangeEvent.class);
		verify(observer).update(eq(player), captor.capture());
		Player.ChangeEvent event = captor.getValue();
		assertThat(event.getPlayer(), equalTo(player));
		assertThat(event.getMatch(), equalTo(match));
		assertThat(event.getAction(), equalTo(Player.ChangeAction.LEFT_MATCH));
	}
	
	@Test
	public void testLeaveOtherMatch() {
		Match match = mock(Match.class);
		Match match2 = mock(Match.class);
		Player player = new Player("theplayername");
		player.joinMatch(match);
		Observer observer = mock(Observer.class);
		player.addObserver(observer);
		
		player.leaveMatch(match2);
		
		assertThat(player.getCurrentMatch(), is(match));
		verify(match, never()).removePlayer(player);
		verify(match2, never()).removePlayer(player);
		verify(observer, never()).update(eq(player), any());
	}
}
