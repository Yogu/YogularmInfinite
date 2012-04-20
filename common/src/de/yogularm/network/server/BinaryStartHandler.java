package de.yogularm.network.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import de.yogularm.network.NetworkGlobals;


public class BinaryStartHandler extends BasicServerHandler {
	private DataInputStream in;
	private DataOutputStream out;
	private ServerContext context;
	
	private static final int MAX_KEY_LEGNTH = 255;

	public BinaryStartHandler(DataInputStream in, DataOutputStream out, ServerContext context,
			ServerHandlerFactory handlerFactory) {
		super(handlerFactory);
		this.in = in;
		this.out = out;
		this.context = context;
	}

	public void run() throws IOException {
		int length = in.readInt();
		if (length < 0 || length > MAX_KEY_LEGNTH) {
			out.write(NetworkGlobals.BINARY_INVALID_SESSION);
			return;
		}
		byte[] data = new byte[length];
		in.read(data, 0, length);
		String key = new String(data, Charset.forName("ASCII"));
		ClientContext clientContext = context.getClientContext(key);
		if (clientContext != null) {
			out.write(NetworkGlobals.BINARY_VALID_SESSION);
			runNested(getHandlerFactory().createBinaryHandler(in, out, clientContext));
		} else {
			out.write(NetworkGlobals.BINARY_INVALID_SESSION);
			System.out.println("Client tried to authenticate with invalid key: " + key);
		}
	}
}
