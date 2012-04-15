package de.yogularm.multiplayer;

import java.util.HashSet;
import java.util.Set;

/**
 * A subclass of Players that makes sure that no name (ignoring case) is taken
 * twice
 * 
 * @author Yogu
 */
public class PlayerManager extends Players {
	private final Object lock = new Object();
	private final Set<String> takenNames = new HashSet<String>();

	/**
	 * Creates and adds a new player with the given name, if the name is not
	 * already taken
	 * 
	 * For checking whether a name is available, the case is ignored.
	 * 
	 * @param name the player's name
	 * @return the created player if the name was available, null otherwise
	 */
	public Player registerPlayer(String name) {
		synchronized (lock) {
			if (!takenNames.contains(name.toLowerCase())) {
				takenNames.add(name.toLowerCase());
			} else
				return null;
		}
		Player player = new Player(name);
		super.add(player);
		return player;
	}

	/**
	 * This method should not be called; instead, registerPlayer() should be used.
	 */
	public void add(Player player) {
		throw new UnsupportedOperationException(
				"Do not call PlayerManager.add(), use registerPlayer instead");
	}

	@Override
	public void remove(String key) {
		synchronized (lock) {
			takenNames.remove(key.toLowerCase());
		}
		super.remove(key);
	}
}
