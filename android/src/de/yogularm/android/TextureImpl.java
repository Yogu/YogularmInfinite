package de.yogularm.android;

import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import de.yogularm.drawing.Texture;

public class TextureImpl implements Texture {
	private GL10 gl;
	private int width;
	private int height;
	private int id;
	
	public TextureImpl(GL10 gl, InputStream stream) {
		this.gl = gl;

		Bitmap bmp = BitmapFactory.decodeStream(stream);
		width = bmp.getWidth();
		height = bmp.getHeight();
		int[] ids = new int[1];
		gl.glGenTextures(1, ids, 0);
		id = ids[0];
		gl.glBindTexture(GL10.GL_TEXTURE_2D, id);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);
		bmp.recycle();
	}

	public void bind() {
		gl.glBindTexture(GL10.GL_TEXTURE_2D, id);
	}

	@Override
	public int getWidth() {
		return width; 
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return height;
	}
}
