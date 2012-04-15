package de.yogularm.multiplayer;

import de.yogularm.utils.ObservableMap;

public class Players extends ObservableMap<String, Player> {
	public Player getByName(String name) {
		return get(name);
	}
	
	public void add(Player player) {
		add(player.getName(), player);
	}
	
	public void remove(Player player) {
		remove(player.getName());
	}
}
