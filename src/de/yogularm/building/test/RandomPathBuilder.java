package de.yogularm.building.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.yogularm.building.BuildingPath;
import de.yogularm.building.PathBuilder;
import de.yogularm.components.general.Coin;
import de.yogularm.components.general.Stone;
import de.yogularm.geometry.Point;

public class RandomPathBuilder extends PathBuilder {
	public RandomPathBuilder(BuildingPath path) {
	  super(path);
  }

	@Override
  public void build() {
		List<Point> reachablePositions = getPath().getReachablePositions();
		List<Point> rightPositions = new ArrayList<Point>();
		
	  for (Point point : reachablePositions) {
	  	getSite().place(new Coin(getComponents()), point);
	  	if (point.getX() > getPath().getCurrentWaypoint().getX())
	  		rightPositions.add(point);
	  }
	  
	  //if (rightPositions.size() == 0)
	  	rightPositions = reachablePositions;

		Random random = new Random();
		Point newWaypoint =	rightPositions.get(random.nextInt(rightPositions.size()));
		getSite().place(new Stone(getComponents()), newWaypoint.add(0, -1));
		getPath().setWaypoint(newWaypoint);
		System.out.println(newWaypoint);
	  
	  //getSite().place(new Stone(getComponents()), new Point(1000, 999));
	  //getPath().setWaypoint(new Point(1000, 1000));
  }
}
