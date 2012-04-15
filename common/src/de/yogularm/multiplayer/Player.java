package de.yogularm.multiplayer;

import java.util.Observable;

import com.google.gson.annotations.Expose;


/**
 * Represents a player in a multiplayer game
 * 
 * All methods of this class are thread-safe.
 * 
 * @author Yogu
 */
public class Player extends Observable {
	@Expose
	private final String name;
	
	private transient Match currentMatch;
	
	private final Object lock = new Object();
	
	private static final String NAME_REGEX = "[a-zA-Z0-9_-]+";

	public static enum ChangeAction {
		JOINED_MATCH,
		LEFT_MATCH
	}
	
	public class ChangeEvent {
		private Match match;
		private ChangeAction action;
		
		private ChangeEvent(ChangeAction action, Match match) {
			this.match = match;
			this.action = action;
		}
		
		public Player getPlayer() {
			return Player.this;
		}
		
		public Match getMatch() {
			return match;
		}
		
		public ChangeAction getAction() {
			return action;
		}
	}
	
	//public de.yogularm.components.Player playerComponent;
	
	/**
	 * Creates a player with no name.
	 * 
	 * This method is designed only for deserialization
	 */
	@SuppressWarnings("unused")
	private Player() {
		name = "";
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
	
	/**
	 * Assigns this player to the given match, adding it to the player list of the match
	 * 
	 * This method is thread-safe.
	 * 
	 * @param match The match to join
	 */
	public void joinMatch(Match match) {
		synchronized (lock) {
			if (currentMatch != match) {
				if (currentMatch != null)
					throw new IllegalStateException("This player is already assigned to a match");
				
				// First assign currentMatch, then call addPlayer because match would otherwise call joinMatch()
				currentMatch = match;
				match.addPlayer(this);
				setChanged();
			}
		}
		
		notifyObservers(new ChangeEvent(ChangeAction.JOINED_MATCH, match));
	}
	
	/**
	 * Removes this player from the assigned match, if it is assigned to the given match.
	 * 
	 * If this player is not assigned to the given match match, nothing is done.
	 * 
	 * This method is thread-safe.
	 * 
	 * @param match the match from which to remove this player
	 */
	public void leaveMatch(Match match) {
		synchronized (lock) {
			if (currentMatch == match) {
				// First set currentMatch to null, then call removePlayer because the latter would otherwise
				// call leaveMatch()
				currentMatch = null;
				match.removePlayer(this);
				setChanged();
			}
		}

		notifyObservers(new ChangeEvent(ChangeAction.LEFT_MATCH, match));
	}

	/**
	 * Removes this player from the assigned match, if it is assigned to one
	 * 
	 * If this player is not assigned to a match, nothing is done.
	 * 
	 * This method is thread-safe.
	 */
	public void leaveMatch() {
		Match match;
		synchronized (lock) {
			match = currentMatch;
			if (match != null) {
				// First set currentMatch to null, then call removePlayer because the latter would otherwise
				// call leaveMatch()
				currentMatch = null;
				match.removePlayer(this);
				setChanged();
			}
		}

		notifyObservers(new ChangeEvent(ChangeAction.LEFT_MATCH, match));
	}
	
	/**
	 * Gets the match this player is assigned to
	 * 
	 * @return the match this player is assigned to
	 */
	public Match getCurrentMatch() {
		return currentMatch;
	}
}
