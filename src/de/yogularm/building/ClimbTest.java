package de.yogularm.building;

import de.yogularm.BuilderBase;
import de.yogularm.components.Ladder;

public class ClimbTest extends BuilderBase {	
	public void build() {
		int length = 40;
		//int height = 10;
		
		for (int i = 0; i < length; i++) {
			if (i % 5 == 0) {
				for (int j = 0; j < 4; j++)
					place(Ladder.class, i, j + 1);
			}
		}
		
		moveBuildingPosition(1000, 0);
	}
}
