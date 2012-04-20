package de.yogularm.network;

import java.io.IOException;

public interface BackgroundHandler {
	public void run() throws IOException;
	public void interrupt();
}
