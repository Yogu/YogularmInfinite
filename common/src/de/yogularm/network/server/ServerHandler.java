package de.yogularm.network.server;

import java.io.IOException;

public interface ServerHandler {
	public void run() throws IOException;
	public void interrupt();
}
