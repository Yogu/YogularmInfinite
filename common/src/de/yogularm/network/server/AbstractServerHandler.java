package de.yogularm.network.server;

import java.io.IOException;

public abstract class AbstractServerHandler implements ServerHandler {
	private boolean isInterrupted;
	private ServerHandlerFactory handlerFactory;
	private ServerHandler nestedHandler;
	
	public AbstractServerHandler(ServerHandlerFactory handlerFactory) {
		this.handlerFactory = handlerFactory;
	}
	
	public void interrupt() {
		isInterrupted = true;
		if (nestedHandler != null)
			nestedHandler.interrupt();
	}
	
	protected boolean isInterrupted() {
		return isInterrupted;
	}
	
	protected ServerHandlerFactory getHandlerFactory() {
		return handlerFactory;
	}
	
	protected void runNested(ServerHandler handler) throws IOException {
		nestedHandler = handler;
		nestedHandler.run();
	}
}
