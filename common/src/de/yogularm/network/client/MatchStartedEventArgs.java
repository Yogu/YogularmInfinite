package de.yogularm.network.client;

import de.yogularm.components.World;

public class MatchStartedEventArgs {
	private World world;
	
	public MatchStartedEventArgs(World world) {
		this.world = world;
	}
	
	public World getWorld()  {
		return world;
	}
}
