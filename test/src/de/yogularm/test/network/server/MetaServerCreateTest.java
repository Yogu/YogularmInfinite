package de.yogularm.test.network.server;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;

import de.yogularm.multiplayer.Player;
import de.yogularm.network.CommunicationError;
import de.yogularm.network.NetworkCommand;

public class MetaServerCreateTest extends MetaServerTest {
	@Test
	public void testCreateMatchWithoutPlayerFails() throws IOException {
		sendCommand(NetworkCommand.CREATE);
		c2s.out().close();
		handler.run();

		verify(manager, never()).startNewMatch(any(Player.class), anyString());
		verifyResponse(CommunicationError.INVALID_STATE);
	}

	@Test
	public void testCreateMatchWithoutComment() throws IOException {
		clientContext.setPlayer(player);
		when(manager.startNewMatch(player, "")).thenReturn(match);

		sendCommand(NetworkCommand.CREATE);
		c2s.out().close();
		handler.run();

		verify(manager).startNewMatch(player, "");
		verifyResponseOK(MATCH_ID + "");
	}

	@Test
	public void testCreateMatchWithComment() throws IOException {
		clientContext.setPlayer(player);
		when(manager.startNewMatch(player, MATCH_COMMENT)).thenReturn(match);

		sendCommand(NetworkCommand.CREATE, MATCH_COMMENT);
		c2s.out().close();
		handler.run();

		verify(manager).startNewMatch(player, MATCH_COMMENT);
		verifyResponseOK(MATCH_ID + "");
	}
}
