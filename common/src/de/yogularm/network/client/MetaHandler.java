package de.yogularm.network.client;

import java.io.IOException;

import de.yogularm.multiplayer.Match;

public interface MetaHandler {
	/**
	 * Tries to log in with the specified name and requests a session key
	 * 
	 * @param name the name to chose as player name
	 * @return the session key if the name was available, or null otherwise
	 */
	String login(String name) throws IOException;
	void createMatch(String comment) throws IOException;
	void joinMatch(Match match) throws IOException;
	void leaveMatch() throws IOException;
	void startMatch() throws IOException;
	void pauseMatch() throws IOException;
	void resumeMatch() throws IOException;
	void cancelMatch() throws IOException;
	void sendMessage(String message) throws IOException;
}
