package de.yogularm.desktop;

import java.io.IOException;
import java.io.InputStream;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.texture.TextureIO;

import de.yogularm.Rect;
import de.yogularm.Vector;
import de.yogularm.drawing.Color;
import de.yogularm.drawing.Font;
import de.yogularm.drawing.RenderContext;
import de.yogularm.drawing.Texture;

public class RenderContextImpl implements RenderContext {
	private GL2 gl;
	
	public RenderContextImpl(GL2 gl) {
		this.gl = gl;
	}
	
	public void setColor(Color color) {
		gl.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
		checkErrors();
	}
	
	public void unbindTexture() {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
		checkErrors();
	}
	
	public void drawRect(Rect bounds) {
		gl.glBegin(GL2.GL_QUADS);
			gl.glVertex2f(bounds.getLeft(),  bounds.getBottom());
			gl.glVertex2f(bounds.getRight(), bounds.getBottom());
			gl.glVertex2f(bounds.getRight(), bounds.getTop());
			gl.glVertex2f(bounds.getLeft(), bounds.getTop());
		gl.glEnd();
		checkErrors();
	}
	
	public void drawRect(Rect bounds, Rect textureBounds) {
		gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(textureBounds.getLeft(), textureBounds.getTop());
			gl.glVertex2f(bounds.getLeft(),  bounds.getBottom());
			
			gl.glTexCoord2f(textureBounds.getRight(), textureBounds.getTop());
			gl.glVertex2f(bounds.getRight(), bounds.getBottom());
			
			gl.glTexCoord2f(textureBounds.getRight(), textureBounds.getBottom());
			gl.glVertex2f(bounds.getRight(), bounds.getTop());
			
			gl.glTexCoord2f(textureBounds.getLeft(), textureBounds.getBottom());
			gl.glVertex2f(bounds.getLeft(), bounds.getTop());
		gl.glEnd();	
		checkErrors();
	}
	
	public void drawLines(Vector[] coords, float lineWidth, boolean doStrip) {
		gl.glLineWidth(lineWidth);
		gl.glBegin(doStrip ? GL2.GL_LINE_STRIP : GL2.GL_LINES);
			for (Vector v : coords) {
				gl.glVertex2f(v.getX(), v.getY());
			}
		gl.glEnd();
		checkErrors();
	}
	
	public void drawText(Vector position, Font font, String text) {
		// TODO: implement.
	}
	
	public void resetTranformation() {
		gl.glLoadIdentity();
		checkErrors();
	}
	
	public void beginTransformation() {
		gl.glPushMatrix();
		checkErrors();
	}
	public void endTransformation() {
		gl.glPopMatrix();
		checkErrors();
	}
	
	public void translate(Vector offset) {
		gl.glTranslatef(offset.getX(), offset.getY(), 0);
		checkErrors();
	}
	
	public void scale(Vector factor) {
		gl.glScalef(factor.getX(), factor.getY(), 1);
		checkErrors();
	}
	
	public void rotate(float angle) {
		gl.glRotatef(angle, 0, 0, 1);
		checkErrors();
	}
	
	public void clear(Color clearColor) {
		gl.glClearColor(clearColor.getRed(), clearColor.getGreen(), clearColor.getBlue(), clearColor.getAlpha());
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
		checkErrors();
	}
	
	public void setProjection(float width, float height) {
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glOrtho(0, width, 0, height, -1, 1);
		checkErrors();
	}
	
	public Texture loadTexture(InputStream stream) {
		com.jogamp.opengl.util.texture.Texture texture;
		try {
			texture = TextureIO.newTexture(stream, false, "png");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		texture.setTexParameteri(GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
		texture.setTexParameteri(GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
		checkErrors();
		
		return new TextureImpl(texture);
	}
	
	public Font loadFont() {
		// TODO: load fonts
		return new FontImpl();
	}
	
	public void checkErrors() {
		int error = gl.glGetError();
		if (error != GL2.GL_NO_ERROR) {
			throw new GLException(new GLU().gluErrorString(error));
		}
	}
}
