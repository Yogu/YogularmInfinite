package de.yogularm.building.test;

import de.yogularm.building.BuildingPath;
import de.yogularm.building.BuildingSite;
import de.yogularm.building.GuidedBuilder;
import de.yogularm.building.PathBuilder;
import de.yogularm.components.general.Stone;
import de.yogularm.geometry.Point;

public class TestBuilder extends GuidedBuilder {
	@Override
  protected PathBuilder getFirstPathBuilder(BuildingSite site) {
		return new RandomPathBuilder(new BuildingPath(site, Point.getZero()));
		//return new TestPathBuilder(new BuildingPath(site, Point.getZero()));
  }

	@Override
  protected void makeOriginSafe() {
		getBuildingSite().place(new Stone(getComponents()), new Point(0, -1));
  }
}
