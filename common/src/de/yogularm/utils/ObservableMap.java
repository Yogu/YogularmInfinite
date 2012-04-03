package de.yogularm.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class ObservableMap<K, V extends Observable> extends Observable implements ListModel<V> {
	private List<ListDataListener> listeners1 = new ArrayList<ListDataListener>();
	private List<ListListener<V>> listeners2 = new ArrayList<ListListener<V>>();
	private Map<K, V> map = new HashMap<K, V>();
	private List<V> list = new ArrayList<V>();
	private TheObserver observer = new TheObserver();

	@Override
	public void addListDataListener(ListDataListener l) {
		listeners1.add(l);
	}
	
	public void addListener(ListListener<V> l) {
		listeners2.add(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listeners1.remove(l);
	}
	
	public void removeListener(ListListener<V> l) {
		listeners2.remove(l);
	}

	@Override
	public V getElementAt(int index) {
		return list.get(index);
	}

	@Override
	public int getSize() {
		return list.size();
	}
	
	public V get(K key) {
		return map.get(key);
	}
	
	public Map<K, V> getMap() {
		return map;
	}
	
	public void replaceAll(Map<K, V> newItems) {
		Map<K, V> oldMap;
		synchronized (list) {
			for (V item : list) {
				item.deleteObserver(observer);
			}
			oldMap = map;
			list.clear();
			map = new HashMap<K, V>();
			list.addAll(newItems.values());
			map.putAll(newItems);
			for (V item: newItems.values()) {
				item.addObserver(observer);
			}
		}
		
		synchronized (listeners1) {
			for (ListDataListener listener : listeners1) {
				if (oldMap.size() > 0)
					listener.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, 0, oldMap.size() - 1));
				if (newItems.size() > 0)
					listener.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, 0, newItems.size() - 1));
			}
		}
		
		synchronized (listeners2) {
			for (ListListener<V> listener : listeners2) {
				for (V item : oldMap.values()) {
					listener.itemRemoved(item);
				}
				for (V item : map.values()) {
					listener.itemAdded(item);
				}
			}
		}
		
		setChanged();
		notifyObservers();
	}

	public void add(K key, V item) {
		int index;
		synchronized (list) {
			list.add(item);
			map.put(key, item);
			index = list.size() - 1;
		}
		
		item.addObserver(observer);
		
		synchronized (listeners1) {
			for (ListDataListener listener : listeners1) {
				listener.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index));
			}
		}
		
		synchronized (listeners2) {
			for (ListListener<V> listener : listeners2) {
				listener.itemAdded(item);
			}
		}
		
		setChanged();
		notifyObservers();
	}

	public void remove(K key) {
		int index;
		V item;
		synchronized (list) {
			item = map.get(key);
			if (item == null)
				return;
			else {
				index = list.indexOf(item);
				list.remove(index);
				map.remove(key);
			}
		}
		
		item.deleteObserver(observer);
		
		synchronized (listeners1) {
			for (ListDataListener listener : listeners1) {
				listener.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index));
			}
		}
		
		synchronized (listeners2) {
			for (ListListener<V> listener : listeners2) {
				listener.itemRemoved(item);
			}
		}
		
		setChanged();
		notifyObservers();
	}
	
	private class TheObserver implements Observer {
		@Override
		public void update(Observable observable, Object arg) {
			@SuppressWarnings("unchecked")
			V item = (V)observable;
			int index = list.indexOf(observable);
			if (index >= 0) {
				synchronized (listeners1) {
					for (ListDataListener listener : listeners1) {
						listener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index, index));
					}
				}
				
				synchronized (listeners2) {
					for (ListListener<V> listener : listeners2) {
						listener.itemChanged(item);
					}
				}
				
				setChanged();
				notifyObservers();
			}
		}
	}
}
