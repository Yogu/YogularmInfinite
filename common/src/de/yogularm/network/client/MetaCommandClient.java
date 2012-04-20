package de.yogularm.network.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import de.yogularm.network.CommunicationError;
import de.yogularm.network.CommunicationException;
import de.yogularm.network.NetworkCommand;

/**
 * A client implementation of the command-style meta protocol
 */
public class MetaCommandClient {
	PrintWriter out;
	BufferedReader in;

	/**
	 * Creates a new MetaCommandClient
	 * 
	 * @param in the input stream to use
	 * @param out the output stream to use
	 */
	public MetaCommandClient(BufferedReader in, PrintWriter out) {
		this.in = in;
		this.out = out;
	}

	public String sendCommand(NetworkCommand command) throws IOException {
		return sendCommand(command, "");
	}

	public String sendCommand(NetworkCommand command, String parameter) throws IOException {
		// Sanitize parameter
		parameter = parameter.replace("\n", "");

		String line = command.toString() + " " + parameter;
		out.println(line);
		out.flush();
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
						"Invalid error identifier, maybe server and client versions are not compatible (" + res
								+ ")");
			}

			if (parts.length == 2)
				throw new CommunicationException(error, parts[1]);
			else
				throw new CommunicationException(error);
		}
	}

}