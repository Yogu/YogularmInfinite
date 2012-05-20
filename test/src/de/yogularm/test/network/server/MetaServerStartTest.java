package de.yogularm.test.network.server;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;

import de.yogularm.multiplayer.MatchState;
import de.yogularm.multiplayer.Player;
import de.yogularm.network.CommunicationError;
import de.yogularm.network.NetworkCommand;

public class MetaServerStartTest extends MetaServerTest {
	@Test
	public void testPlayerMissing() throws IOException {
		sendCommand(NetworkCommand.START);
		c2s.out().close();
		handler.run();

		verifyResponse(CommunicationError.INVALID_STATE);
	}
	
	@Test
	public void testNotJoinedToMatch() throws IOException {
		clientContext.setPlayer(player);
		
		sendCommand(NetworkCommand.START);
		c2s.out().close();
		handler.run();

		verifyResponse(CommunicationError.INVALID_STATE);
	}

	@Test
	public void testNotOwnerOfMatch() throws IOException {
		clientContext.setPlayer(player);
		when(player.getCurrentMatch()).thenReturn(match);
		when(match.getOwner()).thenReturn(new Player("other"));

		sendCommand(NetworkCommand.START);
		c2s.out().close();
		handler.run();

		verifyResponse(CommunicationError.INVALID_STATE);
		verify(match, never()).start();
	}

	@Test
	public void testMatchClosed() throws IOException {
		clientContext.setPlayer(player);
		when(player.getCurrentMatch()).thenReturn(match);
		when(match.getOwner()).thenReturn(player);
		when(match.getState()).thenReturn(MatchState.RUNNING);
		when(match.isOpen()).thenReturn(false);

		sendCommand(NetworkCommand.START);
		c2s.out().close();
		handler.run();

		verifyResponse(CommunicationError.INVALID_STATE);
	}

	@Test
	public void testStartMatch() throws IOException {
		clientContext.setPlayer(player);
		when(player.getCurrentMatch()).thenReturn(match);
		when(match.getOwner()).thenReturn(player);
		when(match.getState()).thenReturn(MatchState.OPEN);
		when(match.isOpen()).thenReturn(true);

		sendCommand(NetworkCommand.START);
		c2s.out().close();
		handler.run();

		verifyResponseOK();
		verify(match).start();
	}
}
