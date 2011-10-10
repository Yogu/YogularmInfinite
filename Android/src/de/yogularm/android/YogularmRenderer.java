package de.yogularm.android;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class YogularmRenderer implements GLSurfaceView.Renderer {
	private FloatBuffer quadVertices;
	private FloatBuffer quadTexCoords;
	private Context context;
	
	public YogularmRenderer(Context context) {
		this.context = context;
	}
	
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		Res.laod(gl, context);
		
		// Set the background frame color
		gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		quadVertices = createBuffer(new float[] { 
			0, 0, 0,
			0, 1, 0, 
			1, 1, 0, 
			0, 0, 0, 
			1, 1, 0, 
			1, 0, 0 
		});
		
		quadTexCoords = createBuffer(new float[] { 
			0, 0,
			0, 1, 
			1, 1, 
			0, 0, 
			1, 1, 
			1, 0, 
		});
	}

	public void onDrawFrame(GL10 gl) {
		// Redraw background color
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
    // Enable use of vertex arrays06	        
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

    // Set GL_MODELVIEW transformation mode
    gl.glMatrixMode(GL10.GL_MODELVIEW);
    gl.glLoadIdentity();   // reset the matrix to its default state
    
		gl.glColor4f(1, 1, 0, 1);
		Res.coin.bind();
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, quadVertices);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, quadTexCoords);
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 2 * 3);
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);

    // Set GL_MODELVIEW transformation mode
    gl.glMatrixMode(GL10.GL_PROJECTION);
    gl.glOrthof(0, 10, 0, 5, 0, 1);
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
}
