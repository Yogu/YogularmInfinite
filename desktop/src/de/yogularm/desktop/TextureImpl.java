package de.yogularm.desktop;

import de.yogularm.drawing.Texture;

public class TextureImpl implements Texture {
	private com.jogamp.opengl.util.texture.Texture tex;

	public TextureImpl(com.jogamp.opengl.util.texture.Texture tex) {
		this.tex = tex;
	}

	@Override
	public void bind() {
		tex.bind();
	}

	@Override
	public int getWidth() {
		return tex.getWidth();
	}

	@Override
	public int getHeight() {
		return tex.getHeight();
	}
}
