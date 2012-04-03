package de.yogularm.network;

public enum NetworkPacket {
	/* server */
	WORLD_INFO, COMPLETE_SECTOR, ADDED, REMOVED, CHANGED,
	
	/* client */
	OBSERVE
}
