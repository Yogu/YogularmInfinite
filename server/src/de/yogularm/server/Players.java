package de.yogularm.server;

import java.util.HashMap;
import java.util.Map;

import de.yogularm.network.Player;

public class Players {
	private Object lock = new Object();
	private Map<String, Player> playersByName = new HashMap<String, Player>();

	public Player registerPlayer(String name) {
		synchronized (lock) {
			if (!playersByName.containsKey(name.toLowerCase())) {
				Player player = new Player(name);
				playersByName.put(name.toLowerCase(), player);
				return player;
			}
		}
		return null;
	}

	public Player getPlayerByName(String name) {
		synchronized (lock) {
			return playersByName.get(name.toLowerCase());
		}
	}

	public void removePlayer(Player player) {
		synchronized (lock) {
			playersByName.values().remove(player);
		}
	}
}
