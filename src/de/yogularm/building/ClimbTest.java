package de.yogularm.building;

import de.yogularm.BuilderBase;
import de.yogularm.components.Ladder;
import de.yogularm.components.Stone;

public class ClimbTest extends BuilderBase {	
	public void doBuild() {
		int length = 40;
		int height = 10;
		
		for (int i = 0; i < height; i++)
			place(Stone.class, -1, i + 1);
		
		for (int i = 0; i < length; i++) {
			place(Stone.class, i - 1, 0);
			if (i % 5 == 0) {
				for (int j = 0; j < 4; j++)
					place(Ladder.class, i - 1, j + 1);
			}
		}
		
		for (int i = 0; i < height; i++)
			place(Stone.class, length - 2, i + 1);
		
		moveBuildingPosition(1000, 0);
	}
}
