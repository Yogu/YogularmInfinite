package de.yogularm.building;

import de.yogularm.BuilderBase;
import de.yogularm.components.Bricks;

public class Castle extends BuilderBase {
	public void doBuild() {
		int width = 16;
		int height = 64;
		int corridorHeight = 3;
		
		for (int x = 1; x < width + 1; x++) {
			for (int y = - height; y < height; y++) {
				if (y < 0 || y >= corridorHeight)
					place(Bricks.class, x, y + 1);
			}
		}
		
		moveBuildingPosition(width, 0);
	}
}
