package de.yogularm.building;

import de.yogularm.components.general.Chicken;
import de.yogularm.components.general.Stone;

public class JumpTest extends BuilderBase {	
	public void build() {
		int length = 40;
		int height = 10;
		
		for (int i = 0; i < height; i++)
			place(Stone.class, -1, i + 1);
		
		for (int i = 0; i < length; i++) {
			place(Stone.class, i - 1, 0);
			if (i % 5 == 0) {
				place(Stone.class, i - 1, 1); // obstacle
				place(Chicken.class, i, 1);
			}
		}
		
		for (int i = 0; i < height; i++)
			place(Stone.class, length - 2, i + 1);
		
		moveBuildingPosition(1000, 0);
	}
}
