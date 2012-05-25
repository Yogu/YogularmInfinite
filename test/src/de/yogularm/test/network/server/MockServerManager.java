package de.yogularm.test.network.server;

import static org.junit.Assert.fail;

import java.util.Collection;

import de.yogularm.multiplayer.Match;
import de.yogularm.multiplayer.MatchManager;
import de.yogularm.multiplayer.Player;
import de.yogularm.multiplayer.ServerListener;
import de.yogularm.multiplayer.ServerManager;

public class MockServerManager implements ServerManager {

	@Override
	public Player registerPlayer(String name) {
		fail();
		return null;
	}

	@Override
	public void removePlayer(Player player) {
		fail();
	}

	@Override
	public Collection<Player> getPlayers() {
		fail();
		return null;
	}

	@Override
	public Player getPlayerByName(String name) {
		fail();
		return null;
	}

	@Override
	public Match startNewMatch(Player owner, String comment) {
		fail();
		return null;
	}

	@Override
	public Collection<Match> getMatches() {
		fail();
		return null;
	}

	@Override
	public Match getMatchByID(int id) {
		fail();
		return null;
	}

	@Override
	public MatchManager getMatchManager(Match match) {
		fail();
		return null;
	}

	@Override
	public void sendMessage(Player sender, String message) {
		fail();
	}

	@Override
	public void addListener(ServerListener listener) {
		fail();
	}

	@Override
	public void removeListener(ServerListener listener) {
		fail();
	}

}
