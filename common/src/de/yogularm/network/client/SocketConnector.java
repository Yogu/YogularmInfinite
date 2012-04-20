package de.yogularm.network.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import de.yogularm.network.InputOutputStreams;

public class SocketConnector implements ClientConnector {
	private InetAddress address;
	private int port;
	

	public SocketConnector(InetAddress serverAddress, int port) {
		this.address = serverAddress;
		this.port = port;
	}
	
	@Override
	public InputOutputStreams openConnection() throws IOException {
		Socket socket = new Socket(address, port);
		return new InputOutputStreams(socket.getInputStream(), socket.getOutputStream());
	}
}
