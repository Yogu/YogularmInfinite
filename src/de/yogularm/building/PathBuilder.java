package de.yogularm.building;

import de.yogularm.components.ComponentCollection;

public abstract class PathBuilder {
	private BuildingPath path;
	
	public PathBuilder(BuildingPath path) {
		this.path = path;
	}
	
	public BuildingPath getPath() {
		return path;
	}
	
	public BuildingSite getSite() {
		return path.getBuildingSite();
	}

	public ComponentCollection getComponents() {
		return getSite().getComponents();
	}
	
	public abstract void build();
}
