package de.yogularm.drawing;

import java.util.Set;

import de.yogularm.Rect;
import de.yogularm.Vector;

public interface RenderContext {
	void setColor(Color color);
	void bindTexture(Texture texture);
	void unbindTexture();
	
	void drawRect(Rect bounds);
	void drawRect(Rect bounds, Rect textureBounds);
	void drawLines(Vector[] coords, float lineWidth, boolean doStrip);
	void drawText(Vector position, Font font, String text);
	
	void resetTranformation();
	void beginTransformation();
	void endTransformation();
	void translate(Vector offset);
	void scale(Vector factor);
	void rotate(float angle);
	
	void clear(Color clearColor);
	void setProjection(float width, float height);
	
	Font loadFont(int size, Set<FontStyle> style);
	
	/**
	 * Deletes all native resources
	 * 
	 * This object should be re-usable after dispose() is called
	 */
	void dispose();
}
