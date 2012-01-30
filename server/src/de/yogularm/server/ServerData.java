package de.yogularm.server;

import java.util.HashMap;
import java.util.Map;

public class ServerData {
	public final Players players = new Players();
	public final Map<Integer, Match> matches = new HashMap<Integer, Match>();
}
