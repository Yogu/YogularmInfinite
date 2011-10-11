package de.yogularm.desktop;

import java.util.Set;

import com.jogamp.opengl.util.awt.TextRenderer;

import de.yogularm.drawing.Font;
import de.yogularm.drawing.FontStyle;

public class FontImpl implements Font {
	private java.awt.Font font;
	private TextRenderer renderer;
	
	public FontImpl(int size, Set<FontStyle> style) {
		int s = 0;
		if (style.contains(FontStyle.BOLD))
			s |= java.awt.Font.BOLD;
		if (style.contains(FontStyle.ITALIC))
			s |= java.awt.Font.ITALIC;
		
		this.font = new java.awt.Font("Verdana", s, size);
		this.renderer = new TextRenderer(font);
	}
	
	public TextRenderer getRenderer() {
		return renderer;
	}
}
