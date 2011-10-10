package de.yogularm.android;

import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

public class Texture {
	private GL10 gl;
	private int width;
	private int height;
	private int id;
	
	public Texture(GL10 gl, InputStream stream) {
		this.gl = gl;

		Bitmap bmp = BitmapFactory.decodeStream(stream);
		//Buffer bb = extract(bmp);
		width = bmp.getWidth();
		height = bmp.getHeight();
		int[] ids = new int[1];
		gl.glGenTextures(1, ids, 0);
		id = ids[0];
		gl.glBindTexture(GL10.GL_TEXTURE_2D, id);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		GLUtils.texImage2D(gl.GL_TEXTURE_2D, 0, bmp, 0);
		/*gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, width, height, 0,
			GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, bb);*/
		bmp.recycle();
	}

	public void bind() {
		gl.glBindTexture(GL10.GL_TEXTURE_2D, id);
	}
}
