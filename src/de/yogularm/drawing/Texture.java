package de.yogularm.drawing;

import java.util.HashMap;
import java.util.Map;

public class Texture {
	private String textureName;
	
	private static Map<String, Texture> textures = new HashMap<String, Texture>();
	
	public static Texture getTexture(String textureName) {
		if (textures.containsKey(textureName))
			return textures.get(textureName);
		else {
			Texture texture = new Texture(textureName);
			textures.put(textureName, texture);
			return texture;
		}
	}
	
	private Texture(String textureName) {
		this.textureName = textureName;
	}
	
	public String getName() {
		return textureName;
	}
}
