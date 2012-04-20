package de.yogularm.network;

import java.io.InputStream;
import java.io.OutputStream;

public class InputOutputStreams {
	private final InputStream in;
	private final OutputStream out;
	
	public InputOutputStreams(InputStream in, OutputStream out) {
		this.in = in;
		this.out = out;
	}
	
	public InputStream getInputStream() {
		return in;
	}
	
	public OutputStream getOutputStream() {
		return out;
	}
}
