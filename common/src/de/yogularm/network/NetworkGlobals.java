package de.yogularm.network;

public class NetworkGlobals {
	public static final int DEFAULT_PORT = 62602;
	public static final String DEFAULT_HOST = "localhost";

	public static final byte STREAM_MODE_IDENTIFIER_ASCII = '\n';
	public static final byte STREAM_MODE_IDENTIFIER_BINARY = 0;
	public static final String CHARSET = "UTF-8";
	
	public static final byte BINARY_VALID_SESSION = 1;
	public static final byte BINARY_INVALID_SESSION = 2;
}
