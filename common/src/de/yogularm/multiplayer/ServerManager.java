package de.yogularm.multiplayer;

import java.util.Collection;

public interface ServerManager {
	Player registerPlayer(String name);
	void removePlayer(Player player);
	Collection<Player> getPlayers();
	Player getPlayerByName(String name);
	
	Match startNewMatch(Player owner, String comment);
	Collection<Match> getMatches();
	Match getMatchByID(int id);
	MatchManager getMatchManager(Match match);
	
	void sendMessage(Player sender, String message);
	
	void addListener(ServerListener listener);
	void removeListener(ServerListener listener);
}
