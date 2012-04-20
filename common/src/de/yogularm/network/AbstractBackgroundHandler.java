package de.yogularm.network;


public abstract class AbstractBackgroundHandler implements BackgroundHandler {
	private boolean isInterrupted;

	public AbstractBackgroundHandler() {
		super();
	}

	public void interrupt() {
		isInterrupted = true;
	}

	protected boolean isInterrupted() {
		return isInterrupted;
	}
}