package de.yogularm.android;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Set;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;
import de.yogularm.Rect;
import de.yogularm.Vector;
import de.yogularm.drawing.Color;
import de.yogularm.drawing.Font;
import de.yogularm.drawing.FontStyle;
import de.yogularm.drawing.RenderContext;
import de.yogularm.drawing.Texture;

public class RenderContextImpl implements RenderContext {
	private GL10 gl;
	
	public RenderContextImpl(GL10 gl) {
		this.gl = gl;
	}

	@Override
	public void setColor(Color color) {
		gl.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
		checkErrors();
	}

	@Override
	public void unbindTexture() {
		gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
		checkErrors();
	}

	@Override
	public void drawRect(Rect bounds) {
		FloatBuffer quadVertices = createBuffer(new float[] { 
			bounds.getLeft(),  bounds.getTop(),    0,
			bounds.getLeft(),  bounds.getBottom(), 0,
			bounds.getRight(), bounds.getTop(),    0,
			bounds.getRight(), bounds.getBottom(), 0 
		});

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, quadVertices);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		checkErrors();
	}

	@Override
	public void drawRect(Rect bounds, Rect textureBounds) {
		FloatBuffer quadVertices = createBuffer(new float[] { 
			bounds.getLeft(),  bounds.getTop(),    0,
			bounds.getLeft(),  bounds.getBottom(), 0,
			bounds.getRight(), bounds.getTop(),    0,
			bounds.getRight(), bounds.getBottom(), 0 
		});
		
		FloatBuffer quadTexCoords = createBuffer(new float[] { 
			textureBounds.getLeft(),  textureBounds.getBottom(),
			textureBounds.getLeft(),  textureBounds.getTop(),
			textureBounds.getRight(), textureBounds.getBottom(), 
			textureBounds.getRight(), textureBounds.getTop()
		});

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, quadVertices);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, quadTexCoords);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		checkErrors();
	}

	@Override
	public void drawLines(Vector[] coords, float lineWidth, boolean doStrip) {
		float[] data = new float[coords.length * 3];
		for (int i = 0; i < coords.length; i++) {
			data[i * 3]    = coords[i].getX();
			data[i * 3 + 1] = coords[i].getY();
		}
		FloatBuffer buffer = createBuffer(data);

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, buffer);
		int type = doStrip ? GL10.GL_LINE_STRIP : GL10.GL_LINES;
		gl.glDrawArrays(type, 0, coords.length);
		checkErrors();
	}

	@Override
	public void drawText(Vector position, Font font, String text) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetTranformation() {
		gl.glLoadIdentity();
		checkErrors();
	}

	@Override
	public void beginTransformation() {
		gl.glPushMatrix();
		checkErrors();
	}

	@Override
	public void endTransformation() {
		gl.glPopMatrix();
		checkErrors();
	}

	@Override
	public void translate(Vector offset) {
		gl.glTranslatef(offset.getX(), offset.getY(), 0);
		checkErrors();
	}

	@Override
	public void scale(Vector factor) {
		gl.glScalef(factor.getX(), factor.getY(), 1);
		checkErrors();
	}

	@Override
	public void rotate(float angle) {
		gl.glRotatef(angle, 0, 0, 1);
		checkErrors();
	}

	@Override
	public void clear(Color clearColor) {
		gl.glClearColor(clearColor.getRed(), clearColor.getGreen(), clearColor.getBlue(), clearColor.getAlpha());
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		checkErrors();
	}

	public void beginProjection(float width, float height) {
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glOrthof(0,	width, 0, height, -1, 1);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		checkErrors();
	}

	public void endProjection() {
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		checkErrors();
	}

	@Override
	public void setProjection(float width, float height) {
		if (width == 0 || height == 0)
			throw new RuntimeException("Either width or height is zero");

		checkErrors();
		gl.glMatrixMode(GL10.GL_PROJECTION);
		checkErrors();
		gl.glLoadIdentity();
		checkErrors();
		gl.glOrthof(0, width, 0, height, -1, 1);
		checkErrors();
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		checkErrors();
	}

	@Override
	public Texture loadTexture(InputStream stream) {
		return new TextureImpl(this, gl, stream);
	}

	@Override
	public Font loadFont(int size, Set<FontStyle> style) {
		return new FontImpl();
	}
	
	private FloatBuffer createBuffer(float[] vertices) {
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);

		// use the device hardware's native byte order
		byteBuffer.order(ByteOrder.nativeOrder());

		// create a floating point buffer from the ByteBuffer
		FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
		
		floatBuffer.put(vertices);

		// set the buffer to read the first coordinate
		floatBuffer.position(0);
		return floatBuffer;
	}
	
	public void checkErrors() {
		int error = gl.glGetError();
		if (error != GL10.GL_NO_ERROR) {
			throw new GLException(GLU.gluErrorString(error));
		}
	}
}
