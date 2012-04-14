package de.yogularm.test.network.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import junit.framework.Assert;
import de.yogularm.network.server.ClientData;
import de.yogularm.network.server.ServerData;
import de.yogularm.network.server.ServerHandler;
import de.yogularm.network.server.ServerHandlerFactory;

public class MockServerHandlerFactory implements ServerHandlerFactory {
	@Override
	public ServerHandler
			createStartHandler(InputStream in, OutputStream out, ServerData serverData) {
		Assert.fail();
		return null;
	}

	@Override
	public ServerHandler
			createMetaHandler(BufferedReader in, PrintWriter out, ServerData serverData) {
		Assert.fail();
		return null;
	}

	@Override
	public ServerHandler createPassiveHandler(PrintWriter out, ClientData clientData) {
		Assert.fail();
		return null;
	}

	@Override
	public ServerHandler createBinaryHandler(DataInputStream in, DataOutputStream out,
			ServerData serverData) {
		Assert.fail();
		return null;
	}
}