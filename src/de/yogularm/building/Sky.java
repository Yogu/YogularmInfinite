package de.yogularm.building;

import java.util.Random;

import de.yogularm.Builder;

public class Sky extends CompositeBuilder {
	public Sky() {
		addBuilder(new Flatland(), 1);
		addBuilder(new Stairs(), 1);
		addBuilder(new Gaps(), 2);
	}
	
	public void doBuild() {
		Random random = getRandom(0x573C75B5);
		Builder builder = getBuilder(random.nextFloat());
		builder.build();
	}
}
