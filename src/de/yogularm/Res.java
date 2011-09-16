package de.yogularm;

import java.io.IOException;
import java.io.InputStream;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class Res {
	public static final Textures textures = new Textures();
	public static final Images images = new Images();
	
	public static void init() {
		try {
			textures.load();
			images.load();
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
	
	public static class Images {
		public Image arrow;
		public Image bricks;
		public Image chicken;
		public Image coin;
		public Image heart;
		public Image shooter;
		public Image stone;
		public Image yogu;
		public Image ladder;
		
		public void load() {
			TiledImage tiles = new TiledImage(Res.textures.blocks, 4);
			
			stone =   tiles.get(0, 0);
			ladder =  tiles.get(1, 0);
			bricks =  tiles.get(2, 0);
			coin =    tiles.get(0, 2);
			chicken = tiles.get(1, 2);
			heart =   tiles.get(2, 2);
			arrow =   tiles.get(3, 2);
			shooter = tiles.get(0, 3);
			yogu = new Image(Res.textures.yogu);
		}
	}
}
