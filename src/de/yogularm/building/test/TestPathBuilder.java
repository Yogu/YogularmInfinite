package de.yogularm.building.test;

import de.yogularm.building.BuildingPath;
import de.yogularm.building.PathBuilder;
import de.yogularm.components.general.Coin;
import de.yogularm.components.general.Stone;
import de.yogularm.geometry.Point;

public class TestPathBuilder extends PathBuilder {
	public TestPathBuilder(BuildingPath path) {
	  super(path);
  }

	@Override
  public void build() {
	  for (Point point : getPath().getReachablePositions())
	  	getSite().place(new Coin(getComponents()), point);
	  getSite().place(new Stone(getComponents()), new Point(1000, 999));
	  getPath().setWaypoint(new Point(1000, 1000));
  }
}
