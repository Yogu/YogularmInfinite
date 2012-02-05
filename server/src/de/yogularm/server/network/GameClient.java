package de.yogularm.server.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import de.yogularm.network.NetworkCommand;
import de.yogularm.server.ServerData;
import de.yogularm.utils.Exceptions;

public class GameClient extends Thread {
	private Runnable closedHandler;
	//private InputStream in;
	//private OutputStream out;
	private BufferedReader reader;
	private PrintStream writer;
	private ServerData serverData;
	private ClientData clientData;
	
	private static Map<NetworkCommand, CommandHandler> commandHandlers =
		new HashMap<NetworkCommand, CommandHandler>();
	
	static {
		commandHandlers.put(NetworkCommand.VERSION, new VersionCommand());
		commandHandlers.put(NetworkCommand.HELLO, new HelloCommand());
		commandHandlers.put(NetworkCommand.LIST, new ListCommand());
		commandHandlers.put(NetworkCommand.CREATE, new CreateCommand());
		commandHandlers.put(NetworkCommand.JOIN, new JoinCommand());
		commandHandlers.put(NetworkCommand.LEAVE, new LeaveCommand());
		commandHandlers.put(NetworkCommand.START, new StartCommand());
		commandHandlers.put(NetworkCommand.PAUSE, new PauseCommand());
		commandHandlers.put(NetworkCommand.RESUME, new ResumeCommand());
		commandHandlers.put(NetworkCommand.CANCEL, new CancelCommand());
		//commandHandlers.put(NetworkCommand.SAY, new SayCommand());
	}

	public GameClient(InputStream in, OutputStream out, ServerData serverData) throws IOException {
		//this.in = in;
		//this.out = out;
		this.serverData = serverData;
		this.clientData = new ClientData();
		clientData.serverData = serverData;
		reader = new BufferedReader(new InputStreamReader(in));
		writer = new PrintStream(out);
	}

	public void run() {
		try {
			while (!isInterrupted()) {
				try {
					String line = reader.readLine();
					executeCommand(line);
				} catch (Exception e) {
					log("Exception in GameClient thread: " + Exceptions.formatException(e));
				}
			}
		} finally {
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
		
		if (command == NetworkCommand.PASSIVE) {
			writer.println("OK");
			doPassive();
		} else {
			CommandHandler handler = command == null ? null : commandHandlers.get(command);
			if (handler == null)
				writer.println("ERR INVALID_COMMAND");
			writer.println(handler.handle(clientData, parameter));
		}
	}
	
	private void doPassive() {
		
	}
	
	private void log(String message) {
		System.out.println("  " + message);
	}
}
