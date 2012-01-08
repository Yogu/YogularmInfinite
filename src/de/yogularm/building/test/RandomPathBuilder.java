package de.yogularm.building.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.yogularm.Config;
import de.yogularm.building.BuildingPath;
import de.yogularm.building.PathBuilder;
import de.yogularm.components.general.Coin;
import de.yogularm.components.general.Stone;
import de.yogularm.geometry.Point;

public class RandomPathBuilder extends PathBuilder {
	private boolean stuck = false;
	
	public RandomPathBuilder(BuildingPath path) {
	  super(path);
  }

	@Override
  public void build() {
		List<Point> reachablePositions = getPath().getReachablePositions();
		List<Point> goodPositions = new ArrayList<Point>();

	  for (Point point : reachablePositions) {
	  	if ((Math.random() > 0.8) || (point.getX() > getPath().getCurrentWaypoint().getX()))
		  	if ((Math.random() > 0.8) || (point.getY() > getPath().getCurrentWaypoint().getY()))
		  		goodPositions.add(point);
	  }

		Random random = new Random();
		Point newWaypoint = null;
		while (reachablePositions.size() > 0) {
			if (goodPositions.size() == 0) {
				goodPositions = reachablePositions;
				System.out.println("  No right position available");
			}
			
			getPath().push();
			
			newWaypoint =	goodPositions.get(random.nextInt(goodPositions.size()));
			System.out.printf("  Trying: %s -> %s\n", getPath().getCurrentWaypoint(), newWaypoint);
			getSite().place(new Stone(getComponents()), newWaypoint.add(0, -1));
			if (getPath().setWaypoint(newWaypoint)) {
				getPath().popAndApply();
				break;
			} else {
				reachablePositions.remove(newWaypoint);
				goodPositions.remove(newWaypoint);
				getPath().popAndDiscard();
			}
		}
		
		if (reachablePositions.size() == 0) {
			if (!stuck)
				System.out.println("Stuck!");
			stuck = true;
			return;
		}
		
		System.out.printf("OK: %s\n", newWaypoint);

	  for (Point point : getPath().getLastTrace()) {
	  	if (Config.DEBUG_BUILDING || Math.random() < 0.15)
	  		getPath().place(new Coin(getComponents()), point);
	  }
	  
	  //getSite().place(new Stone(getComponents()), new Point(1000, 999));
	  //getPath().setWaypoint(new Point(1000, 1000));
  }
}
