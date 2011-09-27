package de.yogularm.building;

import de.yogularm.BuilderBase;
import de.yogularm.Vector;
import de.yogularm.components.Ladder;
import de.yogularm.components.Platform;

public class PlatformTest extends BuilderBase {	
	public void doBuild() {
		moveBuildingPosition(7, 0);
		Platform platform = new Platform(getWorld());
		platform.setTargets(new Vector[] { 
				Vector.getZero(), new Vector(-4, 0)
		});
		place(platform);
		platform.setOrigin();
	}
}
