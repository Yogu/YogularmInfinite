package de.yogularm.building;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public abstract class CompositeBuilder extends BuilderBase {
	private Map<Builder, Float> builders = new HashMap<Builder, Float>();
	private float frequencySum;
	
	public void addBuilder(Builder builder, float frequency) {
		builders.put(builder, frequency);
		frequencySum += frequency;
	}
	
	/**
	 * Gets a random builder out of the set, taking their frequencies into account
	 * 
	 * @param random a random seed, between 0 and 1 (inclusive)
	 * @return a random builder
	 */
	public Builder getBuilder(float random) {
		random *= frequencySum;
		for (Entry<Builder, Float> entry : builders.entrySet()) {
			random -= entry.getValue();
			if (random <= 0) {
				Builder builder = entry.getKey();
				builder.init(getComponents(), getBuildingPosition());
				return builder;
			}
		}
		return null;
	}
	
	public void subBuild(Builder subBuilder) {
		subBuilder.init(getComponents(), getBuildingPosition());
		subBuilder.build();
		setBuildingPosition(subBuilder.getBuildingPosition());
	}
}
