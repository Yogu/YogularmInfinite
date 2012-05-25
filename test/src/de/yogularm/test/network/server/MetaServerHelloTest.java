package de.yogularm.test.network.server;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;

import de.yogularm.multiplayer.Player;
import de.yogularm.network.CommunicationError;
import de.yogularm.network.NetworkCommand;

public class MetaServerHelloTest extends MetaServerTest {	
	@Test
	public void testRegisterInvalidName() throws IOException {
		sendCommand(NetworkCommand.HELLO, INVALID_PLAYER_NAME);
		c2s.out().close();
		handler.run();
		
		verify(manager, never()).registerPlayer(anyString());
		verifyResponse(CommunicationError.ILLEGAL_ARGUMENT);
	}

	@Test
	public void testHelloWithoutName() throws IOException {
		sendCommand(NetworkCommand.HELLO);
		c2s.out().close();
		handler.run();
		
		verify(manager, never()).registerPlayer(anyString());
		verifyResponse(CommunicationError.ILLEGAL_ARGUMENT);
	}
	
	@Test
	public void testRegisterAvailableName() throws IOException {
		when(manager.registerPlayer(PLAYER_NAME)).thenReturn(new Player(PLAYER_NAME));

		sendCommand(NetworkCommand.HELLO, PLAYER_NAME);
		c2s.out().close();
		handler.run();
		
		verify(manager).registerPlayer(PLAYER_NAME);
		verify(context).createClientContext();
		verifyResponseOK(clientContext.getKey());
	}
	
	@Test
	public void testRegisterUnavailableName() throws IOException {
		when(manager.registerPlayer(PLAYER_NAME)).thenReturn(null);

		sendCommand(NetworkCommand.HELLO, PLAYER_NAME);
		c2s.out().close();
		handler.run();
		
		verify(manager).registerPlayer(PLAYER_NAME);
		verifyResponse(CommunicationError.NAME_NOT_AVAILABLE);
	}
}
