package de.yogularm.test.network.client;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;

import de.yogularm.network.NetworkGlobals;
import de.yogularm.network.client.DefaultClientHandlerFactory;
import de.yogularm.network.client.DefaultMetaHandler;
import de.yogularm.network.server.ServerContext;
import de.yogularm.network.server.StartHandler;
import de.yogularm.test.network.PipedConnector;
import de.yogularm.test.network.StreamPipe;
import de.yogularm.test.network.server.MockServerHandlerFactory;
import de.yogularm.test.network.server.MockServerManager;

public class DefaultClientHandlerFactoryTest {
	@Test
	public void testMetaHandler() throws IOException {
		PipedConnector connector = new PipedConnector(true);
		DefaultClientHandlerFactory factory = new DefaultClientHandlerFactory(connector);
		assertNotNull(factory.createMetaHandler());
		assertThat(connector.getOpenCount(), is(1));
		assertThat(connector.c2s.dataIn().available(), is(1));
		assertThat(connector.c2s.dataIn().readByte(), is(NetworkGlobals.STREAM_MODE_IDENTIFIER_ASCII));
	}

	@Test
	public void testPassiveHandler() throws IOException {
		PipedConnector connector = new PipedConnector(true);
		DefaultClientHandlerFactory factory = new DefaultClientHandlerFactory(connector);
		assertNotNull(factory.createPassiveHandler(""));
		assertThat(connector.getOpenCount(), is(1));
		assertThat(connector.c2s.dataIn().available(), is(1));
		assertThat(connector.c2s.dataIn().readByte(), is(NetworkGlobals.STREAM_MODE_IDENTIFIER_ASCII));
	}

	@Test
	public void testBinaryHandler() throws IOException {
		PipedConnector connector = new PipedConnector(true);
		DefaultClientHandlerFactory factory = new DefaultClientHandlerFactory(connector);
		assertNotNull(factory.createBinaryHandler(""));
		assertThat(connector.getOpenCount(), is(1));
		assertThat(connector.c2s.dataIn().available(), is(1));
		assertThat(connector.c2s.dataIn().readByte(), is(NetworkGlobals.STREAM_MODE_IDENTIFIER_BINARY));
	}
}
