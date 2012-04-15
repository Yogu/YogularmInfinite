package de.yogularm.multiplayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.yogularm.multiplayer.Match.ChangeAction;
import de.yogularm.utils.ListListener;

public class DefaultServerManager implements ServerManager {
	private PlayerManager players = new PlayerManager();
	private Matches matches = new Matches();
	private Map<Match, MatchManager> matchManagers = new HashMap<Match, MatchManager>();
	private Collection<ServerListener> listeners = new ArrayList<ServerListener>();
	
	public DefaultServerManager() {
		players.addListener(new ListListener<Player>() {			
			@Override
			public void itemRemoved(Player player) {
				Collection<ServerListener> ls;
				synchronized (listeners) {
					ls = new ArrayList<ServerListener>(listeners);
				}
				for (ServerListener listener : ls) {
					listener.playerRemoved(player);
				}
			}
			
			@Override
			public void itemChanged(Player item, Object arg) {
				
			}
			
			@Override
			public void itemAdded(Player player) {
				Collection<ServerListener> ls;
				synchronized (listeners) {
					ls = new ArrayList<ServerListener>(listeners);
				}
				for (ServerListener listener : ls) {
					listener.playerAdded(player);
				}
			}
		});
		
		matches.addListener(new ListListener<Match>() {
			@Override
			public void itemRemoved(Match item) {
				// Matches 
			}
			
			@Override
			public void itemChanged(Match item, Object arg) {
				if (arg instanceof Match.ChangeEvent) {
					Match.ChangeEvent event = (Match.ChangeEvent)arg;

					if (event.getAction() == ChangeAction.STATE_CHANGED) {
						switch (event.getNewState()) {
						case CANCELLED:
						case FINISHED:
							// Remove matches that are over
							matches.remove(event.getMatch());
							break;
							
						case RUNNING:
							// Start matches
							if (event.getOldState() == MatchState.OPEN) {
								startMatch(event.getMatch());
							}
						}
					}
					
					Collection<ServerListener> ls;
					synchronized (listeners) {
						ls = new ArrayList<ServerListener>(listeners);
					}
					for (ServerListener listener : ls) {
						switch (event.getAction()) {
						case PlAYER_JOINED:
							listener.playerJoinedMatch(event.getPlayer(), event.getMatch());
							break;
						case PLAYER_LEFT:
							listener.playerLeftMatch(event.getPlayer(), event.getMatch());
							break;
						case STATE_CHANGED:
							listener.matchChangedState(event.getMatch(), event.getOldState(), event.getNewState());
							break;
						}
					}
				}
			}
			
			@Override
			public void itemAdded(Match match) {
				Collection<ServerListener> ls;
				synchronized (listeners) {
					ls = new ArrayList<ServerListener>(listeners);
				}
				for (ServerListener listener : ls) {
					listener.matchCreated(match);
				}
			}
		});
	}

	@Override
	public Player registerPlayer(String name) {
		if (name == null)
			throw new NullPointerException("name is null");
		
		return players.registerPlayer(name);
	}

	@Override
	public void removePlayer(Player player) {
		if (player == null)
			throw new NullPointerException("player is null");
		
		player.leaveMatch();
		players.remove(player);
	}

	@Override
	public Collection<Player> getPlayers() {
		return players.getUnmodifiableCollection();
	}

	@Override
	public Player getPlayerByName(String name) {
		return players.getByName(name);
	}

	@Override
	public void sendMessage(Player sender, String message) {
		if (sender == null)
			throw new NullPointerException("sender is null");
		if (message == null)
			throw new NullPointerException("message is null");
		
		Collection<ServerListener> ls;
		synchronized (listeners) {
			ls = new ArrayList<ServerListener>(listeners);
		}
		for (ServerListener listener : ls) {
			listener.messageReceived(sender, message);
		}
	}

	@Override
	/**
	 * @throws IllegalStateException the owner is already assigned to a match
	 */
	public Match startNewMatch(Player owner, String comment) {
		if (owner == null)
			throw new NullPointerException("owner is null");
		if (comment == null)
			throw new NullPointerException("comment is null");
		if (!players.containsValue(owner))
			throw new IllegalArgumentException("The given owner is no player of this server manager");
		
		Match match = new Match(owner);
		synchronized (matches) {
			matches.add(match);
		}
		return match;
	}

	@Override
	public Collection<Match> getMatches() {
		return matches.getUnmodifiableCollection();
	}

	@Override
	public Match getMatchByID(int id) {
		return matches.getByID(id);
	}
	
	@Override
	public MatchManager getMatchManager(Match match) {
		if (match == null)
			throw new NullPointerException("match is null");
		
		synchronized (matchManagers) {
			return matchManagers.get(match);
		}
	}

	@Override
	public void addListener(ServerListener listener) {
		if (listener == null)
			throw new NullPointerException("listener is null");
		
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	@Override
	public void removeListener(ServerListener listener) {
		if (listener == null)
			throw new NullPointerException("listener is null");
		
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}
	
	private void startMatch(Match match) {
		assert match != null;
		
		synchronized (matchManagers) {
			if (!matchManagers.containsKey(match)) {
				MatchManager manager = new DefaultMatchManager(match);
				matchManagers.put(match, manager);
			}
		}
	}
}
