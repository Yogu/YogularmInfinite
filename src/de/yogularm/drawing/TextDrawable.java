package de.yogularm.drawing;

import de.yogularm.Res;
import de.yogularm.Vector;

public class TextDrawable implements Drawable {
	//private String text;
	private Image[] images;
	
	public TextDrawable(String text) {
		//this.text = text;

		images = new Image[text.length()];
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			Image img = Res.images.numbers.get(c);
			if (img == null)
				throw new IllegalArgumentException(String.format(
					"Argument text contains the character %s, which is not included in the numbers texture.", c));
			images[i] = img;
		}
	}

	@Override
	public void draw(RenderContext context) {
		context.beginTransformation();
		for (int i = 0; i < images.length; i++) {
			images[i].draw(context);
			context.translate(new Vector(1, 0));
		}
		context.endTransformation();
	}

	@Override
	public void update(float elapsedTime) {
		
	}
	
	public static void draw(RenderContext context, String text, float x, float y, float size) {
		TextDrawable d = new TextDrawable(text);
		RenderTransformation.draw(context, d, x, y, size, size);
	}
}
