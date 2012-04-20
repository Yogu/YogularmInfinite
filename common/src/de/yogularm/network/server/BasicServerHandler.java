package de.yogularm.network.server;

import java.io.IOException;

import de.yogularm.network.AbstractBackgroundHandler;
import de.yogularm.network.BackgroundHandler;

public abstract class BasicServerHandler extends AbstractBackgroundHandler implements BackgroundHandler {
	private ServerHandlerFactory handlerFactory;
	BackgroundHandler nestedHandler;
	
	public BasicServerHandler(ServerHandlerFactory handlerFactory) {
		this.handlerFactory = handlerFactory;
	}
	
	protected ServerHandlerFactory getHandlerFactory() {
		return handlerFactory;
	}
	
	protected void runNested(BackgroundHandler handler) throws IOException {
		nestedHandler = handler;
		nestedHandler.run();
	}

	public void interrupt() {
		super.interrupt();
		if (nestedHandler != null)
			nestedHandler.interrupt();
	}
}
