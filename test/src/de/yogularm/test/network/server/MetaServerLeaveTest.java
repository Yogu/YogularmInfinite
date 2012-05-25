package de.yogularm.test.network.server;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;

import de.yogularm.network.CommunicationError;
import de.yogularm.network.NetworkCommand;

public class MetaServerLeaveTest extends MetaServerTest {
	@Test
	public void testPlayerMissing() throws IOException {
		sendCommand(NetworkCommand.LEAVE, MATCH_ID + "");
		c2s.out().close();
		handler.run();

		verifyResponse(CommunicationError.INVALID_STATE);
	}
	
	@Test
	public void testNotJoinedToMatch() throws IOException {
		when(clientContext.getPlayer()).thenReturn(player);
		
		sendCommand(NetworkCommand.LEAVE);
		c2s.out().close();
		handler.run();

		verifyResponse(CommunicationError.INVALID_STATE);
	}

	@Test
	public void testLeaveMatch() throws IOException {
		when(clientContext.getPlayer()).thenReturn(player);
		when(player.getCurrentMatch()).thenReturn(match);

		sendCommand(NetworkCommand.LEAVE);
		c2s.out().close();
		handler.run();

		verifyResponseOK();
		verify(player).leaveMatch();
	}
	
	/**
	 * Tests whether ERR INVALID_STATE is returned when IllegalStateException occurs
	 * 
	 * Although the player's state is checked before, it may change between the check and leaveMatch() call.
	 */
	@Test
	public void testJoinFails() throws IOException {
		when(clientContext.getPlayer()).thenReturn(player);
		when(player.getCurrentMatch()).thenReturn(match);
		doThrow(new IllegalStateException()).when(player).leaveMatch();

		sendCommand(NetworkCommand.LEAVE);
		c2s.out().close();
		handler.run();
		
		verifyResponse(CommunicationError.INVALID_STATE);
		verify(player).leaveMatch();
	}
}
