package de.yogularm;

import de.yogularm.components.MultiPlayerWorld;

public class ServerGame {
	private MultiPlayerWorld world;
	private Thread thread;
	private boolean isRunning;
	private boolean isSuspended;
	private long lastFrameTime;
	private float frameTime;
	
	private static final float TARGET_FPS = 30;

	public ServerGame() {
		world = new MultiPlayerWorld();
	}
	
	public MultiPlayerWorld getWorld() {
		return world;
	}

	public void start() {
		if (isRunning) {
			if (isSuspended)
				resume();
		} else {
			startThread();
		}
	}
	
	public void stop() {
		isRunning = false;
		resume();
	}
	
	public void pause() {
		if (isRunning) {
			isSuspended = true;
		}
	}
	
	public void resume() {
		if (isSuspended) {
			isSuspended = false;
			synchronized (thread) {
				thread.notify();
			}
		}
	}

	private void startThread() {
		thread = new Thread(new Runnable() {
			public void run() {
				ServerGame.this.run();
			}
		});
		isRunning = true;
		thread.start();
	}

	private void run() {
		while (isRunning) {
			while (isSuspended) {
				synchronized (thread) {
					try {
						thread.wait();
					} catch (InterruptedException e) {
						return;
					}
				}
			}
			
			captureFrameTime();
			world.update(frameTime);
			
			float sleepTime = (1f / TARGET_FPS) - frameTime;
			if (sleepTime > 0) {
				try {
					Thread.sleep((int)(sleepTime * 1000));
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	}

	private void captureFrameTime() {
		long newTime = System.nanoTime();
		if (lastFrameTime != 0)
			frameTime = (newTime - lastFrameTime) / 1000000000.0f; // ns to s
		lastFrameTime = newTime;
		frameTime = Math.min(frameTime, Config.MAX_FRAMETIME);
	}
}
