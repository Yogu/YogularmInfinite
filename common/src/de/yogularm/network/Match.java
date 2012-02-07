package de.yogularm.network;

import java.util.HashSet;
import java.util.Set;

public class Match {
	private int id;
	private Player owner;
	private Set<Player> players = new HashSet<Player>();
	private MatchState state = MatchState.OPEN;
	
	private static final Object idLock = new Object();
	private static int NEXT_ID = 1;
	
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
		players.add(player);
	}
	
	public void removePlayer(Player player) {
		players.remove(player);
	}
	
	public void start() {
		if (state == MatchState.OPEN)
			state = MatchState.RUNNING;
		else
			throw new IllegalStateException();
	}
	
	public void pause() {
		if ((state == MatchState.RUNNING) || (state == MatchState.PAUSED))
			state = MatchState.PAUSED;
		else
			throw new IllegalStateException();
	}
	
	public void resume() {
		if ((state == MatchState.RUNNING) || (state == MatchState.PAUSED))
			state = MatchState.RUNNING;
		else
			throw new IllegalStateException();
	}
	
	public void cancel() {
		if ((state == MatchState.RUNNING) || (state == MatchState.PAUSED))
			state = MatchState.CANCELLED;
		else
			throw new IllegalStateException();
	}
	
	public MatchState getState() {
		return state;
	}
	
	public Set<Player> getPlayers() {
		return players;
	}
	
	public int getID() {
		return id;
	}
	
	public String serialize() {
		String str = id + ":";
		boolean isFirst = true;
		for (Player player : players) {
			if (!isFirst)
				str += ",";
			else
				isFirst = false;
			str += player.getName();
		}
		return str;
	}
	
	public static Match deserialize(String str) {
		String[] parts = str.split("\\:", 2);
		if (parts.length < 2)
			return null;

		Match match = new Match();
		
		int id;
		try {
			id = Integer.parseInt(parts[0]);
		} catch (NumberFormatException e) {
			return null;
		}
		if (id <= 0)
			return null;
		match.id = id;
		
		String[] players = parts[1].split("\\,");
		for (String playerName : players) {
			Player player = new Player(playerName);
			if (!Player.isValidName(playerName))
				return null;
			
			match.players.add(player);
		}
		
		return match;
	}
}
