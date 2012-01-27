package de.yogularm.drawing;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractRenderContext implements RenderContext {
	private Map<Texture, Integer> textures = new HashMap<Texture, Integer>();
	private Map<Font, Object> fonts = new HashMap<Font, Object>();

	protected int getTextureID(Texture texture) {
		if (textures.containsKey(texture))
			return textures.get(texture);
		else {
			int tex = loadTexture(texture);
			textures.put(texture, tex);
			return tex;
		}
	}
	
	private int loadTexture(Texture texture) {
		InputStream stream =
			getClass().getResourceAsStream("/de/yogularm/res/textures/" + texture.getName() + ".png");
		try {
			return loadTextureFromStream(stream, texture);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public void dispose() {
		destroyTextures();
		destroyFonts();
	}
	
	private void destroyTextures() {
		for (int id : textures.values()) {
			destroyTexture(id);
		}
		textures.clear();
	}
	
	protected abstract void destroyTexture(int id);
	protected abstract int loadTextureFromStream(InputStream stream, Texture texture);

	protected Object getFontObject(Font font) {
		if (fonts.containsKey(font))
			return fonts.get(font);
		else {
			Object obj = loadFont(font);
			fonts.put(font, obj);
			return obj;
		}
	}
	
	private void destroyFonts() {
		for (Object obj : fonts.values()) {
			destroyFont(obj);
		}
		textures.clear();
	}
	
	protected abstract void destroyFont(Object fontObject);
	protected abstract Object loadFont(Font font);
}