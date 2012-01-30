package de.yogularm.server.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import de.yogularm.server.Players;
import de.yogularm.server.ServerData;
import de.yogularm.utils.Exceptions;

public class GameServer {
	private boolean isRunning = false;
	private ServerSocket socket;
	private List<GameClient> clients = new ArrayList<GameClient>();
	private ServerData data = new ServerData();

	public void open(int port) throws IOException {
		if (socket != null)
			throw new IllegalStateException("Server already started");
		socket = new ServerSocket(port);
		isRunning = true;
		log("Server started on port " + port);
		beginAccept();
	}

	public void close() throws IOException {
		if (socket != null) {
			log("Closing server...");
			isRunning = false;
			socket.close();
			synchronized (clients) {
				for (GameClient client : clients) {
					client.interrupt();
				}
				clients.clear();
			}
			data = new ServerData();
			socket = null;
			log("Server closed");
		}
	}

	private void beginAccept() {
		new Thread(new Runnable() {
			public void run() {
				while (isRunning) {
					try {
						final Socket clientSocket = socket.accept();
						log("Accepted client: " + clientSocket.getInetAddress().toString());

						final GameClient client = new GameClient(clientSocket.getInputStream(),
							clientSocket.getOutputStream(), data);

						synchronized (clients) {
							clients.add(client);
						}

						client.setClosedHandler(new Runnable() {
							public void run() {
								try {
									clientSocket.close();
									synchronized (clients) {
										clients.remove(client);
									}
									log("Client socket closed");
								} catch (IOException e) {
									log("Unable to close client socket");
								}
							}
						});
						client.start();
					} catch (IOException e) {
						if (isRunning)
							log("Error accepting client: " + Exceptions.formatException(e));
					}
				}
			}
		}).start();
	}

	private void log(String str) {
		System.out.println(str);
	}
}
