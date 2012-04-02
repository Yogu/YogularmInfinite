package de.yogularm.server;

import java.util.HashSet;
import java.util.Set;

import de.yogularm.network.Player;
import de.yogularm.network.Players;

/**
 * A subclass of Players that makes sure that no name (ignoring case) is taken twice
 * 
 * @author Yogu
 */
public class PlayerManager extends Players {
	private Object lock = new Object();
	private Set<String> takenNames = new HashSet<String>();

	/**
	 * Creates and adds a new player with the given name, if the name is not already taken
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
		add(player);
		return player;
	}
	
	@Override
	public void remove(String key) {
		takenNames.remove(key.toLowerCase());
		super.remove(key);
	}
}
