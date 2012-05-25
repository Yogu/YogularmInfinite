package de.yogularm.test.multiplayer;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Observer;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import de.yogularm.multiplayer.Match;
import de.yogularm.multiplayer.MatchState;
import de.yogularm.multiplayer.Player;

public class MatchTest {
	@Test
	public void testConstructor() {
		Player owner = mock(Player.class);
		Match match1 = new Match(owner);
		Match match2 = new Match(owner);
		
		assertThat(match1.getID(), is(match1.getID())); // should be persistent to one match
		assertThat(match1.getID(), is(not(match2.getID()))); // should be different between matches
		assertThat(match1.getOwner(), is(owner));
	}
	
	@Test
	public void testComment() {
		Match match = new Match(mock(Player.class));
		
		assertThat(match.getComment(), is(""));
		match.setComment("thecomment");
		assertThat(match.getComment(), is("thecomment"));
	}
	
	@Test
	public void testInitialState() {
		Match match = new Match(mock(Player.class));
		
		assertThat(match.getState(), is(MatchState.OPEN));
		assertTrue(match.isOpen());
		assertFalse(match.isOver());
		assertFalse(match.isCancelled());
		assertFalse(match.isPaused());
		assertFalse(match.isStarted());
	}
	
	@Test
	public void testStart() {
		Match match = new Match(mock(Player.class));
		Observer observer = mock(Observer.class);
		match.addObserver(observer);
		
		match.start();
		
		assertThat(match.getState(), is(MatchState.RUNNING));
		assertFalse(match.isOpen());
		assertFalse(match.isOver());
		assertFalse(match.isCancelled());
		assertFalse(match.isPaused());
		assertTrue(match.isStarted());
		
		ArgumentCaptor<Match.ChangeEvent> eventCaptor = ArgumentCaptor.forClass(Match.ChangeEvent.class);
		verify(observer).update(eq(match), eventCaptor.capture());
		Match.ChangeEvent event = eventCaptor.getValue();
		assertThat(event.getMatch(), is(match));
		assertThat(event.getAction(), is(Match.ChangeAction.STATE_CHANGED));
		assertThat(event.getOldState(), is(MatchState.OPEN));
		assertThat(event.getNewState(), is(MatchState.RUNNING));
	}
	
	@Test
	public void testStartRunningMatch() {
		Match match = new Match(mock(Player.class));
		match.start();
		
		match.start();
		
		assertThat(match.getState(), is(MatchState.RUNNING));
	}
	
	@Test
	public void testStartPausedMatch() {
		Match match = new Match(mock(Player.class));
		match.start();
		match.pause();
		
		match.start();
		
		assertThat(match.getState(), is(MatchState.PAUSED));
	}
	
	@Test
	public void testStartCancelledMatch() {
		Match match = new Match(mock(Player.class));
		match.start();
		match.cancel();
		
		try {
			match.start();
			fail("Expected IllegalStateException");
		} catch (IllegalStateException e) {
			// expected
		}
		
		assertThat(match.getState(), is(MatchState.CANCELLED));
	}
	
	@Test
	public void testPause() {
		Match match = new Match(mock(Player.class));
		match.start();
		Observer observer = mock(Observer.class);
		match.addObserver(observer);
		
		match.pause();
		
		assertThat(match.getState(), is(MatchState.PAUSED));
		assertFalse(match.isOpen());
		assertFalse(match.isOver());
		assertFalse(match.isCancelled());
		assertTrue(match.isPaused());
		assertTrue(match.isStarted());
		
		ArgumentCaptor<Match.ChangeEvent> eventCaptor = ArgumentCaptor.forClass(Match.ChangeEvent.class);
		verify(observer).update(eq(match), eventCaptor.capture());
		Match.ChangeEvent event = eventCaptor.getValue();
		assertThat(event.getMatch(), is(match));
		assertThat(event.getAction(), is(Match.ChangeAction.STATE_CHANGED));
		assertThat(event.getOldState(), is(MatchState.RUNNING));
		assertThat(event.getNewState(), is(MatchState.PAUSED));
	}
	
	@Test
	public void testPauseOpenMatch() {
		Match match = new Match(mock(Player.class));
		
		try {
			match.pause();
			fail("Expected IllegalStateException");
		} catch (IllegalStateException e) {
			// expected
		}
		
		assertThat(match.getState(), is(MatchState.OPEN));
	}
	
	@Test
	public void testPausePausedMatch() {
		Match match = new Match(mock(Player.class));
		match.start();
		match.pause();
		
		assertThat(match.getState(), is(MatchState.PAUSED));
	}
	
	@Test
	public void testPauseCancelledMatch() {
		Match match = new Match(mock(Player.class));
		match.start();
		match.cancel();
		
		try {
			match.pause();
			fail("Expected IllegalStateException");
		} catch (IllegalStateException e) {
			// expected
		}
		
		assertThat(match.getState(), is(MatchState.CANCELLED));
	}
	
	@Test
	public void testResume() {
		Match match = new Match(mock(Player.class));
		match.start();
		match.pause();
		Observer observer = mock(Observer.class);
		match.addObserver(observer);
		
		match.resume();
		
		assertThat(match.getState(), is(MatchState.RUNNING));
		assertFalse(match.isOpen());
		assertFalse(match.isOver());
		assertFalse(match.isCancelled());
		assertFalse(match.isPaused());
		assertTrue(match.isStarted());
		
		ArgumentCaptor<Match.ChangeEvent> eventCaptor = ArgumentCaptor.forClass(Match.ChangeEvent.class);
		verify(observer).update(eq(match), eventCaptor.capture());
		Match.ChangeEvent event = eventCaptor.getValue();
		assertThat(event.getMatch(), is(match));
		assertThat(event.getAction(), is(Match.ChangeAction.STATE_CHANGED));
		assertThat(event.getOldState(), is(MatchState.PAUSED));
		assertThat(event.getNewState(), is(MatchState.RUNNING));
	}
	
	@Test
	public void testResumeOpenMatch() {
		Match match = new Match(mock(Player.class));
		
		try {
			match.resume();
			fail("Expected IllegalStateException");
		} catch (IllegalStateException e) {
			// expected
		}
		
		assertThat(match.getState(), is(MatchState.OPEN));
	}
	
	@Test
	public void testResumeRunningMatch() {
		Match match = new Match(mock(Player.class));
		match.start();
		
		match.resume();
		
		assertThat(match.getState(), is(MatchState.RUNNING));
	}
	
	@Test
	public void testResumeCancelledMatch() {
		Match match = new Match(mock(Player.class));
		match.start();
		match.cancel();
		
		try {
			match.resume();
			fail("Expected IllegalStateException");
		} catch (IllegalStateException e) {
			// expected
		}
		
		assertThat(match.getState(), is(MatchState.CANCELLED));
	}
	
	@Test
	public void testCancel() {
		Match match = new Match(mock(Player.class));
		match.start();
		Observer observer = mock(Observer.class);
		match.addObserver(observer);
		
		match.cancel();
		
		assertThat(match.getState(), is(MatchState.CANCELLED));
		assertFalse(match.isOpen());
		assertTrue(match.isOver());
		assertTrue(match.isCancelled());
		assertFalse(match.isPaused());
		assertFalse(match.isStarted());
		
		ArgumentCaptor<Match.ChangeEvent> eventCaptor = ArgumentCaptor.forClass(Match.ChangeEvent.class);
		verify(observer).update(eq(match), eventCaptor.capture());
		Match.ChangeEvent event = eventCaptor.getValue();
		assertThat(event.getMatch(), is(match));
		assertThat(event.getAction(), is(Match.ChangeAction.STATE_CHANGED));
		assertThat(event.getOldState(), is(MatchState.RUNNING));
		assertThat(event.getNewState(), is(MatchState.CANCELLED));
	}
	
	@Test
	public void testCancelCancelledMatch() {
		Match match = new Match(mock(Player.class));
		match.start();
		match.cancel();

		match.cancel();
		
		assertThat(match.getState(), is(MatchState.CANCELLED));
	}
	
	@Test
	public void testAddsOwner() {
		Player owner = mock(Player.class);
		Match match = new Match(owner);
		
		assertThat(match.getPlayers(), Matchers.contains(owner));
		verify(owner).joinMatch(match);
	}
	
	@Test(expected=IllegalStateException.class)
	public void testOwnerIsAlreadyJoined() {
		Player owner = mock(Player.class);
		Mockito.doThrow(new IllegalStateException()).when(owner).joinMatch(any(Match.class));
		
		// IllegalStateException should not be caught by this constructor
		new Match(owner);
	}
	
	@Test
	public void testAddPlayer() {
		Match match = new Match(mock(Player.class));
		Player newPlayer = mock(Player.class);
		Observer observer = mock(Observer.class);
		match.addObserver(observer);
		
		match.addPlayer(newPlayer);
		
		assertThat(match.getPlayers(), hasSize(2));
		verify(newPlayer).joinMatch(match);
		ArgumentCaptor<Match.ChangeEvent> eventCaptor = ArgumentCaptor.forClass(Match.ChangeEvent.class);
		verify(observer).update(eq(match), eventCaptor.capture());
		Match.ChangeEvent event = eventCaptor.getValue();
		assertThat(event.getMatch(), is(match));
		assertThat(event.getPlayer(), is(newPlayer));
		assertThat(event.getAction(), is(Match.ChangeAction.PLAYER_JOINED));
	}
	
	@Test
	public void testRemovePlayer() {
		Match match = new Match(mock(Player.class));
		Player player = mock(Player.class);
		match.addPlayer(player);
		Observer observer = mock(Observer.class);
		match.addObserver(observer);
		
		match.removePlayer(player);
		
		assertThat(match.getPlayers(), hasSize(1));
		verify(player).leaveMatch(match);
		ArgumentCaptor<Match.ChangeEvent> eventCaptor = ArgumentCaptor.forClass(Match.ChangeEvent.class);
		verify(observer).update(eq(match), eventCaptor.capture());
		Match.ChangeEvent event = eventCaptor.getValue();
		assertThat(event.getMatch(), is(match));
		assertThat(event.getPlayer(), is(player));
		assertThat(event.getAction(), is(Match.ChangeAction.PLAYER_LEFT));
	}
}
