package de.yogularm.multiplayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import de.yogularm.components.Component;
import de.yogularm.components.ComponentCollectionListener;
import de.yogularm.components.ObservableComponentCollection;
import de.yogularm.geometry.Point;
import de.yogularm.geometry.Vector;
import de.yogularm.multiplayer.Match.ChangeAction;

public class DefaultMatchManager implements MatchManager {
	private Match match;
	private MultiPlayerWorld world;
	private Map<Player, de.yogularm.components.Player> playerComponents = new HashMap<Player, de.yogularm.components.Player>();
	private Collection<MatchListener> listeners = new ArrayList<MatchListener>();

	public DefaultMatchManager(Match match) {
		if (match == null)
			throw new NullPointerException("match is null");

		this.match = match;
		world = new MultiPlayerWorld();
		initListeners();
	}

	@Override
	public void setPlayerPosition(Player player, Vector newPosition, Vector newMomentum) {
		de.yogularm.components.Player playerComponent = playerComponents.get(player);
		if (playerComponent != null) {
			playerComponent.pushTo(newPosition, newMomentum);
		}
	}

	@Override
	public Collection<Component> getComponentsOfSector(Point sector) {
		if (sector == null)
			throw new NullPointerException("sector is null");
		
		return world.getComponents().getComponentsOfSector(sector);
	}

	@Override
	public de.yogularm.components.Player getPlayerComponent(Player player) {
		if (player == null)
			throw new NullPointerException("player is null");
		
		synchronized (playerComponents) {
			return playerComponents.get(player);
		}
	}

	@Override
	public void observeSectors(Collection<Point> sectors) {
		world.observeSectors(sectors);
	}

	@Override
	public void stopObservation(Collection<Point> sectors) {
		world.stopObservation(sectors);
	}
	
	@Override
	public void addListener(MatchListener listener) {
		if (listener == null)
			throw new NullPointerException("listener is null");
		
		synchronized (listener) {
			listeners.add(listener);
		}
	}

	@Override
	public void removeListener(MatchListener listener) {
		if (listener == null)
			throw new NullPointerException("listener is null");
		
		synchronized (listener) {
			listeners.remove(listener);
		}
	}
	
	private void initListeners() {
		world.addListener(new MultiPlayerWorldListener() {
			@Override
			public void quickChange(MultiPlayerWorld world, Component component, Point sector) {
				Collection<MatchListener> ls;
				synchronized (listeners) {
					ls = new ArrayList<MatchListener>(listeners);
				}
				for (MatchListener listener : ls) {
					listener.quickChange(component, sector);
				}
			}
			
			@Override
			public void componentChanged(MultiPlayerWorld world, Component component, Point sector) {
				Collection<MatchListener> ls;
				synchronized (listeners) {
					ls = new ArrayList<MatchListener>(listeners);
				}
				for (MatchListener listener : ls) {
					listener.componentChanged(component, sector);
				}
			}
		});
		
		world.getComponents().addListener(new ComponentCollectionListener() {
			@Override
			public void componentRemoved(ObservableComponentCollection collection, Component component,
					Point sector) {
				Collection<MatchListener> ls;
				synchronized (listeners) {
					ls = new ArrayList<MatchListener>(listeners);
				}
				for (MatchListener listener : ls) {
					listener.componentRemoved(component, sector);
				}
			}
			
			@Override
			public void componentMoved(ObservableComponentCollection collection, Component component,
					Point lastSector, Point newSector, boolean sectorHasChanged) {
				if (sectorHasChanged) {
					Collection<MatchListener> ls;
					synchronized (listeners) {
						ls = new ArrayList<MatchListener>(listeners);
					}
					for (MatchListener listener : ls) {
						listener.componentChangedSector(component, lastSector, newSector);
					}
				}
			}
			
			@Override
			public void componentAdded(ObservableComponentCollection collection, Component component,
					Point sector) {
				Collection<MatchListener> ls;
				synchronized (listeners) {
					ls = new ArrayList<MatchListener>(listeners);
				}
				for (MatchListener listener : ls) {
					listener.componentAdded(component, sector);
				}
			}
		});
		
		match.addObserver(new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				if (arg instanceof Match.ChangeEvent) {
					Match.ChangeEvent event = (Match.ChangeEvent)arg;
					if (event.getAction() == ChangeAction.STATE_CHANGED) {
						Collection<MatchListener> ls;
						synchronized (listeners) {
							ls = new ArrayList<MatchListener>(listeners);
						}
						for (MatchListener listener : ls) {
							listener.stateChanged(event.getOldState(), event.getNewState());
						}
					}
				}
			}
		});
	}
}
