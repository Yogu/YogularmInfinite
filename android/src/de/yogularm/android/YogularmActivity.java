package de.yogularm.android;

import java.io.PrintWriter;
import java.io.StringWriter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.pm.ActivityInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import de.yogularm.Config;
import de.yogularm.ExceptionHandler;

public class YogularmActivity extends Activity {
	private GLSurfaceView surfaceView;

	public static final float CONTROL_SIZE = 0.3f;
	public static final float CONTROL_DISPLAY_HORIZONTAL_DIVISION = 6f;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			getWindow()
				.addFlags(
					WindowManager.LayoutParams.FLAG_FULLSCREEN
						| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);

			Config.MAX_VIEW_WIDTH = 18;
			Config.MAX_VIEW_HEIGHT = 10;
			Config.SCROLL_BUFFER = 1 / 16f;
			Config.PLAYER_JUMP_SPEED = 6;
			//Config.AIR_ADHESION = 5; // temporarily disabled for testing
			Config.CHECKPOINT_RANGE = 11; // temporarily changed for testing

			YogularmRenderer renderer = new YogularmRenderer(Controller.getGame());

			// Create a GLSurfaceView instance and set it
			// as the ContentView for this Activity.
			surfaceView = new YogularmSurfaceView(this, renderer, Controller.getInput());
			setContentView(surfaceView);

			renderer.setExceptionHandler(new ExceptionHandler() {
				public void handleException(Throwable e) {
					YogularmActivity.this.handleException(e);
				}
			});
		} catch (Exception e) {
			handleException(e);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		// The following call pauses the rendering thread.
		// If your OpenGL application is memory intensive,
		// you should consider de-allocating objects that
		// consume significant memory here.
		try {
			surfaceView.onPause();
		} catch (Exception e) {
			handleException(e);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// The following call resumes a paused rendering thread.
		// If you de-allocated graphic objects for onPause()
		// this is a good place to re-allocate them.
		try {
			surfaceView.onResume();
		} catch (Exception e) {
			handleException(e);
		}
	}

	private void handleException(final Throwable exception) {
		runOnUiThread(new Runnable() {
			public void run() {
				// Without this call the phone may crash (!)
				surfaceView.onPause();
				
				String ex = printStackToString(exception);
				AlertDialog dialog =
					createSelectableDialog("Runtime Error", "Oh! Yogu tripped over a bug.\n\nTouch long to copy this message.\n\n" + ex);
				dialog.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						YogularmActivity.this.finish();
					}
				});
				dialog.show();
			}
		});
	}

	private AlertDialog createSelectableDialog(String title, String message) {
		AlertDialog dialog;
		AlertDialog.Builder builder;
		// The TextView to show your Text
		TextView showText = new TextView(this);
		showText.setText(message);
		// Add the Listener
		showText.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// Copy the Text to the clipboard
				ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				TextView showTextParam = (TextView) v;
				manager.setText(showTextParam.getText());
				// Show a message:
				Toast.makeText(v.getContext(), "Text in clipboard", Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		// Build the Dialog
		builder = new AlertDialog.Builder(this);
		
		ScrollView scrollView = new ScrollView(this);
		scrollView.addView(showText);
		builder.setView(scrollView);
		dialog = builder.create();
		// Some eye-candy
		dialog.setTitle(title);
		dialog.setCancelable(true);
		return dialog;
	}

	private static String printStackToString(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		handleKeyEvent(keyCode, true);
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		handleKeyEvent(keyCode, false);
		return super.onKeyUp(keyCode, event);
	}
	
	private void handleKeyEvent(int keyCode, boolean pressed) {
		InputImpl input = (InputImpl)Controller.getInput();
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
		case KeyEvent.KEYCODE_A:
			input.setX(pressed ? -1 : 0);
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
		case KeyEvent.KEYCODE_D:
			input.setX(pressed ? 1 : 0);
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
		case KeyEvent.KEYCODE_S:
			input.setX(pressed ? -1 : 0);
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
		case KeyEvent.KEYCODE_W:
			input.setY(pressed ? 1 : 0);
			break;
		}
	}
}