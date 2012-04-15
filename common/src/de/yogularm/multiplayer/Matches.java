package de.yogularm.multiplayer;

import de.yogularm.utils.ObservableMap;

public class Matches extends ObservableMap<Integer, Match> {
	public Match getByID(int id) {
		return get(id);
	}
	
	public void add(Match match) {
		add(match.getID(), match);
	}
	
	public void remove(Match match) {
		remove(match.getID());
	}
}
