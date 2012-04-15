package de.yogularm.network.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class BinaryStartHandler extends AbstractServerHandler {
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
			assert false;
			return;
		}
		byte[] data = new byte[length];
		in.read(data, 0, length);
		String key = new String(data, Charset.forName("ASCII"));
		ClientContext clientContext = context.getClientContext(key);
		
		runNested(getHandlerFactory().createBinaryHandler(in, out, clientContext));
	}
}
