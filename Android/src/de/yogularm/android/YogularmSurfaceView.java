package de.yogularm.android;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class YogularmSurfaceView extends GLSurfaceView {
	private InputImpl input;

	public YogularmSurfaceView(Context context, Renderer renderer, InputImpl input) {
		super(context);

		// Set the Renderer for drawing on the GLSurfaceView
		setRenderer(renderer);
		this.input = input;
	}

	public boolean onTouchEvent(final MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			float size = YogularmActivity.CONTROL_SIZE;
			float x = event.getX() / (float)getWidth();
			x = (x - (1 - size)) / size;
			float y = 1 - event.getY() / (float)getHeight();
			y = y / size;
			
			// don't mind about right-bottom borders
			if (x >= 0 && y <= 1.0) {
				input.setIsLeft(x <= 1.0 / 3);
				input.setIsRight(x >= 2.0 / 3);
				input.setIsDown(y <= 1.0 / 3);
				input.setIsUp(y >= 2.0 / 3);
				return true;
			}
		case MotionEvent.ACTION_UP:
			input.setIsLeft(false);
			input.setIsRight(false);
			input.setIsDown(false);
			input.setIsUp(false);
			return true;
		}
		return false;
	}
}
