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
	
	private boolean finished;

	@Override
  public void build() {
		if (finished)
			return;
		
		Point offset = new Point(3, 1);
		
		Point current = getPath().getCurrentWaypoint();
		Point target = current.add(offset);
		getPath().place(new Stone(getComponents()), target.add(0, -1));
		getPath().setWaypoint(target);

		List<Point> trace = getPath().getLastTrace();
		if (trace != null) {
		  for (Point point : trace) {
		  	getPath().place(new Coin(getComponents()), point);
		  }
			/*for (int x = -5; x < 5; x++)
				for (int y = -5; y < 5;y++) {
			  	getPath().place(new Heart(getComponents()), current.add(x, y));
				}*/
		}
		
		finished = true;
  }
}
