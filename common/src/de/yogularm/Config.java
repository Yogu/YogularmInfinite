package de.yogularm;

public class Config {
	// Display
	public static float MAX_VIEW_WIDTH = 25;
	public static float MAX_VIEW_HEIGHT = 15;
	public static float SCROLL_SPEED = 2; // scroll meter/second per meter distance
	public static float GAMEOVER_LENGTH = 3; // [s]
	//the player can move in this fraction of the screen without causing the camera to scroll
	public static float SCROLL_BUFFER = 1/4f;  

	// Physics
	public static float PLAYER_SPEED = 4;
	public static float PLAYER_CLIMB_SPEED = 2;
	public static float CLIMB_ACCELERATION = 30;//20;
	//public static float PLAYER_ACCELERATION = 20;
	public static float PLAYER_JUMP_SPEED = 5.2f;
	public static float GRAVITY_ACCELERATION = 9.81f;
	public static float ARROW_SPEED = 20f;
	public static float ADHESION = 10;
	public static float AIR_ADHESION = 1.5f;//1;
	public static float EPSILON = 0.00001f;
	public static float ON_GROUND_EPSILON = 0.1f;
	
	// Gameplay
	public static float MAX_FRAMETIME = 0.1f;
	public static float DEATH_FALL_SPEED = 20;
	// public static float DEATH_Y_POS = -120;
	public static float INIT_LIFE = 3;
	public static float IMMUNE_TIME = 2;
	public static int CHECKPOINT_RANGE = 21; // every x structures a checkpoint
	
	// Debug
	public static boolean DEBUG = false;
	public static boolean DEBUG_DISPLAY_FORCES = DEBUG && false;
	public static boolean DEBUG_DISPLAY_RENDER_INFO = DEBUG && true;
	public static boolean DEBUG_BUILDING = DEBUG && true;
}
