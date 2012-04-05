package de.yogularm.network;

public enum NetworkPacket {
	/* server */
	INIT_WORLD, COMPLETE_SECTOR, ADDED, REMOVED, CHANGED, QUICK_CHANGE,
	
	/* client */
	OBSERVE
}
