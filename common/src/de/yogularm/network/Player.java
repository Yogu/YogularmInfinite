package de.yogularm.network;

import java.util.Observable;

import com.google.gson.annotations.Expose;

public class Player extends Observable {
	@Expose
	private String name;
	
	private transient Match currentMatch;
	
	private static final String NAME_REGEX = "[a-zA-Z0-9_-]+";
	
	/**
	 * Creates a player with no name.
	 * 
	 * This method is designed only for deserialization
	 */
	@SuppressWarnings("unused")
	private Player() {
		
	}
	
	public Player(String name) {
		if (name == null)
			throw new NullPointerException("name is null");
		
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return "Player " + name;
	}
	
	public static boolean isValidName(String name) {
		return name != null && name.matches(NAME_REGEX);
	}
	
	public boolean equals(Player other) {
		return other != null && other.name != null && other.name.equals(name);
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Player)
			return equals((Player)other);
		else
			return false;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	public void joinMatch(Match match) {
		if (currentMatch != null)
			throw new IllegalStateException("This player is already assigned to a match");
		
		match.addPlayer(this);
		currentMatch = match;
		setChanged();
		notifyObservers();
	}
	
	public void leaveMatch() {
		if (currentMatch != null) {
			currentMatch.removePlayer(this);
			currentMatch = null;
			setChanged();
			notifyObservers();
		}
	}
	
	public Match getCurrentMatch() {
		return currentMatch;
	}
}
