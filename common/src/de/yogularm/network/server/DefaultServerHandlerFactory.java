package de.yogularm.network.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import de.yogularm.network.server.meta.MetaHandler;
import de.yogularm.network.server.meta.PassiveHandler;

public class DefaultServerHandlerFactory implements ServerHandlerFactory {
	@Override
	public ServerHandler createStartHandler(InputStream in, OutputStream out, ServerContext context) {
		return new StartHandler(in, out, context, this);
	}

	@Override
	public ServerHandler createMetaHandler(BufferedReader in, PrintWriter out, ServerContext context) {
		return new MetaHandler(in, out, context, this);
	}

	@Override
	public ServerHandler createPassiveHandler(PrintWriter out, ClientContext clientContext) {
		return new PassiveHandler(out, clientContext, this);
	}

	@Override
	public ServerHandler createBinaryHandler(DataInputStream in, DataOutputStream out,
			ClientContext clientContext) {
		return new BinaryHandler(in, out, clientContext, this);
	}

	@Override
	public ServerHandler createBinaryStartHandler(DataInputStream in, DataOutputStream out, ServerContext context) {
		return new BinaryStartHandler(in, out, context, this);
	}
}
