package de.yogularm;

import de.yogularm.building.Builder2;
import de.yogularm.building.BuildingSite;
import de.yogularm.building.test.TestBuilder;
import de.yogularm.components.ComponentTree;
import de.yogularm.components.ObservableComponentCollection;
import de.yogularm.geometry.Rect;

public class MultiPlayerWorld {
	private ObservableComponentCollection components;
	private BuildingSite buildingSite;
	private Builder2 builder;
	
	public static final int SECTOR_WIDTH = 48;
	public static final int SECTOR_HEIGHT = 48;
	
	public MultiPlayerWorld() {
		components = new ComponentTree(SECTOR_WIDTH, SECTOR_HEIGHT);
		buildingSite = new BuildingSite(components);
		builder = new TestBuilder();
		builder.init(buildingSite);
		// TODO: only for testing
		builder.build(new Rect(0, 0, SECTOR_WIDTH, SECTOR_HEIGHT));
	}
	
	public ObservableComponentCollection getComponents() {
		return components;
	}
}
