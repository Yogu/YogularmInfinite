package de.yogularm.building;

import de.yogularm.components.general.Platform;
import de.yogularm.geometry.Vector;

public class PlatformTest extends BuilderBase {	
	public void build() {
		moveBuildingPosition(7, 0);
		Platform platform = new Platform(getComponents());
		platform.setTargets(new Vector[] { 
				new Vector(0, 10), new Vector(-4, 0)
		});
		platform.setPlatformSpeed(2);
		place(platform);
		platform.setOrigin();
		moveBuildingPosition(0, 10000);
	}
}
