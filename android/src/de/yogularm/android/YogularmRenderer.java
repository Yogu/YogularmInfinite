package de.yogularm.android;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import de.yogularm.Game;
import de.yogularm.Res;
import de.yogularm.drawing.Color;
import de.yogularm.drawing.RenderTransformation;
import de.yogularm.event.ExceptionHandler;
import de.yogularm.geometry.Vector;

public class YogularmRenderer implements GLSurfaceView.Renderer {
	private RenderContextImpl context;
	private Game game;
	private ExceptionHandler exceptionHandler;
	private int width = 1;
	private int height = 1;
	private boolean isInitialized;
	
	private static final float CONTROL_DISPLAY_VERTICAL_FRACTION = 2 / 3f;

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
			
			isInitialized = true;
		} catch (Exception e) {
			handleException(e);
		}
	}

	public void onDrawFrame(GL10 gl) {
		try {
			// Some devices seem to call onDrawFrame() before onSurfaceCreated()
			if (isInitialized) {
				game.update();
				game.render(context);
				
				drawControlArea();
			}
		} catch (Exception e) {
			handleException(e);
		}
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		try {
			// Better allow any sequence of these events.
			if (isInitialized) {
				this.width = width;
				this.height = height;
				
				gl.glViewport(0, 0, width, height);
				context.checkErrors();

				game.setResolution(context, width, height);
			}
		} catch (Exception e) {
			handleException(e);
		}
	}

	public void setExceptionHandler(ExceptionHandler handler) {
		this.exceptionHandler = handler;
	}
	
	private void drawControlArea() {
		float size = YogularmActivity.CONTROL_SIZE * CONTROL_DISPLAY_VERTICAL_FRACTION;
		float div = YogularmActivity.CONTROL_DISPLAY_HORIZONTAL_DIVISION;
		context.beginProjection(1, 1);
		context.beginTransformation();
		context.scale(new Vector(1 / div, size));
		context.unbindTexture();
		context.setColor(Color.white);

		// the size of an control bar quarter
		float xSize = width / div;
		float ySize = height * size;
		Vector scale;
		if (xSize > ySize)
			scale = new Vector(ySize / xSize, 1);
		else
			scale = new Vector(1, xSize / ySize);

		float last = div - 1;
		Vector center = new Vector(0.5f, 0.5f);
		new RenderTransformation(Res.images.arrowKey, new Vector(0,        0), scale, 0,   center).draw(context);
		new RenderTransformation(Res.images.arrowKey, new Vector(1,        0), scale, 180, center).draw(context);
		new RenderTransformation(Res.images.arrowKey, new Vector(last - 1, 0), scale, 90,  center).draw(context);
		new RenderTransformation(Res.images.arrowKey, new Vector(last,     0), scale, 270, center).draw(context);
		
		context.endTransformation();
		context.endProjection();
	}
	
	private void handleException(Exception e) {
		if (exceptionHandler != null)
			exceptionHandler.handleException(e);
	}
}
