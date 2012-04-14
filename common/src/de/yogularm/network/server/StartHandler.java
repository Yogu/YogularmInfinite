package de.yogularm.network.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import de.yogularm.network.StreamModeIdentifiers;

public class StartHandler extends AbstractServerHandler {
	private InputStream in;
	private OutputStream out;
	private ServerData serverData;

	public StartHandler(InputStream in, OutputStream out, ServerData serverData,
			ServerHandlerFactory handlerFactory) {
		super(handlerFactory);
		this.in = in;
		this.out = out;
		this.serverData = serverData;
	}

	@Override
	public void run() throws IOException {
		while (in.available() < 1) {
			if (isInterrupted())
				return;
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				return;
			}
		}

		int firstByte = in.read();
		switch (firstByte) {
		case StreamModeIdentifiers.ASCII:
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			PrintWriter writer = new PrintWriter(new BufferedOutputStream(out));
			runNested(getHandlerFactory().createMetaHandler(reader, writer, serverData));
			return;
		case StreamModeIdentifiers.BINARY:
			DataInputStream input = new DataInputStream(in);
			DataOutputStream output = new DataOutputStream(new BufferedOutputStream(out));
			runNested(getHandlerFactory().createBinaryHandler(input, output, serverData));
			return;
		default:
			writer = new PrintWriter(new BufferedOutputStream(out));
			writer.println("Welcome to Yogularm server. Re-connect and write a newline character to enter text mode.");
			return;
		}
	}
}
