package de.yogularm.desktop;

import java.util.Set;

import de.yogularm.Rect;
import de.yogularm.Vector;
import de.yogularm.drawing.Color;
import de.yogularm.drawing.Font;
import de.yogularm.drawing.FontStyle;
import de.yogularm.drawing.RenderContext;
import de.yogularm.drawing.Texture;

public class DummyRenderContext implements RenderContext {
	public DummyRenderContext() {
	}

	public void setColor(Color color) {
	}

	public void bindTexture(Texture texture) {
	}

	public void unbindTexture() {
	}

	public void drawRect(Rect bounds) {
	}

	public void drawRect(Rect bounds, Rect textureBounds) {
	}

	public void drawLines(Vector[] coords, float lineWidth, boolean doStrip) {
	}

	public void drawText(Vector position, Font font, String text) {
	}

	public void resetTranformation() {
	}

	public void beginTransformation() {
	}

	public void endTransformation() {
	}

	public void translate(Vector offset) {
	}

	public void scale(Vector factor) {
	}

	public void rotate(float angle) {
	}

	public void clear(Color clearColor) {
	}

	public void setProjection(float width, float height) {
	}

	public void checkErrors() {
	}

	public void dispose() {
	}
}
