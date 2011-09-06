package de.yogularm;

import java.io.IOException;
import java.io.InputStream;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class Res {
	public static final Textures textures = new Textures();
	
	public static void init() {
		try {
			textures.load();
		} catch (Exception e) {
			throw new RuntimeException ("Failed to load resources", e);
		}
	}
	
	public static class Textures {
		public Texture blocks;
		public Texture yogu;
		
		public void load() throws GLException, IOException {
			blocks = loadTexture("blocks");
			yogu = loadTexture("yogu");
		}

		private Texture loadTexture(String name) throws GLException, IOException {
			InputStream stream = getClass().getResourceAsStream("/res/textures/" + name + ".png");
			Texture texture =  TextureIO.newTexture(stream, false, "png");
			texture.setTexParameteri(GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
			texture.setTexParameteri(GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);

			return texture;
		}
	}
}
