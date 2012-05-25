package de.yogularm.test.network.server;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import de.yogularm.multiplayer.Match;
import de.yogularm.multiplayer.MatchState;
import de.yogularm.multiplayer.Player;
import de.yogularm.multiplayer.ServerListener;
import de.yogularm.multiplayer.ServerManager;
import de.yogularm.network.NetworkInformation;
import de.yogularm.network.server.ClientContext;
import de.yogularm.network.server.ServerHandlerFactory;
import de.yogularm.network.server.meta.PassiveHandler;
import de.yogularm.test.network.StreamPipe;

public class PassiveHandlerTest {
	private PassiveHandler passiveHandler;
	
	private ServerManager manager;
	private ClientContext context;
	private ServerHandlerFactory handlerFactory;
	private StreamPipe s2c;
	private ServerListener listener;
	
	@Before
	public void setUp() throws IOException {
		context = mock(ClientContext.class);
		handlerFactory = mock(ServerHandlerFactory.class);
		
		manager = mock(ServerManager.class);
		when(context.getManager()).thenReturn(manager);
		
		s2c = new StreamPipe(true); // server should flush
		passiveHandler = new PassiveHandler(s2c.writer(), context, handlerFactory);

		ArgumentCaptor<ServerListener> listenerCaptor = ArgumentCaptor.forClass(ServerListener.class);
		verify(manager).addListener(listenerCaptor.capture());
		listener = listenerCaptor.getValue();
	}
	
	@After
	public void tearDown() throws IOException {
		s2c.close();
	}
	
	@Test
	public void testRemovesHandler() throws IOException {
		passiveHandler.interrupt();
		passiveHandler.run();

		verify(manager).removeListener(any(ServerListener.class));
	}
	
	@Test
	public void testPlayerJoinedServer() throws IOException {
		Player player = new Player("theplayername");
		String json = "{\"name\":\"theplayername\"}";
		
		listener.playerAdded(player);
		
		passiveHandler.interrupt();
		passiveHandler.run();
		
		expectInformation(NetworkInformation.PLAYER_JOINED_SERVER, json);
	}
	
	@Test
	public void testPlayerLeftServer() throws IOException {
		Player player = new Player("theplayername");
		
		listener.playerRemoved(player);
		
		passiveHandler.interrupt();
		passiveHandler.run();
		
		expectInformation(NetworkInformation.PLAYER_LEFT_SERVER, player.getName());
	}
	
	@Test
	public void testMatchCreated() throws IOException {
		Player owner = new Player("theowner");
		Match match = new Match(owner);
		match.setComment("c");
		Player player = new Player("secondplayer");
		match.addPlayer(player);
		String json =
			"{\"id\":" + match.getID() + ",\"owner\":{\"name\":\"theowner\"},\"players\":" + 
			"[{\"name\":\"theowner\"},{\"name\":\"secondplayer\"}],\"state\":\"OPEN\",\"comment\":\"c\"}";
		
		listener.matchCreated(match);
		passiveHandler.interrupt();
		passiveHandler.run();
		
		expectInformation(NetworkInformation.MATCH_CREATED, json);
	}
	
	@Test
	public void testPlayerJoinedMatch() throws IOException {
		Match match = new Match(new Player("theowner"));
		Player player = new Player("secondplayer");
		
		listener.playerJoinedMatch(player, match);
		passiveHandler.interrupt();
		passiveHandler.run();
		
		expectInformation(NetworkInformation.PLAYER_JOINED_MATCH, "secondplayer " + match.getID());
	}
	
	@Test
	public void testPlayerLeftMatch() throws IOException {
		Match match = new Match(new Player("theowner"));
		Player player = new Player("secondplayer");
		
		listener.playerLeftMatch(player, match);
		passiveHandler.interrupt();
		passiveHandler.run();
		
		expectInformation(NetworkInformation.PLAYER_LEFT_MATCH, "secondplayer " + match.getID());
	}
	
	@Test
	public void testMatchStarted() throws IOException {
		Match match = new Match(new Player("theowner"));
		
		listener.matchChangedState(match, MatchState.OPEN, MatchState.RUNNING);
		passiveHandler.interrupt();
		passiveHandler.run();
		
		expectInformation(NetworkInformation.MATCH_STARTED, match.getID() + "");
	}
	
	@Test
	public void testMatchPaused() throws IOException {
		Match match = new Match(new Player("theowner"));
		
		listener.matchChangedState(match, MatchState.RUNNING, MatchState.PAUSED);
		passiveHandler.interrupt();
		passiveHandler.run();
		
		expectInformation(NetworkInformation.MATCH_PAUSED, match.getID() + "");
	}
	
	@Test
	public void testMatchResumed() throws IOException {
		Match match = new Match(new Player("theowner"));
		
		listener.matchChangedState(match, MatchState.PAUSED, MatchState.RUNNING);
		passiveHandler.interrupt();
		passiveHandler.run();
		
		expectInformation(NetworkInformation.MATCH_RESUMED, match.getID() + "");
	}
	
	@Test
	public void testRunningMatchCancelled() throws IOException {
		Match match = new Match(new Player("theowner"));
		
		listener.matchChangedState(match, MatchState.RUNNING, MatchState.CANCELLED);
		passiveHandler.interrupt();
		passiveHandler.run();
		
		expectInformation(NetworkInformation.MATCH_CANCELLED, match.getID() + "");
	}
	
	@Test
	public void testPausedMatchCancelled() throws IOException {
		Match match = new Match(new Player("theowner"));
		
		listener.matchChangedState(match, MatchState.PAUSED, MatchState.CANCELLED);
		passiveHandler.interrupt();
		passiveHandler.run();
		
		expectInformation(NetworkInformation.MATCH_CANCELLED, match.getID() + "");
	}
	
	@Test
	public void testOpenMatchCancelled() throws IOException {
		Match match = new Match(new Player("theowner"));
		
		listener.matchChangedState(match, MatchState.OPEN, MatchState.CANCELLED);
		passiveHandler.interrupt();
		passiveHandler.run();
		
		expectInformation(NetworkInformation.MATCH_CANCELLED, match.getID() + "");
	}
	
	@Test
	public void testMessageReceived() throws IOException {
		Player sender = new Player("thesender");
		
		listener.messageReceived(sender, "themessage");
		passiveHandler.interrupt();
		passiveHandler.run();
		
		expectInformation(NetworkInformation.MESSAGE, "thesender themessage");
	}
	
	protected String readInformation() throws IOException {
		return s2c.reader().readLine();
	}
	
	protected void expectInformation(NetworkInformation information) throws IOException {
		assertThat(readInformation(), equalTo(information.toString()));
	}
	
	protected void expectInformation(NetworkInformation information, String parameter) throws IOException {
		assertThat(readInformation(), equalTo(String.format("%s %s", information, parameter)));
	}
}
