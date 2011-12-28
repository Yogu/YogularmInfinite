package de.yogularm.building.old;

import de.yogularm.building.Builder2;
import de.yogularm.building.BuildingSite;
import de.yogularm.geometry.Rect;
import de.yogularm.geometry.Vector;

public class BuilderConverter implements Builder2 {
	private Builder builder;
	
	public BuilderConverter(Builder builder) {
		this.builder = builder;
	}

	@Override
  public void init(BuildingSite buildingSite) {
	  builder.init(buildingSite.getComponents(), Vector.getZero());
  }

	@Override
  public void build(Rect bounds) {
  	while (bounds.contains(builder.getBuildingPosition())) {
  		builder.build();
  	}
  }
}
