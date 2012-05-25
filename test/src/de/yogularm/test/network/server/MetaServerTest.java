package de.yogularm.test.network.server;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;

import de.yogularm.multiplayer.Match;
import de.yogularm.multiplayer.Player;
import de.yogularm.multiplayer.ServerManager;
import de.yogularm.network.CommunicationError;
import de.yogularm.network.NetworkCommand;
import de.yogularm.network.server.ClientContext;
import de.yogularm.network.server.ServerContext;
import de.yogularm.network.server.ServerHandlerFactory;
import de.yogularm.network.server.meta.MetaHandler;
import de.yogularm.test.network.StreamPipe;

public class MetaServerTest {
	// test object:
	protected MetaHandler handler;
	
	// helpers
	protected ServerManager manager;
	protected ServerHandlerFactory handlerFactory;
	protected ServerContext context;
	protected StreamPipe s2c;
	protected StreamPipe c2s;
	protected ClientContext clientContext;
	protected Player player;
	protected Match match;
	
	protected static final int MATCH_ID = 123;
	protected static final String INVALID_MATCH_ID = "invalid_match_id";
	protected static final String PLAYER_NAME = "theplayername";
	protected static final String INVALID_PLAYER_NAME = "invalid/player/name";
	protected static final String MATCH_COMMENT = "the match's comment";
	protected static final String CLIENT_CONTEXT_KEY = "client-context-key";
	protected static final String WRONG_CLIENT_CONTEXT_KEY = "wrong-client-context-key";
	
	@Before
	public void setUp() throws IOException {
		manager = mock(ServerManager.class);
		handlerFactory = mock(ServerHandlerFactory.class);
		context = mock(ServerContext.class);
		when(context.getManager()).thenReturn(manager);
		
		// client context
		clientContext = mock(ClientContext.class);
		when(clientContext.getManager()).thenReturn(manager);
		when(clientContext.getKey()).thenReturn(CLIENT_CONTEXT_KEY);
		when(context.createClientContext()).thenReturn(clientContext);

		s2c = new StreamPipe(true); // server should call flush()
		c2s = new StreamPipe(false);
		handler = new MetaHandler(c2s.reader(), s2c.writer(), context, handlerFactory);
		
		// just prepared to be used by the tests
		player = mock(Player.class);
		when(player.getName()).thenReturn(PLAYER_NAME);
		match = mock(Match.class);
		when(match.getID()).thenReturn(MATCH_ID);
	}
	
	@After
	public void tearDown() throws IOException {
		s2c.close();
		c2s.close();
	}
	
	protected void sendCommand(NetworkCommand command) {
		c2s.writer().println(command.toString());
	}
	
	protected void sendCommand(NetworkCommand command, String parameter) {
		c2s.writer().println(String.format("%s %s", command, parameter));
	}
	
	protected String readResponse() throws IOException {
		return s2c.reader().readLine();
	}
	
	protected void verifyResponse(CommunicationError error) throws IOException {
		assertThat(readResponse(), startsWith("ERR " + error.toString()));
	}
	
	protected void verifyResponse(CommunicationError error, String message) throws IOException {
		assertThat(readResponse(), equalTo(String.format("ERR %s %s", error.toString(), message)));
	}
	
	protected void verifyResponseOK() throws IOException {
		assertThat(readResponse(), startsWith("OK"));
	}
	
	protected void verifyResponseOK(String message) throws IOException {
		assertThat(readResponse(), equalTo(String.format("OK %s", message)));
	}
	
	//TODO: renew, pause and continue commands
}
