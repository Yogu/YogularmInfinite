package de.yogularm.network.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import de.yogularm.network.BackgroundHandler;

public interface ServerHandlerFactory {
	public BackgroundHandler createStartHandler(InputStream in, OutputStream out, ServerContext context);
	public BackgroundHandler createMetaHandler(BufferedReader in, PrintWriter out, ServerContext context);
	public BackgroundHandler createPassiveHandler(PrintWriter out, ClientContext clientContext);
	public BackgroundHandler createBinaryStartHandler(DataInputStream in, DataOutputStream out, ServerContext context);
	public BackgroundHandler createBinaryHandler(DataInputStream in, DataOutputStream out, ClientContext context);
}
