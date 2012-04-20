package de.yogularm.network.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import de.yogularm.multiplayer.Match;
import de.yogularm.network.CommunicationError;
import de.yogularm.network.CommunicationException;
import de.yogularm.network.NetworkCommand;

/**
 * This class is not thread-safe.
 */
public class DefaultMetaHandler implements MetaHandler {
	private MetaCommandClient commands;

	public DefaultMetaHandler(BufferedReader in, PrintWriter out) {
		commands = new MetaCommandClient(in, out);
	}

	/**
	 * Tries to log in with the specified name and requests a session key
	 * 
	 * @param name the name to chose as player name
	 * @return the session key if the name was available, or null otherwise
	 */
	@Override
	public String login(String name) throws IOException {
		try {
			return commands.sendCommand(NetworkCommand.HELLO, name);
		} catch (CommunicationException e) {
			if (e.getError() == CommunicationError.NAME_NOT_AVAILABLE) {
				return null;
			} else
				throw e;
		}
	}

	@Override
	public void createMatch(String comment) throws IOException {
		commands.sendCommand(NetworkCommand.CREATE);
	}

	@Override
	public void joinMatch(Match match) throws IOException {
		commands.sendCommand(NetworkCommand.JOIN, match.getID() + "");
	}

	@Override
	public void leaveMatch() throws IOException {
		commands.sendCommand(NetworkCommand.LEAVE);
	}

	@Override
	public void startMatch() throws IOException {
		commands.sendCommand(NetworkCommand.START);
	}

	@Override
	public void pauseMatch() throws IOException {
		commands.sendCommand(NetworkCommand.PAUSE);
	}

	@Override
	public void resumeMatch() throws IOException {
		commands.sendCommand(NetworkCommand.RESUME);
	}

	@Override
	public void cancelMatch() throws IOException {
		commands.sendCommand(NetworkCommand.CANCEL);
	}

	@Override
	public void sendMessage(String message) throws IOException {
		commands.sendCommand(NetworkCommand.SAY, message);
	}
}
