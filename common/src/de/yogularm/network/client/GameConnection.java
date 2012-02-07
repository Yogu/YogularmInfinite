package de.yogularm.network.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import de.yogularm.event.Event;
import de.yogularm.event.EventArgs;
import de.yogularm.event.ExceptionEventArgs;
import de.yogularm.network.CommunicationError;
import de.yogularm.network.CommunicationException;
import de.yogularm.network.NetworkCommand;
import de.yogularm.network.Player;

public class GameConnection {
	private Socket socket;
	private BufferedReader in;
	private PrintStream out;
	private String sessionKey;

	private String host;
	private int port;
	private Player player;

	private ConnectionState state = ConnectionState.IDLE;

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

	public ConnectionState getState() {
		return state;
	}

	private void initThread() {
		state = ConnectionState.IDLE;
		new Thread(new Runnable() {
			public void run() {
				try {
					if (initSocket() && login()) {
						setState(ConnectionState.CONNECTED);
						// TODO
					}
				} catch (IOException e) {
					setState(ConnectionState.NETWORK_ERROR);
					onNetworkError.call(new ExceptionEventArgs(e));
				}
			}
		}).run();
	}

	private boolean initSocket() throws IOException {
		try {
			state = ConnectionState.CONNECTING;
			socket = new Socket(host, port);
			state = ConnectionState.CONNECTED;
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintStream(socket.getOutputStream());
			return true;
		} catch (UnknownHostException e) {
			setState(ConnectionState.NETWORK_ERROR);
			onNetworkError.call(new ExceptionEventArgs(e));
		}
		return false;
	}

	private boolean login() throws IOException {
		try {
			sessionKey = sendCommand(NetworkCommand.HELLO, player.getName());
			return true;
		} catch (CommunicationException e) {
			if (e.getError() == CommunicationError.NAME_NOT_AVAILABLE) {
				setState(ConnectionState.NAME_NOT_AVAILABLE);
				return false;
			} else
				throw e;
		}
	}

	private String sendCommand(NetworkCommand command) throws IOException {
		return sendCommand(command, "");
	}

	private String sendCommand(NetworkCommand command, String parameter) throws IOException {
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
