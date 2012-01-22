package de.yogularm.building;

import de.yogularm.components.general.Platform;
import de.yogularm.geometry.Point;

class MovingDetails {
	public final Point source;
	public final Point target;
	public final Platform platform; // if used
	
	public MovingDetails(Point source, Point target, Platform platform) {
		this.source = source;
		this.target = target;
		this.platform = platform;
	}
}