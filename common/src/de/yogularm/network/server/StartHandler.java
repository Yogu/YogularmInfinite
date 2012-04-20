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

import de.yogularm.network.NetworkGlobals;

public class StartHandler extends BasicServerHandler {
	private InputStream in;
	private OutputStream out;
	private ServerContext context;

	public StartHandler(InputStream in, OutputStream out, ServerContext serverData,
			ServerHandlerFactory handlerFactory) {
		super(handlerFactory);
		this.in = in;
		this.out = out;
		this.context = serverData;
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
		case NetworkGlobals.STREAM_MODE_IDENTIFIER_ASCII:
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			PrintWriter writer = new PrintWriter(new BufferedOutputStream(out));
			runNested(getHandlerFactory().createMetaHandler(reader, writer, context));
			return;
		case NetworkGlobals.STREAM_MODE_IDENTIFIER_BINARY:
			DataInputStream input = new DataInputStream(in);
			DataOutputStream output = new DataOutputStream(new BufferedOutputStream(out));
			runNested(getHandlerFactory().createBinaryStartHandler(input, output, context));
			return;
		default:
			writer = new PrintWriter(new BufferedOutputStream(out));
			writer.println("Welcome to Yogularm server. Re-connect and write a newline character to enter text mode.");
			return;
		}
	}
}
