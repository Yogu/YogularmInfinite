package de.yogularm.test.network;

import java.util.concurrent.TimeoutException;

import junit.framework.Assert;

import org.junit.Test;

import de.yogularm.multiplayer.Player;
import de.yogularm.multiplayer.ServerManager;
import de.yogularm.network.client.DefaultMetaHandler;
import de.yogularm.network.server.ServerContext;
import de.yogularm.network.server.StartHandler;
import de.yogularm.test.network.server.MockServerHandlerFactory;
import de.yogularm.test.network.server.MockServerManager;

public class MetaProtocolTest {
	private boolean registerPlayerCalled;
	private Exception threadException;
	
	private static final int TIMEOUT = 100;
	
	@Test
	public void testHello() throws Exception {
		final String loginName = "TheLoginName";
		
		MockServerManager manager = new MockServerManager() {
			@Override
			public Player registerPlayer(String name) {
				Assert.assertEquals(loginName, name);
				registerPlayerCalled = true;
				return new Player(loginName);
			}
		};
		
		ClientCode clientCode = new ClientCode() {
			public void run(DefaultMetaHandler client) throws Exception {
				String key = client.login(loginName);
				Assert.assertNotNull(key);
				Assert.assertTrue(registerPlayerCalled);
			}
		};
		
		test(clientCode, manager);
	}
	
	private void test(final ClientCode clientCode, ServerManager manager) throws Exception {
		final StreamPipe c2s = new StreamPipe(true);
		final StreamPipe s2c = new StreamPipe(true);
		final DefaultMetaHandler client = new DefaultMetaHandler(s2c.reader(), c2s.writer());
		final ServerContext context = new ServerContext(manager);
		final StartHandler server = new StartHandler(c2s.in(), s2c.out(), context,
				new MockServerHandlerFactory());

		Thread serverThread = new Thread(new Runnable() {
			public void run() {
				try {
					server.run();
				} catch (Exception e) {
					threadException = e;
				}
			}
		});
		
		Thread clientThread = new Thread(new Runnable() {
			public void run() {
				try {
					clientCode.run(client);
				} catch (Exception e) {
					threadException = e;
				}
			}
		});
		
		try {
			serverThread.start();
			clientThread.start();
			
			long startTime = System.currentTimeMillis();
			while (serverThread.isAlive() && clientThread.isAlive()) {
				if (System.currentTimeMillis() > startTime + TIMEOUT)
					throw new TimeoutException();
			}
			
			if (threadException != null)
				throw threadException;
		} finally {
			server.interrupt();
			serverThread.interrupt();
		}
	}
	
	private interface ClientCode {
		void run(DefaultMetaHandler client) throws Exception;
	}
}
