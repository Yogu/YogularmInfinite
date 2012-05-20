package de.yogularm.test.network.server;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.eq;

import java.io.IOException;

import org.junit.Test;

import de.yogularm.network.CommunicationError;
import de.yogularm.network.NetworkCommand;

public class MetaServerSayTest extends MetaServerTest {
	@Test
	public void testPlayerMissing() throws IOException {
		sendCommand(NetworkCommand.SAY);
		c2s.out().close();
		handler.run();

		verifyResponse(CommunicationError.INVALID_STATE);
	}
	
	@Test
	public void testMessageMissing() throws IOException {
		clientContext.setPlayer(player);
		
		sendCommand(NetworkCommand.SAY, " ");
		c2s.out().close();
		handler.run();

		verifyResponseOK();
		verify(manager, never()).sendMessage(eq(player), anyString());
	}
	
	@Test
	public void testSay() throws IOException {
		clientContext.setPlayer(player);
		
		sendCommand(NetworkCommand.SAY, "the message ");
		c2s.out().close();
		handler.run();

		verifyResponseOK();
		verify(manager).sendMessage(player, "the message");
	}
}
