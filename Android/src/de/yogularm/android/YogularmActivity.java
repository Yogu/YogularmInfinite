package de.yogularm.android;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class YogularmActivity extends Activity {
	private GLSurfaceView surfaceView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create a GLSurfaceView instance and set it
		// as the ContentView for this Activity.
		surfaceView = new HelloOpenGLES10SurfaceView(this);
		setContentView(surfaceView);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// The following call pauses the rendering thread.
		// If your OpenGL application is memory intensive,
		// you should consider de-allocating objects that
		// consume significant memory here.
		surfaceView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// The following call resumes a paused rendering thread.
		// If you de-allocated graphic objects for onPause()
		// this is a good place to re-allocate them.
		surfaceView.onResume();
	}

}