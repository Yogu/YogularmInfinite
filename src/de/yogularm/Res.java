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
	public static final Animations animations = new Animations();
	
	public static void init() {
		try {
			textures.load();
			images.load();
			animations.load();
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
		public Image ladder;
		public Image yogu;
		public Image yoguWalkingL1;
		public Image yoguWalkingL2;
		public Image yoguWalkingR1;
		public Image yoguWalkingR2;
		public Image yoguFalling;
		
		public void load() {
			TiledImage tiles = new TiledImage(Res.textures.blocks, 4, 4);
			
			stone =   tiles.get(0, 0);
			ladder =  tiles.get(1, 0);
			bricks =  tiles.get(2, 0);
			coin =    tiles.get(0, 2);
			chicken = tiles.get(1, 2);
			heart =   tiles.get(2, 2);
			arrow =   tiles.get(3, 2);
			shooter = tiles.get(0, 3);

			tiles = new TiledImage(Res.textures.yogu, 3, 3);
			yogu = tiles.get(0, 0);
			yoguWalkingL1 = tiles.get(1, 0);
			yoguWalkingL2 = tiles.get(2, 0);
			yoguWalkingR1 = tiles.get(0, 1);
			yoguWalkingR2 = tiles.get(1, 1);
			yoguFalling = tiles.get(2, 1);
		}
	}
	
	public static class Animations {
		public Animation yoguWalking;
		
		public void load() {
			yoguWalking = new Animation(new Image[] {
				Res.images.yogu, Res.images.yoguWalkingL1, Res.images.yoguWalkingL2,
				Res.images.yoguWalkingL1, Res.images.yogu, Res.images.yoguWalkingR1,
				Res.images.yoguWalkingR2, Res.images.yoguWalkingR1, Res.images.yogu },
				0.1f);
		}
	}
}
