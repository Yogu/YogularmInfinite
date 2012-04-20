package de.yogularm.network.server.meta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import de.yogularm.multiplayer.Player;
import de.yogularm.network.CommunicationError;
import de.yogularm.network.NetworkCommand;
import de.yogularm.network.server.BasicServerHandler;
import de.yogularm.network.server.ClientContext;
import de.yogularm.network.server.ServerContext;
import de.yogularm.network.server.ServerHandlerFactory;

public class MetaHandler extends BasicServerHandler {
	private final BufferedReader in;
	private final PrintWriter out;
	private final ServerContext serverContext;
	private ClientContext clientContext;

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

	public MetaHandler(BufferedReader in, PrintWriter out, ServerContext context,
			ServerHandlerFactory handlerFactory) {
		super(handlerFactory);
		this.in = in;
		this.out = out;
		this.serverContext = context;
		clientContext = context.createClientContext();
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
			if (isPrimary) {
				Player player = clientContext.getPlayer();
				if (player != null)
					serverContext.getManager().removePlayer(player);
				serverContext.removeClientContext(clientContext);
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
			ClientContext context = serverContext.getClientContext(parameter);
			if (context == null) {
				out.println(new CommandHandlerUtils().err(CommunicationError.INVALID_SESSION_KEY));
				log("Client tried to authenticate with invalid session key");
			} else {
				out.println(new CommandHandlerUtils().ok());
				clientContext = context;

				if (command == NetworkCommand.PASSIVE) {
					runNested(getHandlerFactory().createPassiveHandler(out, clientContext));
					interrupt();
					break;
				}
			}
		default:
			CommandHandler handler = command == null ? null : commandHandlers.get(command);
			if (handler == null)
				out.println(new CommandHandlerUtils().err(CommunicationError.INVALID_COMMAND));
			else
				out.println(handler.handle(clientContext, parameter));
		}
	}

	private void log(String message) {
		System.out.println("  " + message);
	}
}
