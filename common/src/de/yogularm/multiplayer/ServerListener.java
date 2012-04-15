package de.yogularm.multiplayer;

public interface ServerListener {
	void playerAdded(Player player);
	void playerRemoved(Player player);
	void matchCreated(Match match);
	void matchChangedState(Match match, MatchState oldState, MatchState newState);
	void playerJoinedMatch(Player player, Match match);
	void playerLeftMatch(Player player, Match match);
	void messageReceived(Player sender, String message);
}
