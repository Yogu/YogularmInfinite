package de.yogularm.server;

import java.util.HashMap;
import java.util.Map;

import de.yogularm.network.Match;

public class ServerData {
	public final Players players = new Players();
	public final Map<Integer, Match> matches = new HashMap<Integer, Match>();
	public final Map<String, ClientData> clientData = new HashMap<String, ClientData>();
}
