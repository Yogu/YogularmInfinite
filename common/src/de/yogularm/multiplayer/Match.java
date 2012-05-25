package de.yogularm.multiplayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import com.google.gson.annotations.Expose;

/**
 * Represents a match
 * 
 * All methods of this class are thread-safe.
 * 
 * @author Yogu
 */
public class Match extends Observable {
	@Expose
	private final int id;
	@Expose
	private final Player owner;
	@Expose
	private Set<Player> players = new HashSet<Player>();
	@Expose
	private MatchState state = MatchState.OPEN;
	@Expose
	private String comment;
	
	private final Object lock = new Object();

	// public ServerGame game;

	private static final Object idLock = new Object();
	private static int NEXT_ID = 1;

	public static enum ChangeAction {
		STATE_CHANGED,
		PLAYER_JOINED,
		PLAYER_LEFT,
		COMMENT_CHANGED
	}
	
	public class ChangeEvent {
		private ChangeAction action;
		private Player player;
		private MatchState oldState;
		private MatchState newState;

		private ChangeEvent(ChangeAction action) {
			this.action = action;
		}
		
		private ChangeEvent(ChangeAction action, Player player) {
			this.action = action;
			this.player = player;
		}
		
		private ChangeEvent(ChangeAction action, MatchState oldState, MatchState newState) {
			this.action = action;
			this.oldState = oldState;
			this.newState = newState;
		}
		
		public Match getMatch() {
			return Match.this;
		}
		
		public ChangeAction getAction() {
			return action;
		}
		
		public Player getPlayer() {
			return player;
		}
		
		public MatchState getOldState() {
			return oldState;
		}
		
		public MatchState getNewState() {
			return newState;
		}
	}

	// only for deserialization purpose
	@SuppressWarnings("unused")
	private Match() {
		id = 0;
		owner = null;
		comment = "";
	}

	public Match(Player owner) {
		synchronized (idLock) {
			id = NEXT_ID;
			NEXT_ID++;
		}
		this.owner = owner;
		comment = "";
		addPlayer(owner);
	}

	/**
	 * Lets the given player join this match and makes sure that it is in the
	 * player list of this match.
	 * 
	 * This method is thread-safe.
	 * 
	 * @param player to add to this match
	 */
	public void addPlayer(Player player) {
		if (player == null)
			throw new NullPointerException("player is null");
		
		player.joinMatch(this);

		synchronized (lock) {
			if (players.add(player))
				setChanged();
		}

		notifyObservers(new ChangeEvent(ChangeAction.PLAYER_JOINED, player));
	}

	/**
	 * Lets the given player leave this match and makes sure that it is not in the
	 * player list of this match.
	 * 
	 * If the last player has left, the match is cancelled.
	 * 
	 * This method is thread-safe.
	 * 
	 * @param player the player to remove from this match
	 */
	public void removePlayer(Player player) {
		if (player == null)
			throw new NullPointerException("player is null");
		
		player.leaveMatch(this);

		synchronized (lock) {
			if (players.remove(player))
				setChanged();
		}

		notifyObservers(new ChangeEvent(ChangeAction.PLAYER_LEFT, player));
		
		if (players.isEmpty())
			cancel(); // Calls notifyObservers() with the STATE_CHANGED event
	}

	/**
	 * If this match is open, starts this match
	 * 
	 * This method is thread-safe.
	 * 
	 * @throws IllegalStateException the match is already over
	 */
	public void start() {
		ChangeEvent event = null;
		synchronized (lock) {
			if (state == MatchState.OPEN) {
				event = setState(MatchState.RUNNING);
			} else if (isOver())
				throw new IllegalStateException();
		}
		
		if (event != null)
			notifyObservers(event);
	}

	/*
	 * public void startOnServer() { start();
	 * 
	 * game = new ServerGame(); int x = 0; for (Player player : players) {
	 * de.yogularm.components.Player component = new
	 * de.yogularm.components.Player(game.getWorld().getComponents());
	 * game.getWorld().getComponents().add(component); component.setPosition(new
	 * Vector(x, 0)); x--; player.playerComponent = component; } game.start(); }
	 */

	/**
	 * Pauses this match and freezes all components
	 * 
	 * This method is thread-safe.
	 * 
	 * @throws IllegalStateException the match is not started
	 */
	public void pause() {
		ChangeEvent event = null;
		synchronized (lock) {
			if (isStarted())
				event = setState(MatchState.PAUSED);
			else
				throw new IllegalStateException();
		}
		
		if (event != null)
			notifyObservers(event);
	}

	/**
	 * Resumes a paused match
	 * 
	 * This method is thread-safe.
	 * 
	 * @throws IllegalStateException the match is not started
	 */
	public void resume() {
		ChangeEvent event = null;
		synchronized (lock) {
			if (isStarted())
				event = setState(MatchState.RUNNING);
			else
				throw new IllegalStateException();
		}
		
		if (event != null)
			notifyObservers(event);
	}

	/**
	 * Cancels this match
	 * 
	 * This method is thread-safe.
	 * 
	 * @throws IllegalStateException the match is already finished
	 */
	public void cancel() {
		ChangeEvent event = null;
		synchronized (lock) {
			if (state != MatchState.FINISHED) {
				event = setState(MatchState.CANCELLED);
	
				// Copy the list to avoid concurrency issues
				List<Player> players = new ArrayList<Player>(getPlayers());
				for (Player player : players) {
					player.leaveMatch(this);
				}
	
				/*
				 * if (game != null) game.stop();
				 */
			} else
				throw new IllegalStateException();
		}
		
		if (event != null)
			notifyObservers(event);
	}

	private ChangeEvent setState(MatchState newState) {
		MatchState oldState = this.state;
		if (oldState != newState) {
			this.state = newState;
			setChanged();
			return new ChangeEvent(ChangeAction.STATE_CHANGED, oldState, newState);
		} else
			return null;
	}

	public void setComment(String comment) {
		if (comment == null)
			throw new NullPointerException("comment is null");
		
		synchronized (lock) {
			if (!comment.equals(this.comment)) {
				this.comment = comment;
				setChanged();
			}
		}
		notifyObservers(new ChangeEvent(ChangeAction.COMMENT_CHANGED));
	}

	public int getID() {
		return id;
	}

	public Player getOwner() {
		return owner;
	}

	public MatchState getState() {
		return state;
	}
	
	public boolean isOpen() {
		return state == MatchState.OPEN;
	}
	
	public boolean isPaused() {
		return state == MatchState.PAUSED;
	}

	public boolean isStarted() {
		MatchState s = state;
		return s == MatchState.RUNNING || s == MatchState.PAUSED;
	}
	
	public boolean isOver() {
		MatchState s = state;
		return s == MatchState.CANCELLED || s == MatchState.FINISHED;
	}
	
	public boolean isCancelled() {
		return state == MatchState.CANCELLED;
	}

	/**
	 * Gets an immutable set of the players assigned to this match
	 * 
	 * @return the players assigned to this match
	 */
	public Set<Player> getPlayers() {
		return Collections.unmodifiableSet(new HashSet<Player>(players));
	}

	public String getComment() {
		return comment;
	}

	public boolean equals(Match other) {
		return other != null && other.id == id;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Match)
			return equals((Match) other);
		else
			return false;
	}

	@Override
	public int hashCode() {
		return id;
	}
}
