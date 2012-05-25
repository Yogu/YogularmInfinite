package de.yogularm.test.network.server;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

import java.io.IOException;

import org.junit.Test;

import de.yogularm.multiplayer.MatchState;
import de.yogularm.network.CommunicationError;
import de.yogularm.network.NetworkCommand;

public class MetaServerJoinTest extends MetaServerTest {
	@Test
	public void testPlayerMissing() throws IOException {
		sendCommand(NetworkCommand.JOIN, MATCH_ID + "");
		c2s.out().close();
		handler.run();

		verifyResponse(CommunicationError.INVALID_STATE);
	}

	@Test
	public void testOpenMatch() throws IOException {
		when(clientContext.getPlayer()).thenReturn(player);
		when(manager.getMatchByID(MATCH_ID)).thenReturn(match);
		when(match.isOpen()).thenReturn(true);
		when(match.getState()).thenReturn(MatchState.OPEN);

		sendCommand(NetworkCommand.JOIN, MATCH_ID + "");
		c2s.out().close();
		handler.run();

		verifyResponseOK();
		verify(player).joinMatch(match);
	}

	@Test
	public void testClosedMatch() throws IOException {
		when(clientContext.getPlayer()).thenReturn(player);
		when(manager.getMatchByID(MATCH_ID)).thenReturn(match);
		when(match.isOpen()).thenReturn(false);
		when(match.getState()).thenReturn(MatchState.RUNNING);

		sendCommand(NetworkCommand.JOIN, MATCH_ID + "");
		c2s.out().close();
		handler.run();

		verifyResponse(CommunicationError.MATCH_NOT_OPEN);
		verify(player, never()).joinMatch(match);
	}

	@Test
	public void testMatchNotFound() throws IOException {
		when(clientContext.getPlayer()).thenReturn(player);
		when(manager.getMatchByID(MATCH_ID)).thenReturn(null);

		sendCommand(NetworkCommand.JOIN, MATCH_ID + "");
		c2s.out().close();
		handler.run();

		verifyResponse(CommunicationError.MATCH_NOT_FOUND);
	}

	@Test
	public void testWithoutMatchID() throws IOException {
		when(clientContext.getPlayer()).thenReturn(player);

		sendCommand(NetworkCommand.JOIN);
		c2s.out().close();
		handler.run();

		verifyResponse(CommunicationError.ILLEGAL_ARGUMENT);
	}

	@Test
	public void testInvalidMatchID() throws IOException {
		when(clientContext.getPlayer()).thenReturn(player);

		sendCommand(NetworkCommand.JOIN, INVALID_MATCH_ID);
		c2s.out().close();
		handler.run();

		verifyResponse(CommunicationError.ILLEGAL_ARGUMENT);
	}
	
	/**
	 * Tests whether ERR INVALID_STATE is returned when IllegalStateException occurs
	 * 
	 * Although the player's state is checked before, it may change between the check and joinMatch() call.
	 */
	@Test
	public void testJoinFails() throws IOException {
		when(clientContext.getPlayer()).thenReturn(player);
		doThrow(new IllegalStateException()).when(player).joinMatch(match);
		when(manager.getMatchByID(MATCH_ID)).thenReturn(match);
		when(match.isOpen()).thenReturn(true);
		when(match.getState()).thenReturn(MatchState.OPEN);

		sendCommand(NetworkCommand.JOIN, MATCH_ID + "");
		c2s.out().close();
		handler.run();
		
		verifyResponse(CommunicationError.INVALID_STATE);
		verify(player).joinMatch(match);
	}
}
