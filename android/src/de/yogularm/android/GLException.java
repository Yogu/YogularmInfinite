package de.yogularm.android;

@SuppressWarnings("serial")
public class GLException extends RuntimeException {
	public GLException(String message) {
		super(message);
	}

	public GLException(Throwable throwable) {
		super(throwable);
	}

	public GLException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
