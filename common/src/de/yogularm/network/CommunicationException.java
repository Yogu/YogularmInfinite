package de.yogularm.network;

import java.io.IOException;

public class CommunicationException extends IOException {
	private static final long serialVersionUID = 6669121645185160183L;
	
	private CommunicationError error;

	public CommunicationException(CommunicationError error) {
		super(error.toString());
		this.error = error;
	}

	public CommunicationException(CommunicationError error, String comment) {
		super(error + ": " + comment);
		this.error = error;
	}
	
	public CommunicationError getError() {
		return error;
	}
}
