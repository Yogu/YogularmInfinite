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
	  	//if (point.getX() > getPath().getCurrentWaypoint().getX())
	  		rightPositions.add(point);
	  }

		Random random = new Random();
		Point newWaypoint = null;
		while (reachablePositions.size() > 0) {
			if (rightPositions.size() == 0) {
				rightPositions = reachablePositions;
				System.out.println("No right position available");
			}
			
			getPath().push();
			
			newWaypoint =	rightPositions.get(random.nextInt(rightPositions.size()));
			getSite().place(new Stone(getComponents()), newWaypoint.add(0, -1));
			if (getPath().setWaypoint(newWaypoint)) {
				getPath().popAndApply();
				break;
			} else {
				reachablePositions.remove(newWaypoint);
				getPath().popAndDiscard();
			}
		}
		
		if (reachablePositions.size() == 0) {
			System.out.println("Stuck!");
			return;
		}
		
		System.out.println(newWaypoint);

	  for (Point point : getPath().getLastTrace()) {
	  	getSite().place(new Coin(getComponents()), point);
	  }
	  
	  //getSite().place(new Stone(getComponents()), new Point(1000, 999));
	  //getPath().setWaypoint(new Point(1000, 1000));
  }
}
