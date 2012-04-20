package de.yogularm.test.network.server;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.junit.Test;

import de.yogularm.network.BackgroundHandler;
import de.yogularm.network.server.GameServer;
import de.yogularm.network.server.ServerContext;
import de.yogularm.network.server.ServerHandlerFactory;

public class GameServerTest {
	private static final int PORT = 62702;
	private static final int NETWORK_TIMEOUT = 1000;

	@Test
	public void testConstructor() {
		new GameServer(new MockServerHandlerFactory(), new MockServerManager());
	}

	private boolean handlerCreated;
	private boolean runCalled;
	private boolean interruptCalled;

	/**
	 * Tests whether the server creates start handlers for incoming sockets and
	 * calls the run() method on them
	 * 
	 * @throws IOException
	 */
	@Test
	public void testAccept() throws IOException {
		handlerCreated = false;
		ServerHandlerFactory factory = new MockServerHandlerFactory() {
			public BackgroundHandler createStartHandler(InputStream in, OutputStream out,
					ServerContext serverData) {
				assertNotNull(in);
				assertNotNull(out);
				assertNotNull(serverData);
				handlerCreated = true;
				return new BackgroundHandler() {
					public void run() throws IOException {
						runCalled = true;
					}

					public void interrupt() {
					}
				};
			}
		};
		GameServer server = new GameServer(factory, new MockServerManager());
		server.open(PORT);
		try {
			Socket clientSocket = new Socket(InetAddress.getLoopbackAddress(), PORT);
			try {
				long time = System.currentTimeMillis();
				while (!handlerCreated || !runCalled)
					if (System.currentTimeMillis() > time + NETWORK_TIMEOUT) {
						if (!handlerCreated)
							fail("StartHandler was not created");
						else
							fail("handler.run() was not called");
					}
			} finally {
				clientSocket.close();
			}
		} finally {
			server.close();
		}
	}

	/**
	 * Tests whether the server calls interrupt() on handlers when the server is
	 * closed
	 */
	@Test
	public void testInterrupt() throws IOException {
		runCalled = false;
		interruptCalled = false;
		ServerHandlerFactory factory = new MockServerHandlerFactory() {
			public BackgroundHandler createStartHandler(InputStream in, OutputStream out,
					ServerContext serverData) {
				return new BackgroundHandler() {
					public void run() throws IOException {
						runCalled = true;
						while (!interruptCalled)
							;
					}

					public void interrupt() {
						interruptCalled = true;
					}
				};
			}
		};
		GameServer server = new GameServer(factory, new MockServerManager());
		server.open(PORT);
		try {
			Socket clientSocket = new Socket(InetAddress.getLoopbackAddress(), PORT);
			try {
				long time = System.currentTimeMillis();
				while (!runCalled)
					if (System.currentTimeMillis() > time + NETWORK_TIMEOUT)
						fail("StartHandler was not created or handler.run() was not called");

				server.close();

				time = System.currentTimeMillis();
				while (!interruptCalled)
					if (System.currentTimeMillis() > time + NETWORK_TIMEOUT)
						fail("handler.interrupt() was not called");
			} finally {
				clientSocket.close();
			}
		} finally {
			server.close();
		}
	}

	/**
	 * Tests whether the server closes sockets whose run() method has finished
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testServerClosesSocket() throws IOException, InterruptedException {
		runCalled = false;
		interruptCalled = false;
		ServerHandlerFactory factory = new MockServerHandlerFactory() {
			public BackgroundHandler createStartHandler(InputStream in, OutputStream out,
					ServerContext serverData) {
				return new BackgroundHandler() {
					public void run() throws IOException {
						runCalled = true;
					}

					public void interrupt() {
						interruptCalled = true;
					}
				};
			}
		};
		GameServer server = new GameServer(factory, new MockServerManager());
		server.open(PORT);
		try {
			Socket clientSocket = new Socket(InetAddress.getLoopbackAddress(), PORT);
			try {
				long time = System.currentTimeMillis();
				while (!runCalled)
					if (System.currentTimeMillis() > time + NETWORK_TIMEOUT)
						fail("StartHandler was not created or handler.run() was not called");

				try {
					time = System.currentTimeMillis();
					while (System.currentTimeMillis() < time + NETWORK_TIMEOUT)
						clientSocket.getOutputStream().write(0);
					fail("Server did not close the socket");
				} catch (IOException e) {
					// write failed because of closed connection
					assertFalse(clientSocket.isClosed());
				}
			} finally {
				clientSocket.close();
			}
		} finally {
			server.close();
		}
	}

	/**
	 * Tests whether connections made after calling close() are refused
	 * 
	 * @throws IOException
	 */
	@Test
	public void testConnectAfterClose() throws IOException {
		GameServer server = new GameServer(new MockServerHandlerFactory(), new MockServerManager());
		server.open(PORT);
		server.close();

		Socket clientSocket;
		try {
			clientSocket = new Socket(InetAddress.getLoopbackAddress(), PORT);
		} catch (IOException e) {
			return; // connection was successfully refused
		}
		clientSocket.close();
		fail("Server was not closed");
	}
}
