package de.yogularm.network.server.meta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import de.yogularm.network.CommunicationError;
import de.yogularm.network.NetworkCommand;
import de.yogularm.network.NetworkInformation;
import de.yogularm.network.server.AbstractServerHandler;
import de.yogularm.network.server.ClientData;
import de.yogularm.network.server.ServerData;
import de.yogularm.network.server.ServerHandlerFactory;

public class MetaHandler extends AbstractServerHandler {
	private BufferedReader in;
	private PrintWriter out;
	private ServerData serverData;
	private ClientData clientData;

	/**
	 * Specifies whether a player is assumed as disconnected when this socket is
	 * closed
	 * 
	 * false for passive sockets
	 */
	private boolean isPrimary = true;

	private static Map<NetworkCommand, CommandHandler> commandHandlers = new HashMap<NetworkCommand, CommandHandler>();

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

	public MetaHandler(BufferedReader in, PrintWriter out, ServerData serverData,
			ServerHandlerFactory handlerFactory) throws IOException {
		super(handlerFactory);
		this.in = in;
		this.out = out;
		this.serverData = serverData;
		this.clientData = new ClientData();
		serverData.clientData.put(clientData.key, clientData);
		clientData.serverData = serverData;
	}

	public void run() throws IOException {
		try {
			while (!isInterrupted()) {
				String line = in.readLine();
				if (line == null)
					break; // socket closed
				else
					executeCommand(line);
			}
		} finally {
			if (isPrimary && clientData.player != null) {
				if (clientData.player != null && clientData.player.getCurrentMatch() != null) {
					new LeaveCommand().handle(clientData, "");
				}

				serverData.players.remove(clientData.player);
				serverData
						.notifyClients(NetworkInformation.PLAYER_LEFT_SERVER, clientData.player.getName());
				serverData.clientData.remove(clientData.key);
			}
		}
	}

	private void executeCommand(String line) throws IOException {
		String[] parts = line.split("\\s", 2);
		if (parts.length == 0)
			return;

		String commandStr = parts[0];
		NetworkCommand command = NetworkCommand.fromString(commandStr);
		String parameter = parts.length > 1 ? parts[1].trim() : "";

		switch (command) {
		case PASSIVE:
		case RENEW:
			ClientData data = serverData.clientData.get(parameter);
			if (data == null) {
				out.println(new CommandHandlerUtils().err(CommunicationError.INVALID_SESSION_KEY));
				log("Client tried to authenticate with invalid session key");
			} else {
				out.println(new CommandHandlerUtils().ok());
				clientData = data;

				if (command == NetworkCommand.PASSIVE) {
					runNested(getHandlerFactory().createPassiveHandler(out, clientData));
					interrupt();
					break;
				}
			}
		default:
			CommandHandler handler = command == null ? null : commandHandlers.get(command);
			if (handler == null)
				out.println(new CommandHandlerUtils().err(CommunicationError.INVALID_COMMAND));
			else
				out.println(handler.handle(clientData, parameter));
		}
	}

	private void log(String message) {
		System.out.println("  " + message);
	}
}
