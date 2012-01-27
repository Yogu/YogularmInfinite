package de.yogularm.event;

import java.util.ArrayList;
import java.util.List;

public class Event<T> {
	private List<EventListener<T>> listeners;
	private Object owner;
	
	public Event(Object owner) {
		this.owner = owner;
	}
	
	public void addListener(EventListener<T> listener) {
		if (listener == null)
			return;
		
		if (listeners == null)
			listeners = new ArrayList<EventListener<T>>();
		listeners.add(listener);
	}
	
	public void removeListener(EventListener<T> listener) {
		if (listeners != null)
			listeners.remove(listener);
	}
	
	public void call(T param) {
		if (listeners != null) {
			// Copy to local array to allow changes to the listeners list during call
			List<EventListener<T>> list = new ArrayList<EventListener<T>>(listeners);
			for (EventListener<T> listener : list) 
				listener.call(owner, param);
		}
	}
}
