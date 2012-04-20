package de.yogularm.network.client;

import java.io.IOException;

public interface ClientHandlerFactory {
	public MetaHandler createMetaHandler() throws IOException;
	public PassiveHandler createPassiveHandler(String sessionKey) throws IOException;
	public BinaryHandler createBinaryHandler(String sessionKey) throws IOException;
}
