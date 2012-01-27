package de.yogularm.drawing;

import java.util.EnumSet;
import java.util.Set;


public class Font {
	private int size;
	private Set<FontStyle> style;
	
	public Font(int size, Set<FontStyle> style) {
		if (style == null)
			style = EnumSet.noneOf(FontStyle.class);
		
		this.size = size;
		this.style = style;
	}
	
	public int getSize() {
		return size;
	}
	
	public Set<FontStyle> getStyle() {
		return style;
	}
	
	@Override
	public int hashCode() {
		return size ^ style.hashCode();
	}
	
	public boolean equals(Font other) {
		return other != null && other.size == size && other.style.equals(style);
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof Font && equals((Font)other);
	}
}
