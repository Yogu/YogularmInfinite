package de.yogularm.building.test;

import java.util.List;

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
		getPath().place(new Stone(getComponents()), new Point(0, 2));
		getPath().place(new Stone(getComponents()), new Point(-3, -11));
		//getPath().place(new Stone(getComponents()), new Point(-2, 0));

		//List<Point> reachablePositions = getPath().getReachablePositions();
		Point c = getPath().getCurrentWaypoint();
		List<Point> trace = getPath().getTrace(c, c.add(-3, -1));
		
		if (trace != null)
		  for (Point point : trace) {
		  	getPath().place(new Coin(getComponents()), point);
		  }
	  
	  getSite().place(new Stone(getComponents()), new Point(1000, 999));
	  getPath().setWaypoint(new Point(1000, 1000));
  }
}
