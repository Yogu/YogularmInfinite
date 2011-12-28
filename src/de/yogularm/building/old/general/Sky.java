package de.yogularm.building.old.general;

import java.util.Random;

import de.yogularm.building.old.Builder;
import de.yogularm.building.old.CompositeBuilder;


public class Sky extends CompositeBuilder {
	public Sky() {
		addBuilder(new Flatland(), 1);
		addBuilder(new Stairs(), 1);
		addBuilder(new Gaps(), 2);
	}
	
	public void build() {
		Random random = new Random();
		Builder builder = getBuilder(random.nextFloat());
		builder.build();
	}
}
