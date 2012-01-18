package de.yogularm.android;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import de.yogularm.geometry.Vector;

public class YogularmSurfaceView extends GLSurfaceView {
	private InputImpl input;
	
	private static boolean ENABLE_ACCELERATION_CONTROL = false;
	private static float MIN_ACCELERATION = 0.3f;//0.8f;
	private static float MAX_ACCELERATION = 1f;
	private static float JUMP_ANGLE_SPEED = 180; // [°/s]

	public YogularmSurfaceView(Context context, Renderer renderer, InputImpl input) {
		super(context);

		// Set the Renderer for drawing on the GLSurfaceView
		setRenderer(renderer);
		this.input = input;


		if (ENABLE_ACCELERATION_CONTROL)
			initAccelerationControl();
	}
	
	private void initAccelerationControl() {
		AccelerometerManager acceleration = new AccelerometerManager(getContext());
		if (acceleration.isSupported()) {
			acceleration.startListening(new AccelerometerListener() {
				private float lastAngle;
				private long lastTime;
				private boolean wasJumping;
				
				public void onShake(float force) {
				}

				@Override
				public void onAccelerationChanged(float x, float y, float z) {
					YogularmSurfaceView.this.input.setX(makeDirection(y));

					if (wasJumping) {
						YogularmSurfaceView.this.input.setY(0);
						wasJumping = false;
					}
					float angle = new Vector(x, z).getAngleToXAxis();
					if (lastTime > 0) {
						float speed = (angle - lastAngle) / ((System.currentTimeMillis() - lastTime) / 1000f);
						if (speed >= JUMP_ANGLE_SPEED) {
							YogularmSurfaceView.this.input.setY(1);
							wasJumping = true;
						}
					}
					lastTime = System.currentTimeMillis();
					lastAngle = angle;
					
					//YogularmSurfaceView.this.input.setIsUp(z > ACCELERATION_EPSILON);
					//YogularmSurfaceView.this.input.setIsDown(z < -ACCELERATION_EPSILON);
				}
			});
		}
	}
	
	private float makeDirection(float acceleration) {
		float sign = Math.signum(acceleration);
		acceleration = Math.max(0, Math.abs(acceleration) - MIN_ACCELERATION);
		acceleration /= (MAX_ACCELERATION - MIN_ACCELERATION);
		acceleration = Math.min(1, acceleration);
		return acceleration * sign;
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

		input.setX(0);
		input.setY(0);
		for (int i = 0; i < event.getPointerCount(); i++) {
			// exclude just released pointers
			if (i != upIndex) {
				float x = event.getX(i) / (float) getWidth();
				float y = 1 - event.getY(i) / (float) getHeight();

				if (y < YogularmActivity.CONTROL_SIZE) {
					if (x < div)
						input.setY(1);
					else if (x < 2 * div)
						input.setY(-1);
					else if (x > 1 - 2 * div) {
						if (x < 1 - div)
							input.setX(-1);
						else
							input.setX(1);
					}
				}
			}
		}
		return true;
	}
}
