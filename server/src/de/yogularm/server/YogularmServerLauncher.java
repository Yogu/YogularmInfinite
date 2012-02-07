package de.yogularm.server;

import java.io.IOException;

import de.yogularm.server.meta.GameServer;

public class YogularmServerLauncher {
	public static void main(String[] arguments) {
		try {
	    new GameServer().open(1234);
    } catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }
	}
}
