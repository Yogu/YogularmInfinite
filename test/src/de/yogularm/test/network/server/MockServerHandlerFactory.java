package de.yogularm.test.network.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import junit.framework.Assert;
import de.yogularm.network.server.ClientContext;
import de.yogularm.network.server.ServerContext;
import de.yogularm.network.server.ServerHandler;
import de.yogularm.network.server.ServerHandlerFactory;

public class MockServerHandlerFactory implements ServerHandlerFactory {
	@Override
	public ServerHandler
			createStartHandler(InputStream in, OutputStream out, ServerContext serverData) {
		Assert.fail();
		return null;
	}

	@Override
	public ServerHandler
			createMetaHandler(BufferedReader in, PrintWriter out, ServerContext serverData) {
		Assert.fail();
		return null;
	}

	@Override
	public ServerHandler createPassiveHandler(PrintWriter out, ClientContext clientData) {
		Assert.fail();
		return null;
	}

	@Override
	public ServerHandler createBinaryStartHandler(DataInputStream in, DataOutputStream out,
			ServerContext context) {
		Assert.fail();
		return null;
	}

	@Override
	public ServerHandler createBinaryHandler(DataInputStream in, DataOutputStream out,
			ClientContext context) {
		Assert.fail();
		return null;
	}
}