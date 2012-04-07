package de.yogularm.test.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.yogularm.components.Component;
import de.yogularm.components.ComponentCollection;
import de.yogularm.geometry.Rect;
import de.yogularm.geometry.Vector;

public class MockComponentCollection implements ComponentCollection {
	private List<Component> components = new ArrayList<Component>();
	
	@Override
  public Collection<Component> getComponentsAround(Rect range) {
	  return components;
  }

	@Override
  public Collection<Component> getComponentsAround(Vector position) {
	  return components;
  }

	@Override
  public boolean contains(Component component) {
		return components.contains(component);
  }

	@Override
  public void add(Component component) {
	  components.add(component);
  }

	@Override
  public void remove(Component component) {
	  components.remove(component);
  }

	@Override
  public int getCount() {
	  return components.size();
  }
}