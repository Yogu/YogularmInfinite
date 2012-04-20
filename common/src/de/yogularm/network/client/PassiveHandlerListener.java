package de.yogularm.network.client;

import de.yogularm.multiplayer.Match;
import de.yogularm.multiplayer.MatchState;
import de.yogularm.multiplayer.Player;

public interface PassiveHandlerListener {
	void playerJoinedServer(Player player);
	void playerLeftServer(String playerName);
	void matchCreated(Match match);
	void matchChangedState(int matchID, MatchState newState);
	void playerJoinedMatch(String playerName, int matchID);
	void playerLeftMatch(String playerName, int matchID);
	void messageReceived(String playerName, String message);
}
