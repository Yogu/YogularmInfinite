package de.yogularm.server;

import java.io.IOException;

import de.yogularm.multiplayer.DefaultServerManager;
import de.yogularm.multiplayer.ServerManager;
import de.yogularm.network.NetworkGlobals;
import de.yogularm.network.server.DefaultServerHandlerFactory;
import de.yogularm.network.server.GameServer;
import de.yogularm.network.server.ServerHandlerFactory;

public class YogularmServerLauncher {
	public static void main(String[] arguments) {
		try {
			ServerManager manager = new DefaultServerManager();
			ServerHandlerFactory handlerFactory = new DefaultServerHandlerFactory();
			GameServer server = new GameServer(handlerFactory, manager);
	    server.open(NetworkGlobals.DEFAULT_PORT);
    } catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }
	}
}
