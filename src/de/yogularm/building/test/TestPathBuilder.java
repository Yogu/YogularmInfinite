package de.yogularm.building.test;

import java.util.List;

import de.yogularm.Config;
import de.yogularm.building.BuildingPath;
import de.yogularm.building.PathBuilder;
import de.yogularm.components.general.Coin;
import de.yogularm.components.general.Stone;
import de.yogularm.geometry.Point;

public class TestPathBuilder extends PathBuilder {
	public TestPathBuilder(BuildingPath path) {
	  super(path);
		Config.DEBUG_BUILDING = true;
  }

	@Override
  public void build() {
		Point offset = new Point(-4, 1);
		Point current = getPath().getCurrentWaypoint();
		Point target = current.add(offset);
		getPath().place(new Stone(getComponents()), target.add(0, -1));
		getPath().setWaypoint(target);

		List<Point> trace = getPath().getLastTrace();
		if (trace != null)
		  for (Point point : trace) {
		  	getPath().place(new Coin(getComponents()), point);
		  }
		Config.DEBUG_BUILDING = false;
	  
	  /*getSite().place(new Stone(getComponents()), new Point(1000, 999));
	  getPath().setWaypoint(new Point(1000, 1000));*/
  }
}
