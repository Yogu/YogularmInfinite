package de.yogularm.building.old.general;

import de.yogularm.building.old.BuilderBase;
import de.yogularm.components.general.Bricks;

public class Castle extends BuilderBase {
	public void build() {
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
