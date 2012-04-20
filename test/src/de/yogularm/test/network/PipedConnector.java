package de.yogularm.test.network;

import java.io.IOException;

import de.yogularm.network.InputOutputStreams;
import de.yogularm.network.client.ClientConnector;

public class PipedConnector implements ClientConnector {
	public final StreamPipe c2s;
	public final StreamPipe s2c;
	private int openCount = 0;
	
	public PipedConnector(boolean buffered) throws IOException {
		c2s = new StreamPipe(buffered);
		s2c = new StreamPipe(buffered);
	}
	
	@Override
	public InputOutputStreams openConnection() throws IOException {
		openCount++;
		return new InputOutputStreams(s2c.in(), c2s.out());
	}
	
	public int getOpenCount() {
		return openCount;
	}
}
