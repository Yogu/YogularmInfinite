package de.yogularm.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;


/**
 * All methods of this class are thread-safe.
 * 
 * @author Yogu
 *
 * @param <K>
 * @param <V>
 */
public class ObservableMap<K, V extends Observable> extends Observable implements ListModel<V> {
	private List<ListDataListener> listeners1 = new ArrayList<ListDataListener>();
	private List<ListListener<V>> listeners2 = new ArrayList<ListListener<V>>();
	private final Object lock = new Object();
	private Map<K, V> map = new HashMap<K, V>();
	private List<V> list = new ArrayList<V>();
	private TheObserver observer = new TheObserver();

	@Override
	public void addListDataListener(ListDataListener l) {
		synchronized (listeners1) {
			listeners1.add(l);
		}
	}

	public void addListener(ListListener<V> l) {
		synchronized (listeners2) {
			listeners2.add(l);
		}
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		synchronized (listeners1) {
			listeners1.remove(l);
		}
	}

	public void removeListener(ListListener<V> l) {
		synchronized (listeners2) {
			listeners2.remove(l);
		}
	}

	@Override
	public V getElementAt(int index) {
		synchronized (lock) {
			return list.get(index);
		}
	}

	@Override
	public int getSize() {
		synchronized (lock) {
			return list.size();
		}
	}

	public V get(K key) {
		synchronized (lock) {
			return map.get(key);
		}
	}

	public Collection<V> getUnmodifiableCollection() {
		synchronized (lock) {
			return Collections.unmodifiableCollection(new ArrayList<V>(list));
		}
	}

	public boolean containsKey(K key) {
		synchronized (lock) {
			return map.containsKey(key);
		}
	}

	public boolean containsValue(V value) {
		synchronized (lock) {
			return list.contains(value);
		}
	}

	public void replaceAll(Map<K, V> newItems) {
		Map<K, V> oldMap;
		synchronized (lock) {
			for (V item : list) {
				item.deleteObserver(observer);
			}
			oldMap = map;
			list.clear();
			map = new HashMap<K, V>();
			list.addAll(newItems.values());
			map.putAll(newItems);
			for (V item : newItems.values()) {
				item.addObserver(observer);
			}
		}

		synchronized (listeners1) {
			for (ListDataListener listener : listeners1) {
				if (oldMap.size() > 0)
					listener.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, 0,
							oldMap.size() - 1));
				if (newItems.size() > 0)
					listener.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, 0, newItems
							.size() - 1));
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
		synchronized (lock) {
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
		synchronized (lock) {
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
				listener.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index,
						index));
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
			V item = (V) observable;
			int index;
			synchronized (lock) {
				index = list.indexOf(observable);
			}
			if (index >= 0) {
				synchronized (listeners1) {
					for (ListDataListener listener : listeners1) {
						listener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index,
								index));
					}
				}

				synchronized (listeners2) {
					for (ListListener<V> listener : listeners2) {
						listener.itemChanged(item, arg);
					}
				}

				setChanged();
				notifyObservers();
			}
		}
	}
}
