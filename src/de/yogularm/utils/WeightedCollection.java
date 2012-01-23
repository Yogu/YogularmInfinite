package de.yogularm.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class WeightedCollection<T> implements Collection<T> {
	private Map<T, Float> map = new HashMap<T, Float>();
	private float weightSum;
	
	public boolean add(T item, float frequency) {
		if (map.containsKey(item))
			return false;
		map.put(item, frequency);
		weightSum += frequency;
		return true;
	}
	
	/**
	 * Gets a random builder out of the set, taking their frequencies into account
	 * 
	 * @param random a random seed, between 0 and 1 (inclusive)
	 * @return a random builder
	 */
	public T getRandom(float random) {
		random *= weightSum;
		for (Entry<T, Float> entry : map.entrySet()) {
			random -= entry.getValue();
			if (random <= 0) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	public T getRandom() {
		return getRandom((float)Math.random());
	}
	
	public boolean remove(Object item) {
		Float weight = map.get(item);
		if (weight != null) {
			weightSum -= weight;
			map.remove(item);
			return true;
		} else
			return false;
	}

	@Override
  public int size() {
	  return map.size();
  }

	@Override
  public boolean isEmpty() {
	  return map.isEmpty();
  }

	@Override
  public boolean contains(Object o) {
	  return map.containsKey(o);
  }

	@Override
  public Iterator<T> iterator() {
	  return map.keySet().iterator();
  }

	@Override
  public Object[] toArray() {
	  return map.keySet().toArray();
  }

	@Override
  public <T> T[] toArray(T[] a) {
	  return map.keySet().toArray(a);
  }

	@Override
  public boolean add(T e) {
	  return add(e, 1);
  }

	@Override
  public boolean containsAll(Collection<?> c) {
	  return map.keySet().containsAll(c);
  }

	@Override
  public boolean addAll(Collection<? extends T> c) {
		boolean changed = false;
	  for (T e : c) {
	  	if (add(e))
	  		changed = true;
	  }
	  return changed;
  }

	@Override
  public boolean removeAll(Collection<?> c) {
		boolean changed = false;
	  for (Object e : c) {
	  	if (remove(e))
	  		changed = true;
	  }
	  return changed;	  	
  }

	@Override
  public boolean retainAll(Collection<?> c) {
		boolean changed = false;
	  for (T e : map.keySet()) {
	  	if (!c.contains(e)) {
	  		if (remove(e))
	  			changed = true;
	  	}
	  }
	  return changed;
  }

	@Override
  public void clear() {
	  map.clear();
	  weightSum = 0;
  } 
}
