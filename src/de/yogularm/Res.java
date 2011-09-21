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
		public Texture chicken;
		
		public void load() throws GLException, IOException {
			blocks = loadTexture("blocks");
			yogu = loadTexture("yogu");
			chicken = loadTexture("chicken");
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
		public Image coin;
		public Image heart;
		public Image shooter;
		public Image stone;
		public Image ladder;
		public Image yogu;
		public Image yoguWalkingLeft1;
		public Image yoguWalkingLeft2;
		public Image yoguWalkingRight1;
		public Image yoguWalkingRight2;
		public Image yoguFalling;
		public Image chicken;
		public Image chickenWalkingLeft1;
		public Image chickenWalkingLeft2;
		public Image chickenWalkingRight1;
		public Image chickenWalkingRight2;
		public Image chickenFluttering1;
		public Image chickenFluttering2;
		public Image chickenExploding1;
		public Image chickenExploding2;
		public Image chickenExploding3;
		public Image chickenExploding4;
		
		public void load() {
			TiledImage tiles = new TiledImage(Res.textures.blocks, 4, 4);
			
			stone =   tiles.get(0, 0);
			ladder =  tiles.get(1, 0);
			bricks =  tiles.get(2, 0);
			coin =    tiles.get(0, 2);
			heart =   tiles.get(2, 2);
			arrow =   tiles.get(3, 2);
			shooter = tiles.get(0, 3);

			tiles = new TiledImage(Res.textures.yogu, 3, 3);
			yogu = tiles.get(0, 0);
			yoguWalkingLeft1 = tiles.get(1, 0);
			yoguWalkingLeft2 = tiles.get(2, 0);
			yoguWalkingRight1 = tiles.get(0, 1);
			yoguWalkingRight2 = tiles.get(1, 1);
			yoguFalling = tiles.get(2, 1);

			tiles = new TiledImage(Res.textures.chicken, 4, 4);
			chicken = tiles.get(1, 1);
			chickenWalkingLeft1 = tiles.get(1, 1);
			chickenWalkingLeft2 = tiles.get(2, 1);
			chickenWalkingRight1 = tiles.get(0, 2);
			chickenWalkingRight2 = tiles.get(1, 2);
			chickenFluttering1 = tiles.get(2, 2);
			chickenFluttering2 = tiles.get(0, 3);
			chickenExploding1 = tiles.get(0, 0);
			chickenExploding2 = tiles.get(1, 0);
			chickenExploding3 = tiles.get(2, 0);
			chickenExploding4 = tiles.get(3, 0);
		}
	}
	
	public static class Animations {
		public Animation yoguWalking;
		public Animation chickenWalking;
		public Animation chickenFluttering;
		public Animation chickenExploding;
		
		public void load() {
			yoguWalking = new Animation(new Image[] {
				Res.images.yogu, Res.images.yoguWalkingLeft1, Res.images.yoguWalkingLeft2,
				Res.images.yoguWalkingLeft1, Res.images.yogu, Res.images.yoguWalkingRight1,
				Res.images.yoguWalkingRight2, Res.images.yoguWalkingRight1, Res.images.yogu },
				0.1f);

			chickenWalking = new Animation(new Image[] {
				Res.images.chicken, Res.images.chickenWalkingLeft1, Res.images.chickenWalkingLeft2,
				Res.images.chickenWalkingLeft1, Res.images.chicken, Res.images.chickenWalkingRight1,
				Res.images.chickenWalkingRight2, Res.images.chickenWalkingRight1, Res.images.chicken },
				0.05f);

			chickenFluttering = new Animation(new Image[] {
				Res.images.chicken, Res.images.chickenFluttering1, Res.images.chicken,
				Res.images.chickenFluttering2 }, 0.05f);

			chickenExploding = new Animation(new Image[] {
				Res.images.chickenExploding1, Res.images.chickenExploding2,
				Res.images.chickenExploding3, Res.images.chickenExploding4}, 0.05f);
		}
	}
}
