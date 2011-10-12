package de.yogularm.android;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.text.method.MovementMethod;
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
		float div = 1 / YogularmActivity.CONTROL_DISPLAY_HORIZONTAL_DIVISION;
		
		int upIndex;
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			upIndex = event.getActionIndex();
			break;
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
		case MotionEvent.ACTION_MOVE:
			upIndex = -1;
			break;
		default:
			return false;
		}

		input.setIsLeft(false);
		input.setIsRight(false);
		input.setIsDown(false);
		input.setIsUp(false);
		for (int i = 0; i < event.getPointerCount(); i++) {
			// exclude just released pointers
			if (i != upIndex) {
				float x = event.getX(i) / (float) getWidth();
				float y = 1 - event.getY(i) / (float) getHeight();

				if (y < YogularmActivity.CONTROL_SIZE) {
					if (x < div)
						input.setIsUp(true);
					else if (x < 2 * div)
						input.setIsDown(true);
					else if (x > 1 - 2 * div) {
						if (x < 1 - div)
							input.setIsLeft(true);
						else
							input.setIsRight(true);
					}
				}
			}
		}
		return true;
	}
}
