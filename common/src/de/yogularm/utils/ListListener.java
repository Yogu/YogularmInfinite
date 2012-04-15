package de.yogularm.utils;

import java.util.Observable;


public interface ListListener<T extends Observable> {
	void itemAdded(T item);
	void itemRemoved(T item);
	void itemChanged(T item, Object arg);
}
