package de.yogularm.server;

import java.io.IOException;

import de.yogularm.network.ServerGlobals;
import de.yogularm.network.server.GameServer;

public class YogularmServerLauncher {
	public static void main(String[] arguments) {
		try {
	    new GameServer().open(ServerGlobals.DEFAULT_PORT);
    } catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }
	}
}
