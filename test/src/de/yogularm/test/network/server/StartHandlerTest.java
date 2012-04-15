package de.yogularm.test.network.server;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;

import org.junit.Test;

import de.yogularm.network.StreamModeIdentifiers;
import de.yogularm.network.server.ServerContext;
import de.yogularm.network.server.ServerHandler;
import de.yogularm.network.server.ServerHandlerFactory;
import de.yogularm.network.server.StartHandler;

public class StartHandlerTest {
	private boolean handlerCreated;
	private boolean handlerRunned;

	@Test
	public void testTextMode() throws IOException {
		final ServerContext data = new ServerContext(new MockServerManager());
		ServerHandlerFactory factory = new MockServerHandlerFactory() {
			public ServerHandler createMetaHandler(BufferedReader in, PrintWriter out,
					ServerContext serverData) {
				assertNotNull(in);
				assertNotNull(out);
				assertThat(serverData, is(data));
				handlerCreated = true;
				return new ServerHandler() {
					public void run() throws IOException {
						handlerRunned = true;
						return;
					}

					public void interrupt() {
					}
				};
			}
		};
		PipedInputStream in = new PipedInputStream();
		PipedOutputStream out = new PipedOutputStream(in);

		out.write(StreamModeIdentifiers.ASCII);

		handlerCreated = false;
		StartHandler handler = new StartHandler(in, out, data, factory);
		handler.run();
		assertTrue("Meta handler was not created", handlerCreated);
		assertTrue("Meta handler was not runned", handlerRunned);
	}

	@Test
	public void testBinaryMode() throws IOException {
		final ServerContext data = new ServerContext(new MockServerManager());
		ServerHandlerFactory factory = new MockServerHandlerFactory() {
			@Override
			public ServerHandler createBinaryStartHandler(DataInputStream in, DataOutputStream out,
					ServerContext serverData) {
				assertNotNull(in);
				assertNotNull(out);
				assertThat(serverData, is(data));
				handlerCreated = true;
				return new ServerHandler() {
					public void run() throws IOException {
						handlerRunned = true;
						return;
					}

					public void interrupt() {
					}
				};
			}
		};
		PipedInputStream in = new PipedInputStream();
		PipedOutputStream out = new PipedOutputStream(in);

		out.write(StreamModeIdentifiers.BINARY);

		handlerCreated = false;
		StartHandler handler = new StartHandler(in, out, data, factory);
		handler.run();
		assertTrue("Binary handler was not created", handlerCreated);
		assertTrue("Binary handler was not runned", handlerRunned);
	}
}
