package de.yogularm.android;

import android.content.Context;
import android.opengl.GLSurfaceView;

class HelloOpenGLES10SurfaceView extends GLSurfaceView {
	public HelloOpenGLES10SurfaceView(Context context) {
		super(context);

		// Set the Renderer for drawing on the GLSurfaceView
		setRenderer(new YogularmRenderer(context));
	}
}
