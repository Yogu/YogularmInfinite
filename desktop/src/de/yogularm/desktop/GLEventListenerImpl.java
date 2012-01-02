package de.yogularm.desktop;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import com.jogamp.opengl.util.FPSAnimator;

import de.yogularm.ExceptionHandler;
import de.yogularm.Game;
import de.yogularm.drawing.RenderContext;

public class GLEventListenerImpl implements GLEventListener {
	private FPSAnimator animator;
	private Game game;
	private RenderContextImpl context;
	//private RenderContext context;
	private ExceptionHandler exceptionHandler;
	
	public GLEventListenerImpl(Game game) {
		this.game = game;
	}

	public void init(GLAutoDrawable drawable) {
		try {
			GL2 gl = drawable.getGL().getGL2();

			context = new RenderContextImpl(gl);
			//context = new DummyRenderContext();
			
			gl.glDisable(GL.GL_DEPTH_TEST);
			gl.glDisable(GL.GL_CULL_FACE);
			gl.glEnable(GL.GL_TEXTURE_2D);
			gl.glEnable(GL.GL_BLEND);
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			context.checkErrors();
		} catch (Exception e) {
			if (exceptionHandler != null)
				exceptionHandler.handleException(e);
		}
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		try {
			game.setResolution(context, width, height);
			
			GL2 gl = drawable.getGL().getGL2();
			gl.glViewport(0, 0, width, height);
			//context.checkErrors();
		} catch (Exception e) {
			if (exceptionHandler != null)
				exceptionHandler.handleException(e);
		}
	}

	public void dispose(GLAutoDrawable drawable) {

	}

	public void display(GLAutoDrawable drawable) {
		try {
			game.update();
			game.render(context);
		} catch (Exception e) {
			if (exceptionHandler != null)
				exceptionHandler.handleException(e);
		}
	}

	public void start(GLAutoDrawable drawable) {
		try {
			animator = new FPSAnimator(drawable, 60);
			animator.add(drawable);
			animator.start();
		} catch (Exception e) {
			if (exceptionHandler != null)
				exceptionHandler.handleException(e);
		}
	}

	public void stop() {
		try {
			animator.stop();
		} catch (Exception e) {
			if (exceptionHandler != null)
				exceptionHandler.handleException(e);
		}
	}

	public void setExceptionHandler(ExceptionHandler handler) {
		this.exceptionHandler = handler;
	}
}
