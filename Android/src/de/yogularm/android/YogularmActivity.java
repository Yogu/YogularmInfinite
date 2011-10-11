package de.yogularm.android;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import de.yogularm.Config;
import de.yogularm.ExceptionHandler;
import de.yogularm.Game;

public class YogularmActivity extends Activity {
	private GLSurfaceView surfaceView;
	
	public static final float CONTROL_SIZE = 0.5f;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		Config.MAX_VIEW_WIDTH = 18;
		Config.MAX_VIEW_HEIGHT = 10;
		Config.SCROLL_BUFFER = 1/16f;
		
		Game game = new Game();
		InputImpl input = new InputImpl();
		game.setInput(input);
		
		YogularmRenderer renderer = new YogularmRenderer(game);

		// Create a GLSurfaceView instance and set it
		// as the ContentView for this Activity.
		surfaceView = new YogularmSurfaceView(this, renderer, input);
		setContentView(surfaceView);

		renderer.setExceptionHandler(new ExceptionHandler() {
			public void handleException(Throwable e) {
				YogularmActivity.this.handleException(e);
			}
		});
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

	private void handleException(Throwable exception) {
		Toast.makeText(this, "Error: " + exception.getMessage(), Toast.LENGTH_LONG).show();
		finish();
	}
}