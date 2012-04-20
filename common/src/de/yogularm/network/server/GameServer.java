package de.yogularm.network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import de.yogularm.multiplayer.ServerManager;
import de.yogularm.network.BackgroundHandler;
import de.yogularm.utils.Exceptions;

public class GameServer {
	private boolean isRunning = false;
	private ServerSocket socket;
	private final List<BackgroundHandler> clients = new ArrayList<BackgroundHandler>();
	private final ServerManager manager;
	private ServerContext context;
	private final ServerHandlerFactory handlerFactory;

	public GameServer(ServerHandlerFactory handlerFactory, ServerManager manager) {
		this.handlerFactory = handlerFactory;
		this.manager = manager;
		context = new ServerContext(manager);
	}

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
				for (BackgroundHandler handler : clients) {
					handler.interrupt();
				}
				clients.clear();
			}
			context = new ServerContext(manager);
			socket = null;
			log("Server closed");
		}
	}

	private void beginAccept() {
		new Thread(new Runnable() {
			public void run() {
				while (isRunning) {
					try {
						startHandler(socket.accept());
					} catch (IOException e) {
						if (isRunning)
							log("Error accepting client: " + Exceptions.formatException(e));
						return;
					}
				}
			}
		}).start();
	}

	private void startHandler(final Socket clientSocket) throws IOException {
		final String address = clientSocket.getInetAddress().toString();
		log("Accepted client: " + address);

		final BackgroundHandler handler = handlerFactory.createStartHandler(clientSocket.getInputStream(),
				clientSocket.getOutputStream(), context);

		synchronized (clients) {
			clients.add(handler);
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					handler.run();
				} catch (IOException e) {
					log("IO exception in client at " + address + ": " + Exceptions.formatException(e));
				} finally {
					try {
						clientSocket.close();
						synchronized (clients) {
							clients.remove(handler);
						}
						log("Client socket closed: " + address);
					} catch (IOException e) {
						log("Unable to close client socket " + address);
					}
				}
			}
		}).start();
	}

	private void log(String str) {
		System.out.println(str);
	}
}
