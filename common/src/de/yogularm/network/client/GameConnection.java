package de.yogularm.network.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import de.yogularm.event.Event;
import de.yogularm.event.EventArgs;
import de.yogularm.event.ExceptionEventArgs;
import de.yogularm.network.CommunicationError;
import de.yogularm.network.CommunicationException;
import de.yogularm.network.Match;
import de.yogularm.network.NetworkCommand;
import de.yogularm.network.NetworkInformation;
import de.yogularm.network.Player;

public class GameConnection {
	private Socket activeSocket;
	private BufferedReader in;
	private PrintStream out;
	private String sessionKey;

	private String host;
	private int port;
	private Player player;
	private Map<Integer, Match> matches = new HashMap<Integer, Match>();
	private Map<String, Player> players = new HashMap<String, Player>();

	private ConnectionState state = ConnectionState.IDLE;

	private interface NetworkAction {
		public void run(BufferedReader in, PrintStream out) throws IOException;
	}

	private Queue<NetworkAction> networkActions = new LinkedList<NetworkAction>();

	public GameConnection(String host, int port, String playerName) {
		if (!Player.isValidName(playerName))
			throw new IllegalArgumentException("Invalid player name: " + playerName);

		this.host = host;
		this.port = port;
		this.player = new Player(playerName);
	}

	/**
	 * An event that is called when there is a critical network error
	 */
	public final Event<ExceptionEventArgs> onNetworkError = new Event<ExceptionEventArgs>(this);

	/**
	 * An event that is called when there is a critical network error
	 */
	public final Event<EventArgs> onStateChanged = new Event<EventArgs>(this);

	public void start() {
		if (state != ConnectionState.IDLE)
			throw new IllegalStateException("Start can only be called in IDLE state");

		initThread();
	}

	public void close() {
		setState(ConnectionState.CLOSING);
	}

	public ConnectionState getState() {
		return state;
	}

	public Player getPlayer() {
		return player;
	}

	public String getHost() {
		return host;
	}

	public void sendMessage(final String message) {
		networkActions.add(new NetworkAction() {
			public void run(BufferedReader in, PrintStream out) throws IOException {
				sendCommand(NetworkCommand.SAY, message);
			}
		});
	}

	private void initThread() {
		state = ConnectionState.IDLE;
		new Thread(new Runnable() {
			public void run() {
				try {
					try {
						initSocket();
						if (!login()) {
							setState(ConnectionState.NAME_NOT_AVAILABLE);
							return;
						}

						networkActions.clear();
						setState(ConnectionState.CONNECTED);
						System.out.println("connected, session: " + sessionKey);
						initPassiveMode();
						receiveMatchList();

						while (state == ConnectionState.CONNECTED) {
							Thread.sleep(20); // in this stream, there are no time-critical
																// issues
							NetworkAction action;
							synchronized (networkActions) {
								action = networkActions.poll();
							}
							if (action != null) {
								action.run(in, out);
							}
						}
					} catch (IOException e) {
						setState(ConnectionState.NETWORK_ERROR);
						onNetworkError.call(new ExceptionEventArgs(e));
					} catch (InterruptedException e) {
						// be interrupted...
					}
				} finally {
					if (activeSocket != null) {
						try {
							activeSocket.close();
						} catch (IOException e) {
							System.out.println("Failed to close active socket");
							e.printStackTrace();
						}
						activeSocket = null;
					}
					setState(ConnectionState.IDLE);
				}
			}
		}).start();
	}

	private void initSocket() throws IOException {
		try {
			state = ConnectionState.CONNECTING;
			activeSocket = new Socket(host, port);
			in = new BufferedReader(new InputStreamReader(activeSocket.getInputStream()));
			out = new PrintStream(activeSocket.getOutputStream());
		} catch (UnknownHostException e) {
			throw new IOException(e);
		}
	}

	private boolean login() throws IOException {
		try {
			sessionKey = sendCommand(NetworkCommand.HELLO, player.getName());
			return true;
		} catch (CommunicationException e) {
			if (e.getError() == CommunicationError.NAME_NOT_AVAILABLE) {
				return false;
			} else
				throw e;
		}
	}
	
	private void receiveMatchList() throws IOException {
		String response = sendCommand(NetworkCommand.LIST);
		matches = Match.deserializeMatches(response);
	}
	
	private void receivePlayerList() throws IOException {
		String response = sendCommand(NetworkCommand.LIST);
		matches = Match.deserializeMatches(response);
	}
	
	private void initPassiveMode() {
		final Object sync = new Object();
		new Thread(new Runnable() {
			public void run() {
				Socket socket = null;
				try {
					try {
						socket = new Socket(host, port);
						BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						PrintStream out = new PrintStream(activeSocket.getOutputStream());
						
						sendCommand(in, out, NetworkCommand.PASSIVE, sessionKey);
						sync.notify();

						while (state != ConnectionState.CONNECTED) {
							String line = in.readLine();
							String[] parts = in.readLine().split("\\s", 2);
							if (parts.length < 1)
								throw new IOException("Invalid line in passive mode: " + line);
							NetworkInformation information = NetworkInformation.fromString(parts[0]);
							if (information == null)
								throw new IOException("Invalid network information in passive mode: " + parts[0]);
							String parameter = parts.length >= 2 ? parts[1] : "";
							handleNetworkInformation(information, parameter);
						}
					} catch (IOException e) {
						setState(ConnectionState.NETWORK_ERROR);
						onNetworkError.call(new ExceptionEventArgs(e));
					}
				} finally {
					sync.notify();
					if (socket != null) {
						try {
							socket.close();
						} catch (IOException e) {
							System.out.println("Failed to close passive socket");
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
		
		// Wait until passive mode is activated to make sure that list updates are received properly
		// before receiving lists
		try {
			sync.wait();
		} catch (InterruptedException e) {
		}
	}
	
	private void handleNetworkInformation(NetworkInformation information, String parameter) {
		// used several times
		Match match;
		int id;
		Player player;
		
		switch (information) {
		case MATCH_CREATED:
			match = Match.deserialize(parameter);
			matches.put(match.getID(), match);
			break;
		case MATCH_CANCELLED:
		case MATCH_PAUSED:
		case MATCH_RESUMED:
		case MATCH_STARTED:
			id = Integer.parseInt(parameter);
			match = matches.get(id);
			if (match != null) {
				switch (information) {
				case MATCH_CANCELLED:
					match.cancel();
					matches.remove(id);
					break;
				case MATCH_PAUSED:
					match.pause();
					break;
				case MATCH_RESUMED:
					match.resume();
					break;
				case MATCH_STARTED:
					match.start();
					break;
				}
			} else {
				// Something went wrong
				networkActions.add(new NetworkAction() {
					public void run(BufferedReader in, PrintStream out) throws IOException {
						receiveMatchList();
					}
				});
			}
			break;
		case PLAYER_JOINED_SERVER:
			player = new Player(parameter);
			players.put(player.getName(), player);
			break;
		case PLAYER_LEFT_SERVER:
			players.remove(parameter);
			break;
		case PLAYER_JOINED_MATCH:
		case PLAYER_LEFT_MATCH:
			String[] parts = parameter.split("\\s", 2);
			assert parts.length >= 2;
			player = players.get(parts[0]);
			id = Integer.parseInt(parts[1]);
			match = matches.get(id);
			if (match != null && player != null) {
				switch (information) {
				case PLAYER_JOINED_MATCH:
					match.addPlayer(player);
					break;
				case PLAYER_LEFT_MATCH:
					match.removePlayer(player);
					break;
				}
			} else {
				// Something went wrong
				networkActions.add(new NetworkAction() {
					public void run(BufferedReader in, PrintStream out) throws IOException {
						receiveMatchList();
						receivePlayerList();
					}
				});
			}
			break;
		}
	}

	private String sendCommand(NetworkCommand command)
			throws IOException {
		return sendCommand(command, "");
	}

	private String sendCommand(NetworkCommand command, String parameter) throws IOException {
		return sendCommand(in, out, command, parameter);
	}

	private String sendCommand(BufferedReader in, PrintStream out, NetworkCommand command, String parameter) throws IOException {
		// Sanitize parameter
		parameter = parameter.replace("\n", "");
		
		String line = command.toString() + " " + parameter;
		out.println(line);
		String[] response = in.readLine().split("\\s", 2);
		if (response.length < 1)
			throw new IOException("Invalid response format");
		if ("OK".equals(response[0])) {
			return response.length > 1 ? response[1] : null;
		} else {
			String[] parts = response[1].split("\\s", 2);

			CommunicationError error;
			if (parts.length == 0)
				throw new IOException("Unknown error");
			try {
				error = CommunicationError.valueOf(parts[0]);
			} catch (IllegalArgumentException e) {
				throw new IOException(
						"Invalid error identifier, maybe server and client versions are not compatible");
			}

			if (parts.length == 2)
				throw new CommunicationException(error, parts[1]);
			else
				throw new CommunicationException(error);
		}
	}

	private void setState(ConnectionState state) {
		this.state = state;
		onStateChanged.call(EventArgs.EMPTY);
	}
}
