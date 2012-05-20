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
	private PrintWriter writer;
	private BufferedReader reader;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	
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
		if (reader == null)
			reader = new BufferedReader(new InputStreamReader(in));
		return reader;
	}
	
	public PrintWriter writer() {
		if (writer == null)
			writer = new PrintWriter(out, true);
		return writer;
	}
	
	public DataInputStream dataIn() {
		if (dataIn == null)
			dataIn = new DataInputStream(in);
		return dataIn;
	}
	
	public DataOutputStream dataOut() {
		if (dataOut == null)
			dataOut = new DataOutputStream(out);
		return dataOut;
	}
	
	public void close() throws IOException {
		in.close();
		out.close();
	}
}
