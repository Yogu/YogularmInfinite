package de.yogularm.test.network.server;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.yogularm.network.BackgroundHandler;
import de.yogularm.network.CommunicationError;
import de.yogularm.network.NetworkCommand;

public class MetaServerPassiveTest extends MetaServerTest {
	private BackgroundHandler passiveHandler;
	
	@Before
	@Override
	public void setUp() throws IOException {
		super.setUp();

		passiveHandler = mock(BackgroundHandler.class);
		when(handlerFactory.createPassiveHandler(s2c.writer(), clientContext)).thenReturn(passiveHandler);
	}
	
	@Test
	public void testEnterPassiveMode() throws IOException {
		when(context.getClientContext(CLIENT_CONTEXT_KEY)).thenReturn(clientContext);
		
		sendCommand(NetworkCommand.PASSIVE, clientContext.getKey());
		handler.run();
		
		verifyResponseOK();
		verify(passiveHandler).run();
	}
	
	@Test
	public void testLeavePassiveMode() throws IOException {
		when(context.getClientContext(CLIENT_CONTEXT_KEY)).thenReturn(clientContext);
		
		sendCommand(NetworkCommand.PASSIVE, clientContext.getKey());
		handler.run();
		verifyResponseOK();
		
		verify(passiveHandler).interrupt();
		
		// the handler should now be stopped because the passive handler is finished immediately after run()
		// is called. Therefore, no more commands should be responded

		sendCommand(NetworkCommand.VERSION);
		assertThat(s2c.in().available(), equalTo(0));
	}
	
	@Test
	public void testEnterPassiveModeFails() throws IOException {
		when(context.getClientContext(WRONG_CLIENT_CONTEXT_KEY)).thenReturn(null);
		
		sendCommand(NetworkCommand.PASSIVE, WRONG_CLIENT_CONTEXT_KEY);
		c2s.out().close();
		handler.run();
		
		verifyResponse(CommunicationError.INVALID_SESSION_KEY);
		verify(passiveHandler, never()).run();
	}
	
	@Test
	public void testMetaContinuesAfterPassiveFails() throws IOException {
		when(context.getClientContext(WRONG_CLIENT_CONTEXT_KEY)).thenReturn(null);
		
		sendCommand(NetworkCommand.PASSIVE, WRONG_CLIENT_CONTEXT_KEY);
		sendCommand(NetworkCommand.VERSION);
		c2s.out().close();
		
		handler.run();
		
		verifyResponse(CommunicationError.INVALID_SESSION_KEY);
		verifyResponseOK();
	}
}
