package de.yogularm.drawing;

public class Renderer {
	public static void render(RenderContext context, Renderable renderable) {
		Drawable drawable = renderable.getDrawable();
		if (drawable != null) {
			// Initialize color for the case that no ColoredDrawable is used
			context.setColor(Color.white);
			
			context.beginTransformation();
				context.translate(renderable.getPosition());
				drawable.draw(context);
			context.endTransformation();
		}
	}
}
