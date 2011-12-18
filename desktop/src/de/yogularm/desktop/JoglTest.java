package de.yogularm.desktop;

import java.awt.BorderLayout;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import com.jogamp.opengl.util.FPSAnimator;

import de.yogularm.ExceptionHandler;
import de.yogularm.Rect;
import de.yogularm.Res;
import de.yogularm.drawing.Texture;

public class JoglTest {
	public static void main(String[] args) {
		System.out.println("Creating canvas");
		JFrame window = new JFrame("Yogularm Infinite");
		window.setSize(200, 100);
		window.setVisible(true);
		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities(glprofile);
		GLCanvas canvas = new GLCanvas(glcapabilities);
		window.getContentPane().add(canvas, BorderLayout.CENTER);
		System.out.println("Creating eventl istener");
		GLEventListenerImpl eventListener = new GLEventListenerImpl();
		canvas.addGLEventListener(eventListener);
		System.out.println("Starting game");
		eventListener.start(canvas);
	}
	
	public static class GLEventListenerImpl implements GLEventListener {
		private FPSAnimator animator;
		private RenderContextImpl context;
		private ExceptionHandler exceptionHandler;
		
		public GLEventListenerImpl() {
		}

		public void init(GLAutoDrawable drawable) {
			try {
				System.out.println("Initializing...");
				GL2 gl = drawable.getGL().getGL2();

				context = new RenderContextImpl(gl);
				
				gl.glDisable(GL.GL_DEPTH_TEST);
				gl.glDisable(GL.GL_CULL_FACE);
				gl.glEnable(GL.GL_TEXTURE_2D);
				gl.glEnable(GL.GL_BLEND);
				gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
				context.checkErrors();
				System.out.println("Initialized");
			} catch (Exception e) {
				if (exceptionHandler != null)
					exceptionHandler.handleException(e);
			}
		}

		public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
			try {
				System.out.println("Reshaping...");
				context.setProjection(10, 10);
				GL2 gl = drawable.getGL().getGL2();
				gl.glViewport(0, 0, width, height);
				context.checkErrors();
				System.out.println("Reshaped");
			} catch (Exception e) {
				if (exceptionHandler != null)
					exceptionHandler.handleException(e);
			}
		}

		public void dispose(GLAutoDrawable drawable) {

		}

		public void display(GLAutoDrawable drawable) {
			try {
				render();
			} catch (Exception e) {
				if (exceptionHandler != null)
					exceptionHandler.handleException(e);
			}
		}

		public void start(GLAutoDrawable drawable) {
			try {
				System.out.println("Starting...");
				animator = new FPSAnimator(drawable, 60);
				animator.add(drawable);
				animator.start();
				System.out.println("Started");
			} catch (Exception e) {
				if (exceptionHandler != null)
					exceptionHandler.handleException(e);
			}
		}

		public void stop() {
			try {
				System.out.println("Stopping...");
				animator.stop();
				System.out.println("Stopped");
			} catch (Exception e) {
				if (exceptionHandler != null)
					exceptionHandler.handleException(e);
			}
		}

		public void setExceptionHandler(ExceptionHandler handler) {
			this.exceptionHandler = handler;
		}
		
		private void render() {
			context.bindTexture(Res.textures.blocks);
			context.drawRect(new Rect(0, 0, 1, 1));
		}
	}

}
