package de.yogularm.network;

import java.io.IOException;

/**
 * An exception which occurrs when a network packet could not be handled successfully
 * 
 * The packet is probably skipped properly and the stream thus still valid.
 */
public class InvalidPacketException extends IOException {
	private static final long serialVersionUID = -2123522728275677252L;

	public InvalidPacketException(String message) {
		super(message);
	}
}
