package de.yogularm.server;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class Players {
	private Object lock = new Object();
	private SecureRandom random = new SecureRandom();
	private Map<String, Player> playersByName = new HashMap<String, Player>();
	private Map<String, Player> playersByKey = new HashMap<String, Player>();

	public Player registerPlayer(String name) {
		synchronized (lock) {
			if (!playersByName.containsKey(name.toLowerCase())) {
				Player player = new Player(name);
				playersByName.put(name.toLowerCase(), player);
				String key = generateKey();
				playersByKey.put(key, player);
				return player;
			}
		}
		return null;
	}
	
	public Player getPlayerByKey(String key) {
		synchronized (lock) {
			return playersByKey.get(key);
		}
	}

	
	public Player getPlayerByName(String name) {
		synchronized (lock) {
			return playersByKey.get(name.toLowerCase());
		}
	}
	public void removePlayer(Player player) {
		synchronized (lock) {
			playersByName.values().remove(player);
			playersByKey.values().remove(player);
		}
	}
	
	private String generateKey() {
		return new BigInteger(128, random).toString(16);
	}
}
