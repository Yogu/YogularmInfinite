package de.yogularm.event;

public interface EventListener<T> {
	void call(Object sender, T param);
}
