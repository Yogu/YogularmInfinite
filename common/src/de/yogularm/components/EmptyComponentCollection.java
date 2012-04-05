package de.yogularm.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.yogularm.geometry.Point;
import de.yogularm.geometry.Rect;
import de.yogularm.geometry.Vector;

public class EmptyComponentCollection implements ObservableComponentCollection {
	@Override
	public Collection<Component> getComponentsAround(Rect range) {
		return new ArrayList<Component>();
	}

	@Override
	public Collection<Component> getComponentsAround(Vector position) {
		return new ArrayList<Component>();
	}

	@Override
	public boolean contains(Component component) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void add(Component component) {
		throw new UnsupportedOperationException("Called add to an EmptyComponentCollection");
	}

	@Override
	public void remove(Component component) {
		throw new UnsupportedOperationException("Called remove to an EmptyComponentCollection");
	}

	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public void addListener(ComponentCollectionListener listener) {
		throw new UnsupportedOperationException("Called addListener to an EmptyComponentCollection");
	}

	@Override
	public void removeListener(ComponentCollectionListener listener) {
		throw new UnsupportedOperationException("Called removeListener to an EmptyComponentCollection");
	}

	@Override
	public List<Component> getComponentsOfSector(Point sector) {
		return new ArrayList<Component>();
	}

	@Override
	public Component getByID(int id) {
		return null;
	}
}
