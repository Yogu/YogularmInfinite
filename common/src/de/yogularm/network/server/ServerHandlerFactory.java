package de.yogularm.network.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public interface ServerHandlerFactory {
	public ServerHandler createStartHandler(InputStream in, OutputStream out, ServerData serverData);
	public ServerHandler createMetaHandler(BufferedReader in, PrintWriter out, ServerData serverData);
	public ServerHandler createPassiveHandler(PrintWriter out, ClientData clientData);
	public ServerHandler createBinaryHandler(DataInputStream in, DataOutputStream out, ServerData serverData);
}
