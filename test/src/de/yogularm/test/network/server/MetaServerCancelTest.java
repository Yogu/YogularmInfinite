package de.yogularm.test.network.server;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;

import de.yogularm.multiplayer.MatchState;
import de.yogularm.multiplayer.Player;
import de.yogularm.network.CommunicationError;
import de.yogularm.network.NetworkCommand;

public class MetaServerCancelTest extends MetaServerTest {
	@Test
	public void testPlayerMissing() throws IOException {
		sendCommand(NetworkCommand.CANCEL);
		c2s.out().close();
		handler.run();

		verifyResponse(CommunicationError.INVALID_STATE);
	}
	
	@Test
	public void testNotJoinedToMatch() throws IOException {
		when(clientContext.getPlayer()).thenReturn(player);
		
		sendCommand(NetworkCommand.CANCEL);
		c2s.out().close();
		handler.run();

		verifyResponse(CommunicationError.INVALID_STATE);
	}

	@Test
	public void testNotOwnerOfMatch() throws IOException {
		when(clientContext.getPlayer()).thenReturn(player);
		when(player.getCurrentMatch()).thenReturn(match);
		when(match.getOwner()).thenReturn(new Player("other"));

		sendCommand(NetworkCommand.CANCEL);
		c2s.out().close();
		handler.run();

		verifyResponse(CommunicationError.INVALID_STATE);
		verify(match, never()).start();
	}

	@Test
	public void testMatchIsFinished() throws IOException {
		when(clientContext.getPlayer()).thenReturn(player);
		when(player.getCurrentMatch()).thenReturn(match);
		when(match.getOwner()).thenReturn(player);
		when(match.getState()).thenReturn(MatchState.FINISHED);
		when(match.isOpen()).thenReturn(true);
		when(match.isOver()).thenReturn(true);

		sendCommand(NetworkCommand.CANCEL);
		c2s.out().close();
		handler.run();

		verifyResponse(CommunicationError.INVALID_STATE);
	}

	@Test
	public void testCancelMatch() throws IOException {
		when(clientContext.getPlayer()).thenReturn(player);
		when(player.getCurrentMatch()).thenReturn(match);
		when(match.getOwner()).thenReturn(player);
		when(match.getState()).thenReturn(MatchState.OPEN);
		when(match.isOpen()).thenReturn(true);
		when(match.isOver()).thenReturn(false);

		sendCommand(NetworkCommand.CANCEL);
		c2s.out().close();
		handler.run();

		verifyResponseOK();
		verify(match).cancel();
	}
	
	/**
	 * Tests whether ERR INVALID_STATE is returned when IllegalStateException occurs
	 * 
	 * Although the state is checked before, it may change between the check and cancel() call.
	 */
	@Test
	public void testStartFails() throws IOException {
		when(clientContext.getPlayer()).thenReturn(player);
		when(player.getCurrentMatch()).thenReturn(match);
		when(match.getOwner()).thenReturn(player);
		when(match.getState()).thenReturn(MatchState.OPEN);
		when(match.isOpen()).thenReturn(true);
		when(match.isOver()).thenReturn(false);
		doThrow(new IllegalStateException()).when(match).cancel();

		sendCommand(NetworkCommand.CANCEL);
		c2s.out().close();
		handler.run();
		
		verifyResponse(CommunicationError.INVALID_STATE);
		verify(match).cancel();
	}
}
