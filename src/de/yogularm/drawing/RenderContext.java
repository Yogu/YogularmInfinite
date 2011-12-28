package de.yogularm.drawing;

import de.yogularm.geometry.Rect;
import de.yogularm.geometry.Vector;

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
	
	/**
	 * Deletes all native resources
	 * 
	 * This object should be re-usable after dispose() is called
	 */
	void dispose();
}
