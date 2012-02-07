package de.yogularm.event;

public class ExceptionEventArgs {
	private Throwable exception;
	
	public ExceptionEventArgs(Throwable exception) {
		this.exception = exception;
	}
	
	public Throwable getException() {
		return exception;
	}
}
