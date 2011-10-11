package de.yogularm.android;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import de.yogularm.ExceptionHandler;
import de.yogularm.Game;
import de.yogularm.Rect;
import de.yogularm.Vector;
import de.yogularm.drawing.Color;

public class YogularmRenderer implements GLSurfaceView.Renderer {
	private RenderContextImpl context;
	private Game game;
	private ExceptionHandler exceptionHandler;

	public YogularmRenderer(Game game) {
		this.game = game;
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		try {
			context = new RenderContextImpl(gl);

			gl.glDisable(GL10.GL_DEPTH_TEST);
			gl.glDisable(GL10.GL_CULL_FACE);
			gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glEnable(GL10.GL_BLEND);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			context.checkErrors();

			game.setRenderContext(context);
			game.init();
		} catch (Exception e) {
			if (exceptionHandler != null)
				exceptionHandler.handleException(e);
		}
	}

	public void onDrawFrame(GL10 gl) {
		try {
			game.update();
			game.render();
			
			drawControlArea();
		} catch (Exception e) {
			if (exceptionHandler != null)
				exceptionHandler.handleException(e);
		}
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		try {
			gl.glViewport(0, 0, width, height);
			context.checkErrors();

			game.setResolution(width, height);
		} catch (Exception e) {
			if (exceptionHandler != null)
				exceptionHandler.handleException(e);
		}
	}

	public void setExceptionHandler(ExceptionHandler handler) {
		this.exceptionHandler = handler;
	}
	
	private void drawControlArea() {
		float size = YogularmActivity.CONTROL_SIZE;
		context.beginProjection(1, 1);
		context.beginTransformation();
		context.translate(new Vector(1 - size, 0));
		context.scale(new Vector(size / 3, size / 3));
		context.unbindTexture();
		context.setColor(new Color(0, 0, 0, 0.1f));
		
		// rect
		context.drawRect(new Rect(0, 0, 3, 3));
		
		// oxo
		// oxo
		// oxo
		context.drawRect(new Rect(1, 0, 2, 3));
		// ooo
		// xoo
		// ooo
		context.drawRect(new Rect(0, 1, 1, 2));
		// ooo
		// oox
		// ooo
		context.drawRect(new Rect(2, 1, 3, 2));
		
		context.endTransformation();
		context.endProjection();
	}
}
