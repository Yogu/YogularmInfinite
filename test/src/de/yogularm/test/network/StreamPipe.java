package de.yogularm.test.network;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;

public class StreamPipe {
	private InputStream in;
	private OutputStream out;
	
	public StreamPipe(boolean buffered) throws IOException {
		PipedOutputStream pipedOut = new PipedOutputStream();
		out = pipedOut;
		in = new PipedInputStream(pipedOut);
		
		if (buffered) {
			out = new BufferedOutputStream(out);
		}
	}
	
	public InputStream in() {
		return in;
	}
	
	public OutputStream out() {
		return out;
	}
	
	public BufferedReader reader() {
		return new BufferedReader(new InputStreamReader(in));
	}
	
	public PrintWriter writer() {
		return new PrintWriter(out);
	}
	
	public DataInputStream dataIn() { 
		return new DataInputStream(in);
	}
	
	public DataOutputStream dataOut() {
		return new DataOutputStream(out);
	}
}
