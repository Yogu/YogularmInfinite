package de.yogularm.network.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.yogularm.event.Event;
import de.yogularm.event.EventArgs;
import de.yogularm.event.EventListener;
import de.yogularm.event.ExceptionEventArgs;
import de.yogularm.multiplayer.Match;
import de.yogularm.multiplayer.Matches;
import de.yogularm.multiplayer.Player;
import de.yogularm.multiplayer.Players;
import de.yogularm.network.CommunicationError;
import de.yogularm.network.CommunicationException;
import de.yogularm.network.NetworkCommand;
import de.yogularm.network.NetworkInformation;
import de.yogularm.utils.GsonFactory;

public class GameConnection {
	private Socket activeSocket;
	private BufferedReader in;
	private PrintStream out;
	private String sessionKey;

	private String host;
	private int port;
	private Player player;
	private Matches openMatches = new Matches();
	private Players otherPlayers = new Players();
	private ClientWorld world;

	private ConnectionState state = ConnectionState.IDLE;

	private static final int CONNECTION_TIMEOUT = 5000;

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

	/**
	 * An event that is called when a player sends a message
	 */
	public final Event<MessageEventArgs> onMessageReceived = new Event<MessageEventArgs>(this);

	/**
	 * An event that is called when the player's match has started
	 */
	public final Event<MatchStartedEventArgs> onMatchStarted = new Event<MatchStartedEventArgs>(this);

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

	public Matches getOpenMatches() {
		return openMatches;
	}

	public Players getOtherPlayers() {
		return otherPlayers;
	}

	public void sendMessage(final String message) {
		networkActions.add(new NetworkAction() {
			public void run(BufferedReader in, PrintStream out) throws IOException {
				sendCommand(NetworkCommand.SAY, message);
			}
		});
	}

	public void createMatch(final String comment) {
		networkActions.add(new NetworkAction() {
			public void run(BufferedReader in, PrintStream out) throws IOException {
				if (player.getCurrentMatch() == null) {
					sendCommand(NetworkCommand.CREATE, comment);
				}
			}
		});
	}

	public void joinMatch(final Match match) {
		networkActions.add(new NetworkAction() {
			public void run(BufferedReader in, PrintStream out) throws IOException {
				if (player.getCurrentMatch() == null) {
					sendCommand(NetworkCommand.JOIN, match.getID() + "");
				}
			}
		});
	}

	public void leaveMatch() {
		networkActions.add(new NetworkAction() {
			public void run(BufferedReader in, PrintStream out) throws IOException {
				if (player.getCurrentMatch() != null) {
					sendCommand(NetworkCommand.LEAVE);
				}
			}
		});
	}

	public void startMatch() {
		networkActions.add(new NetworkAction() {
			public void run(BufferedReader in, PrintStream out) throws IOException {
				if (player.getCurrentMatch() != null) {
					sendCommand(NetworkCommand.START);
				}
			}
		});
	}

	public void cancelMatch() {
		networkActions.add(new NetworkAction() {
			public void run(BufferedReader in, PrintStream out) throws IOException {
				if (player.getCurrentMatch() != null) {
					sendCommand(NetworkCommand.CANCEL);
				}
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
						receivePlayerList();

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
		state = ConnectionState.CONNECTING;
		activeSocket = new Socket();
		activeSocket.connect(new InetSocketAddress(host, port), CONNECTION_TIMEOUT);
		in = new BufferedReader(new InputStreamReader(activeSocket.getInputStream()));
		out = new PrintStream(activeSocket.getOutputStream());
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
		String response = sendCommand(NetworkCommand.LIST_MATCHES);
		Gson gson = GsonFactory.createGson();
		Type collectionType = new TypeToken<Map<Integer, Match>>() {
		}.getType();
		Map<Integer, Match> matchMap = gson.fromJson(response, collectionType);
		openMatches.replaceAll(matchMap);
	}

	private void receivePlayerList() throws IOException {
		String response = sendCommand(NetworkCommand.LIST_PLAYERS);
		Gson gson = GsonFactory.createGson();
		Type collectionType = new TypeToken<Map<String, Player>>() {
		}.getType();
		Map<String, Player> matchMap = gson.fromJson(response, collectionType);
		if (player != null)
			matchMap.remove(player.getName());
		otherPlayers.replaceAll(matchMap);
	}

	private boolean passiveModeInitialized = false;

	private void initPassiveMode() {
		final Object sync = new Object();
		passiveModeInitialized = false;
		new Thread(new Runnable() {
			public void run() {
				Socket socket = null;
				try {
					try {
						BufferedReader passiveIn;
						synchronized (sync) {
							socket = new Socket();
							socket.connect(new InetSocketAddress(host, port), CONNECTION_TIMEOUT);
							passiveIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
							PrintStream passiveOut = new PrintStream(socket.getOutputStream());

							sendCommand(passiveIn, passiveOut, NetworkCommand.PASSIVE, sessionKey);
						}
						passiveModeInitialized = true;

						while (state == ConnectionState.CONNECTED) {
							String line = passiveIn.readLine();
							if (line == null)
								throw new IOException("Server closed passive connection");

							System.out.println("Information: " + line);
							String[] parts = line.split("\\s", 2);
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
					passiveModeInitialized = true;
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

		// Wait until passive mode is activated to make sure that list updates are
		// received properly
		// before receiving lists
		while (!passiveModeInitialized)
			synchronized (sync) {
			} // Wait until initialization is finished
	}

	private void handleNetworkInformation(NetworkInformation information, String parameter) {
		// used several times
		Match match;
		int id;
		Player player;
		String[] parts;

		switch (information) {
		case MATCH_CREATED:
			match = GsonFactory.createGson().fromJson(parameter, Match.class);

			// the players in the match are created by deserialization and thus are
			// not identical to
			// the players used by this GameConnection object.
			List<Player> players = new ArrayList<Player>(match.getPlayers());
			match.getPlayers().clear();
			for (Player p : players) {
				p = findPlayer(p.getName());
				if (p != null)
					p.joinMatch(match);
			}

			openMatches.add(match);
			break;
		case MATCH_CANCELLED:
		case MATCH_PAUSED:
		case MATCH_RESUMED:
		case MATCH_STARTED:
			id = Integer.parseInt(parameter);
			match = openMatches.getByID(id);
			if (match != null) {
				switch (information) {
				case MATCH_CANCELLED:
					match.cancel();
					openMatches.remove(match);
					if (match.equals(this.player.getCurrentMatch())) {
						stopGame();
					}
					break;
				case MATCH_PAUSED:
					match.pause();
					break;
				case MATCH_RESUMED:
					match.resume();
					break;
				case MATCH_STARTED:
					match.start();
					if (match.equals(this.player.getCurrentMatch())) {
						startGame();
					}
					break;
				}
			} else {
				// Something went wrong
				System.out.println("Unknown match changed state");
				networkActions.add(new NetworkAction() {
					public void run(BufferedReader in, PrintStream out) throws IOException {
						receiveMatchList();
					}
				});
			}
			break;
		case PLAYER_JOINED_SERVER:
			player = GsonFactory.createGson().fromJson(parameter, Player.class);
			if (!player.equals(this.player))
				otherPlayers.add(player);
			break;
		case PLAYER_LEFT_SERVER:
			otherPlayers.remove(parameter);
			break;
		case PLAYER_JOINED_MATCH:
		case PLAYER_LEFT_MATCH:
			parts = parameter.split("\\s", 2);
			assert parts.length >= 2;
			player = findPlayer(parts[0]);
			id = Integer.parseInt(parts[1]);
			match = openMatches.getByID(id);
			if (match != null && player != null) {
				switch (information) {
				case PLAYER_JOINED_MATCH:
					if (player.getCurrentMatch() == null) {
						player.joinMatch(match);
						return;
					}
					break;
				case PLAYER_LEFT_MATCH:
					player.leaveMatch();
					return;
				}
			}

			// Something went wrong
			System.out.println("Unknown match or player");
			networkActions.add(new NetworkAction() {
				public void run(BufferedReader in, PrintStream out) throws IOException {
					receiveMatchList();
					receivePlayerList();
				}
			});
			break;
		case MESSAGE:
			parts = parameter.split("\\s", 2);
			assert parts.length >= 2;
			onMessageReceived.call(new MessageEventArgs(parts[0], parts[1]));
		}
	}

	private void initBinaryStream() {
		new Thread(new Runnable() {
			public void run() {
				Socket socket = null;
				try {
					try {
						socket = new Socket();
						socket.connect(new InetSocketAddress(host, port), CONNECTION_TIMEOUT);
						InputStream in = socket.getInputStream();
						// Buffer with size 1 because otherwise, bytes from the binary protocol might be read
						// out into the buffer and be missing in the binary handler
						BufferedReader reader = new BufferedReader(new InputStreamReader(in), 1);
						final OutputStream out = socket.getOutputStream();
						PrintStream writer = new PrintStream(out);
						// TODO
						//sendCommand(reader, writer, NetworkCommand.BINARY, sessionKey);
						System.out.println("Binary stream established");
						System.out.println("Next byte:");
						System.out.println(in.read());
						
						new Thread(new Runnable() {
							public void run() {
								try {
									world.handleOutput(out);
								} catch (IOException e) {
									setState(ConnectionState.NETWORK_ERROR);
									onNetworkError.call(new ExceptionEventArgs(e));
								}
							}
						}).start();
						
						world.handleInput(in);
					} catch (IOException e) {
						setState(ConnectionState.NETWORK_ERROR);
						onNetworkError.call(new ExceptionEventArgs(e));
					}
				} finally {
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
	}

	private String sendCommand(NetworkCommand command) throws IOException {
		return sendCommand(command, "");
	}

	private String sendCommand(NetworkCommand command, String parameter) throws IOException {
		return sendCommand(in, out, command, parameter);
	}

	private String sendCommand(BufferedReader in, PrintStream out, NetworkCommand command,
			String parameter) throws IOException {
		// Sanitize parameter
		parameter = parameter.replace("\n", "");

		String line = command.toString() + " " + parameter;
		out.println(line);
		System.out.println("Sent: " + line);
		String res = in.readLine();
		if (res == null)
			throw new IOException("Server has closed the connection");
		System.out.println("Response: " + res);
		String[] response = res.split("\\s", 2);
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
					"Invalid error identifier, maybe server and client versions are not compatible (" + 
						res + ")");
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

	private Player findPlayer(String name) {
		if (player != null && player.getName().equals(name))
			return player;
		else
			return otherPlayers.getByName(name);
	}

	private void startGame() {
		world = new ClientWorld();
		world.onWorldInitialized.addListener(new EventListener<Void>() {			
			@Override
			public void call(Object sender, Void param) {
				onMatchStarted.call(new MatchStartedEventArgs(world));
			}
		});
		initBinaryStream();
	}
	
	private void stopGame() {
		if (world != null) {
			world.stop();
		}
	}
}
