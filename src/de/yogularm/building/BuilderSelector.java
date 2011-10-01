package de.yogularm.building;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.yogularm.Builder;

public class BuilderSelector {
	private Map<Builder, Float> builders = new HashMap<Builder, Float>();
	private float frequencySum;
	
	public void add(Builder builder, float frequency) {
		builders.put(builder, frequency);
		frequencySum += frequency;
	}
	
	/**
	 * Gets a random builder out of the set, taking their frequencies into account
	 * 
	 * @param random a random seed, between 0 and 1 (inclusive)
	 * @return a random builder
	 */
	public Builder get(float random) {
		random *= frequencySum;
		for (Entry<Builder, Float> entry : builders.entrySet()) {
			random -= entry.getValue();
			if (random <= 0)
				return entry.getKey();
		}
		return null;
	}
}
