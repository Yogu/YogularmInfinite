package de.yogularm.test.network.server;

import java.io.IOException;

import org.junit.Test;

import de.yogularm.network.NetworkCommand;

public class MetaServerVersionTest extends MetaServerTest {
	@Test
	public void testVersion() throws IOException {
		sendCommand(NetworkCommand.VERSION);
		c2s.out().close();
		handler.run();
		verifyResponseOK();
	}
}
