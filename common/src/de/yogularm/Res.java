package de.yogularm;

import java.util.HashMap;
import java.util.Map;

import de.yogularm.drawing.Animation;
import de.yogularm.drawing.Image;
import de.yogularm.drawing.Texture;
import de.yogularm.drawing.TiledImage;

public class Res {
	public static final Textures textures = new Textures();
	public static final Images images = new Images();
	public static final Animations animations = new Animations();

	static {
		images.load();
		animations.load();
	}

	public static class Textures {
		public Texture blocks = Texture.getTexture("blocks");
		public Texture yogu = Texture.getTexture("yogu");
		public Texture chicken = Texture.getTexture("chicken");
		public Texture ui = Texture.getTexture("ui");
		public Texture numbers = Texture.getTexture("numbers");
	}

	public static class Images {
		public Image arrow;
		public Image bricks;
		public Image coin;
		public Image checkpoint;
		public Image heart;
		public Image shooter;
		public Image stone;
		public Image ladder;
		public Image platform;
		public Image platformPropeller;

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

		public Image arrowKey;

		public Map<java.lang.Character, Image> numbers;

		public void load() {
			TiledImage tiles = new TiledImage(Res.textures.blocks, 4, 4);

			stone = tiles.get(0, 0);
			ladder = tiles.get(1, 0);
			bricks = tiles.get(2, 0);
			platform = tiles.get(0, 1);
			platformPropeller = tiles.get(1, 1);
			coin = tiles.get(0, 2);
			checkpoint = tiles.get(1, 2);
			heart = tiles.get(2, 2);
			arrow = tiles.get(3, 2);
			shooter = tiles.get(0, 3);

			tiles = new TiledImage(Res.textures.yogu, 4, 2);
			yogu = tiles.get(0, 0);
			yoguFalling = tiles.get(0, 1);
			yoguWalkingLeft1 = tiles.get(1, 0);
			yoguWalkingLeft2 = tiles.get(1, 1);
			yoguWalkingRight1 = tiles.get(2, 0);
			yoguWalkingRight2 = tiles.get(2, 1);

			tiles = new TiledImage(Res.textures.chicken, 4, 4);
			chicken = tiles.get(0, 0);
			chickenWalkingLeft1 = tiles.get(0, 1);
			chickenWalkingLeft2 = tiles.get(0, 2);
			chickenWalkingRight1 = tiles.get(1, 1);
			chickenWalkingRight2 = tiles.get(1, 2);
			chickenFluttering1 = tiles.get(2, 1);
			chickenFluttering2 = tiles.get(2, 2);
			chickenExploding1 = tiles.get(0, 3);
			chickenExploding2 = tiles.get(1, 3);
			chickenExploding3 = tiles.get(2, 3);
			chickenExploding4 = tiles.get(3, 3);

			tiles = new TiledImage(Res.textures.ui, 1, 1);
			arrowKey = tiles.get(0, 0);

			tiles = new TiledImage(Res.textures.numbers, 4, 4);
			numbers = new HashMap<java.lang.Character, Image>();
			numbers.put('1', tiles.get(0, 0));
			numbers.put('2', tiles.get(1, 0));
			numbers.put('3', tiles.get(2, 0));
			numbers.put('4', tiles.get(0, 1));
			numbers.put('5', tiles.get(1, 1));
			numbers.put('6', tiles.get(2, 1));
			numbers.put('7', tiles.get(0, 2));
			numbers.put('8', tiles.get(1, 2));
			numbers.put('9', tiles.get(2, 2));
			numbers.put('.', tiles.get(0, 3));
			numbers.put('0', tiles.get(1, 3));
			numbers.put(',', tiles.get(2, 3));
			numbers.put('_', tiles.get(3, 0));
			numbers.put('-', tiles.get(3, 1));
			numbers.put('+', tiles.get(3, 2));
			numbers.put('*', tiles.get(3, 3));
		}
	}

	public static class Animations {
		public Animation yoguWalking;
		public Animation chickenWalking;
		public Animation chickenFluttering;
		public Animation chickenExploding;

		public void load() {
			yoguWalking =
			  new Animation(
			    new Image[] { Res.images.yogu, Res.images.yoguWalkingLeft1, Res.images.yoguWalkingLeft2,
			      Res.images.yoguWalkingLeft1, Res.images.yogu, Res.images.yoguWalkingRight1,
			      Res.images.yoguWalkingRight2, Res.images.yoguWalkingRight1 }, 0.1f);

			chickenWalking =
			  new Animation(new Image[] { Res.images.chicken, Res.images.chickenWalkingLeft1,
			    Res.images.chickenWalkingLeft2, Res.images.chickenWalkingLeft1, Res.images.chicken,
			    Res.images.chickenWalkingRight1, Res.images.chickenWalkingRight2,
			    Res.images.chickenWalkingRight1 }, 0.05f);

			chickenFluttering =
			  new Animation(new Image[] { Res.images.chicken, Res.images.chickenFluttering1,
			    Res.images.chicken, Res.images.chickenFluttering2 }, 0.05f);

			chickenExploding =
			  new Animation(new Image[] { Res.images.chickenExploding1, Res.images.chickenExploding2,
			    Res.images.chickenExploding3, Res.images.chickenExploding4 }, 0.05f);
		}
	}
}
