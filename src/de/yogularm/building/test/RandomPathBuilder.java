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
		List<Point> goodPositions = new ArrayList<Point>();

	  for (Point point : reachablePositions) {
	  	//if (point.getX() > getPath().getCurrentWaypoint().getX())
	  	if ((Math.random() > 0.75) || (point.getY() > getPath().getCurrentWaypoint().getY()))
	  		goodPositions.add(point);
	  }

		Random random = new Random();
		Point newWaypoint = null;
		while (reachablePositions.size() > 0) {
			if (goodPositions.size() == 0) {
				goodPositions = reachablePositions;
				System.out.println("No right position available");
			}
			
			getPath().push();
			
			newWaypoint =	goodPositions.get(random.nextInt(goodPositions.size()));
			System.out.printf("Trying: %s -> %s\n", getPath().getCurrentWaypoint(), newWaypoint);
			getSite().place(new Stone(getComponents()), newWaypoint.add(0, -1));
			if (getPath().setWaypoint(newWaypoint)) {
				getPath().popAndApply();
				break;
			} else {
				reachablePositions.remove(newWaypoint);
				goodPositions.remove(newWaypoint);
				getPath().popAndDiscard();
				System.out.println("Discarded: " + newWaypoint);
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
