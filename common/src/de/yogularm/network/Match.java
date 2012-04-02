package de.yogularm.network;

import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

import com.google.gson.annotations.Expose;

public class Match extends Observable {
	@Expose
	private int id;
	@Expose
	private Player owner;
	@Expose
	private Set<Player> players = new HashSet<Player>();
	@Expose
	private MatchState state = MatchState.OPEN;
	@Expose
	private String comment;
	
	private static final Object idLock = new Object();
	private static int NEXT_ID = 1;
	
	// only for deserialization purpose
	@SuppressWarnings("unused")
	private Match() {
		
	}
	
	public Match(Player owner) {
		synchronized (idLock) {
			id = NEXT_ID;
			NEXT_ID++;
		}
		this.owner = owner;
	}
	
	public void addPlayer(Player player) {
		if (players.add(player)) {
			setChanged();
			notifyObservers();
		}
	}
	
	public void removePlayer(Player player) {
		if (players.remove(player)) {
			setChanged();
			notifyObservers();
		}
	}
	
	public void start() {
		if (state == MatchState.OPEN) {
			setState(MatchState.RUNNING);
		} else
			throw new IllegalStateException();
	}
	
	public void pause() {
		if ((state == MatchState.RUNNING) || (state == MatchState.PAUSED))
			setState(MatchState.PAUSED);
		else
			throw new IllegalStateException();
	}
	
	public void resume() {
		if ((state == MatchState.RUNNING) || (state == MatchState.PAUSED))
			setState(MatchState.RUNNING);
		else
			throw new IllegalStateException();
	}
	
	public void cancel() {
		if ((state != MatchState.CANCELLED) && (state != MatchState.FINISHED)) {
			setState(MatchState.CANCELLED);
			
			for (Player player: getPlayers()) {
				player.leaveMatch();
			}
		} else
			throw new IllegalStateException();
	}
	
	private void setState(MatchState state) {
		this.state = state;
		setChanged();
		notifyObservers();
	}
	
	public void setComment(String comment) {
		this.comment = comment;
		setChanged();
		notifyObservers();
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
	
	public Set<Player> getPlayers() {
		return players;
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
			return equals((Match)other);
		else
			return false;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
}
