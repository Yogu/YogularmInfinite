package de.yogularm;

public class Config {
	// Display
	public final static float MAX_VIEW_WIDTH = 38;
	public final static float MAX_VIEW_HEIGHT = 18;
	public final static float MIN_RESOLUTION = 50;
	public final static float SCROLL_SPEED = 2; // scroll meter/second per meter distance
	public final static float SCROLL_OFFSET = 4; // distance of player from center to begin scrolling
	public final static float SCROLL_MIN_BUFFER = 5; // minimum target distance from player to window border
	public final static float GAMEOVER_LENGTH = 3; // [s]

	// Physics
	public static final float PLAYER_SPEED = 4;
	//public static final float PLAYER_ACCELERATION = 20;
	public static final float PLAYER_JUMP_SPEED = 5;
	public static final float GRAVITY_ACCELERATION = 9.81f;
	public static final float ARROW_SPEED = 20f;
	public static final float ADHESION = 10;
	public static final float AIR_ADHESION = 1;
	public static final float EPSILON = 0.00001f;
	
	// Gameplay
	public static final float MAX_FRAMETIME = 0.1f;
	public static final float DEATH_FALL_SPEED = 20;
	public static final float MAX_LIFE = 3;
	public static final float IMMUNE_TIME = 2;
}
