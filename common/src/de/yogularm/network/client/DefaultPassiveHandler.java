package de.yogularm.network.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;

import de.yogularm.multiplayer.Match;
import de.yogularm.multiplayer.MatchState;
import de.yogularm.multiplayer.Player;
import de.yogularm.network.AbstractBackgroundHandler;
import de.yogularm.network.NetworkCommand;
import de.yogularm.network.NetworkInformation;
import de.yogularm.utils.GsonFactory;


public class DefaultPassiveHandler extends AbstractBackgroundHandler implements PassiveHandler {
	private BufferedReader in;
	private PrintWriter out;
	private String sessionKey;
	private Collection<PassiveHandlerListener> listeners = new ArrayList<PassiveHandlerListener>();
	
	public DefaultPassiveHandler(BufferedReader in, PrintWriter out, String sessionKey) {
		this.in = in;
		this.out = out;
	}

	@Override
	public void addListener(PassiveHandlerListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	@Override
	public void removeListener(PassiveHandlerListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	/**
	 * Initializes the streams as text streams, enters passive mode and begins listening
	 * 
	 * @throws IOException the session key is incorrect - or - some kind of i/o error occurred
	 */
	@Override
	public void run() throws IOException {
		MetaCommandClient commands = new MetaCommandClient(in, out);
		commands.sendCommand(NetworkCommand.PASSIVE, sessionKey);
		
		while (!isInterrupted()) {
			String line = in.readLine();
			if (line == null)
				break; // server closed connection

			System.out.println("Information: " + line);
			String[] parts = line.split("\\s", 2);
			if (parts.length < 1)
				throw new IOException("Invalid line in passive mode: " + line);
			NetworkInformation information = NetworkInformation.fromString(parts[0]);
			if (information == null)
				throw new IOException("Invalid network information in passive mode: " + parts[0]);
			String parameter = parts.length >= 2 ? parts[1] : "";
			handleNetworkInformation(information, parameter);
		}
	}

	private void handleNetworkInformation(NetworkInformation information, String parameter) {
		// used several times
		int matchID;
		String[] parts;
		MatchState state;

		switch (information) {
		case MATCH_CREATED:
			Match match = GsonFactory.createGson().fromJson(parameter, Match.class);
			
			synchronized (listeners) {
				for (PassiveHandlerListener listener : listeners)
					listener.matchCreated(match);
			}
			break;

		case MATCH_CANCELLED:
			state = MatchState.CANCELLED;
		case MATCH_PAUSED:
			state = MatchState.PAUSED;
		case MATCH_RESUMED:
			state = MatchState.RUNNING;
		case MATCH_STARTED:
			state = MatchState.RUNNING;
			matchID = Integer.parseInt(parameter);

			synchronized (listeners) {
				for (PassiveHandlerListener listener : listeners)
					listener.matchChangedState(matchID, state);
			}
			break;
						
		case PLAYER_JOINED_SERVER:
			Player player = GsonFactory.createGson().fromJson(parameter, Player.class);

			synchronized (listeners) {
				for (PassiveHandlerListener listener : listeners)
					listener.playerJoinedServer(player);
			}
			break;
						
		case PLAYER_LEFT_SERVER:
			synchronized (listeners) {
				for (PassiveHandlerListener listener : listeners)
					listener.playerLeftServer(parameter);
			}
			break;
			
		case PLAYER_JOINED_MATCH:
		case PLAYER_LEFT_MATCH:
			parts = parameter.split("\\s", 2);
			assert parts.length >= 2;
			String playerName = parts[0];
			matchID = Integer.parseInt(parts[1]);
			switch (information) {
			case PLAYER_JOINED_MATCH:
				synchronized (listeners) {
					for (PassiveHandlerListener listener : listeners)
						listener.playerJoinedMatch(playerName, matchID);
				}
				break;
			case PLAYER_LEFT_MATCH:
				synchronized (listeners) {
					for (PassiveHandlerListener listener : listeners)
						listener.playerLeftMatch(playerName, matchID);
				}
				return;
			}
			
		case MESSAGE:
			parts = parameter.split("\\s", 2);
			assert parts.length >= 2;

			synchronized (listeners) {
				for (PassiveHandlerListener listener : listeners)
					listener.messageReceived(parts[0], parts[1]);
			}
		}
	}
}
