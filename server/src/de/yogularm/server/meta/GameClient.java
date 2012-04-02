package de.yogularm.server.meta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import de.yogularm.network.CommunicationError;
import de.yogularm.network.NetworkCommand;
import de.yogularm.network.NetworkInformation;
import de.yogularm.server.ClientData;
import de.yogularm.server.ServerData;
import de.yogularm.utils.Exceptions;

public class GameClient extends Thread {
	private Runnable closedHandler;
	private BufferedReader reader;
	private PrintStream writer;
	private ServerData serverData;
	private ClientData clientData;
	
	/**
	 * Specifies whether a player is assumed as disconnected when this socket is closed
	 * 
	 * false for passive sockets
	 */
	private boolean isPrimary = true;
	
	private static Map<NetworkCommand, CommandHandler> commandHandlers =
		new HashMap<NetworkCommand, CommandHandler>();
	
	static {
		commandHandlers.put(NetworkCommand.VERSION, new VersionCommand());
		commandHandlers.put(NetworkCommand.HELLO, new HelloCommand());
		commandHandlers.put(NetworkCommand.LIST_MATCHES, new ListMatchesCommand());
		commandHandlers.put(NetworkCommand.LIST_PLAYERS, new ListPlayersCommand());
		commandHandlers.put(NetworkCommand.CREATE, new CreateCommand());
		commandHandlers.put(NetworkCommand.JOIN, new JoinCommand());
		commandHandlers.put(NetworkCommand.LEAVE, new LeaveCommand());
		commandHandlers.put(NetworkCommand.START, new StartCommand());
		commandHandlers.put(NetworkCommand.PAUSE, new PauseCommand());
		commandHandlers.put(NetworkCommand.RESUME, new ResumeCommand());
		commandHandlers.put(NetworkCommand.CANCEL, new CancelCommand());
		commandHandlers.put(NetworkCommand.SAY, new SayCommand());
	}

	public GameClient(InputStream in, OutputStream out, ServerData serverData) throws IOException {
		this.serverData = serverData;
		this.clientData = new ClientData();
		serverData.clientData.put(clientData.key, clientData);
		clientData.serverData = serverData;
		reader = new BufferedReader(new InputStreamReader(in));
		writer = new PrintStream(out);
	}

	public void run() {
		try {
			while (!isInterrupted()) {
				try {
					String line = reader.readLine();
					if (line == null)
						break; // socket closed
					else
						executeCommand(line);
				} catch (Exception e) {
					log("Exception in GameClient thread: " + Exceptions.formatException(e));
					e.printStackTrace();
					break;
				}
			}
		} finally {
			if (isPrimary && clientData.player != null) {
				if (clientData.player != null && clientData.player.getCurrentMatch() != null) {
					new LeaveCommand().handle(clientData, "");
				}
				
				serverData.players.remove(clientData.player);
				serverData.notifyClients(NetworkInformation.PLAYER_LEFT_SERVER, clientData.player.getName());
				serverData.clientData.remove(clientData.key);
			}
			
			if (closedHandler != null)
				closedHandler.run();
		}
	}
	
	public void setClosedHandler(Runnable handler) {
		this.closedHandler = handler;
	}
	
	private void executeCommand(String line) {
		String[] parts = line.split("\\s", 2);
		if (parts.length == 0)
			return;
		
		String commandStr = parts[0];
		NetworkCommand command = NetworkCommand.fromString(commandStr);
		String parameter = parts.length > 1 ? parts[1].trim() : "";
		
		if ((command == NetworkCommand.PASSIVE) || (command == NetworkCommand.RENEW)) {
			ClientData data = serverData.clientData.get(parameter);
			if (data == null) {
				writer.println(new CommandHandlerUtils().err(CommunicationError.INVALID_SESSION_KEY));
				System.out.println("Client tried to authenticate with invalid session key");
			} else {
				writer.println("OK");
			clientData = data;
				
				if (command == NetworkCommand.PASSIVE)
					doPassive();
			}
		} else {
			CommandHandler handler = command == null ? null : commandHandlers.get(command);
			if (handler == null)
				writer.println("ERR INVALID_COMMAND");
			else
				writer.println(handler.handle(clientData, parameter));
		}
	}
	
	private void doPassive() {
		isPrimary = false;
		PassiveHandler handler = new PassiveHandler(serverData, clientData, writer);
		handler.run();
		// Handler has closed, nothing more to do here
		interrupt();
	}
	
	private void log(String message) {
		System.out.println("  " + message);
	}
}
