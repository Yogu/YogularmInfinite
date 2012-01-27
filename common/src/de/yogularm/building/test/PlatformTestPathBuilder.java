package de.yogularm.building.test;

import de.yogularm.building.BuildingPath;
import de.yogularm.building.PathBuilder;
import de.yogularm.components.general.Platform;
import de.yogularm.components.general.Stone;
import de.yogularm.geometry.Point;
import de.yogularm.geometry.Vector;

public class PlatformTestPathBuilder extends PathBuilder {
	public PlatformTestPathBuilder(BuildingPath path) {
		super(path);
	}

	private boolean finished;

	@Override
	public void build() {
		if (finished)
			return;
		
		build2();

		finished = true;
	}
	
	@SuppressWarnings("unused")
  private void build1() {
		Point target = new Point(8, 2);
		Point origin = new Point(2, 0);
		Platform platform = new Platform(getComponents());
		platform.setOrigin(origin.toVector());
		platform.setTargets(new Vector[] { Vector.ZERO, new Vector(4, 2) });
		getPath().place(platform, origin);
		
		getPath().place(new Stone(getComponents()), target.add(0, -1));
		getPath().setWaypointUsingPlatform(target, platform);
	}

	private void build2() {
		Point target = new Point(8, 10);
		Point origin = new Point(2, 0);
		Platform platform = new Platform(getComponents());
		platform.setOrigin(origin.toVector());
		platform.setTargets(new Vector[] { Vector.ZERO, new Vector(4, 2), new Vector(4, 9) });
		getPath().place(platform, origin);
		
		getPath().place(new Stone(getComponents()), target.add(0, -1));
		getPath().setWaypointUsingPlatform(target, platform);
	}
}
