package de.yogularm.network.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public interface ServerHandlerFactory {
	public ServerHandler createStartHandler(InputStream in, OutputStream out, ServerContext context);
	public ServerHandler createMetaHandler(BufferedReader in, PrintWriter out, ServerContext context);
	public ServerHandler createPassiveHandler(PrintWriter out, ClientContext clientContext);
	public ServerHandler createBinaryStartHandler(DataInputStream in, DataOutputStream out, ServerContext context);
	public ServerHandler createBinaryHandler(DataInputStream in, DataOutputStream out, ClientContext context);
}
