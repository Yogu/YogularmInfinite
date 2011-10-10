package de.yogularm.android;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

public class Res {
	public static Texture yogu;
	public static Texture coin;

	public static void laod(GL10 gl, Context context) {
		//yogu = new Texture(gl, context.getResources().openRawResource(R.drawable.yogu));
		coin = new Texture(gl, context.getResources().openRawResource(R.drawable.coin));
	}
}
