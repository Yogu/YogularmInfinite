package de.yogularm.network.client;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import de.yogularm.network.InputOutputStreams;
import de.yogularm.network.NetworkGlobals;

public class DefaultClientHandlerFactory implements ClientHandlerFactory {
	private ClientConnector connector;
	
	public DefaultClientHandlerFactory(ClientConnector connector) {
		this.connector = connector;
	}
	
	@Override
	public MetaHandler createMetaHandler() throws IOException {
		InputOutputStreams streams = connector.openConnection();
		initStreams(streams, NetworkGlobals.STREAM_MODE_IDENTIFIER_ASCII);
		return new DefaultMetaHandler(
				new BufferedReader(new InputStreamReader(streams.getInputStream())), 
				new PrintWriter(new BufferedOutputStream(streams.getOutputStream())));
	}

	@Override
	public PassiveHandler createPassiveHandler(String sessionKey) throws IOException {
		InputOutputStreams streams = connector.openConnection();
		initStreams(streams, NetworkGlobals.STREAM_MODE_IDENTIFIER_ASCII);
		return new DefaultPassiveHandler(
				new BufferedReader(new InputStreamReader(streams.getInputStream())), 
				new PrintWriter(new BufferedOutputStream(streams.getOutputStream())),
				sessionKey);
	}

	@Override
	public BinaryHandler createBinaryHandler(String sessionKey) throws IOException {
		InputOutputStreams streams = connector.openConnection();
		initStreams(streams, NetworkGlobals.STREAM_MODE_IDENTIFIER_BINARY);
		return new DefaultBinaryHandler(
				new DataInputStream(streams.getInputStream()),
				new DataOutputStream(new BufferedOutputStream(streams.getOutputStream())), sessionKey);
	}
	
	private void initStreams(InputOutputStreams streams, int type) throws IOException {
		streams.getOutputStream().write(type);
		streams.getOutputStream().flush();
	}
}
