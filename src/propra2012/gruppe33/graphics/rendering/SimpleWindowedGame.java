package propra2012.gruppe33.graphics.rendering;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;

import propra2012.gruppe33.PreMilestoneApp;
import propra2012.gruppe33.assets.AssetManager;
import propra2012.gruppe33.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.graphics.rendering.scenegraph.grid.Grid;
import propra2012.gruppe33.routines.AnimationRoutines;
import propra2012.gruppe33.routines.PlayerRoutines;

public class SimpleWindowedGame {

	public static void main(String[] args) throws Exception {

		// Create game window...
		JFrame app = new JFrame();
		app.setIgnoreRepaint(true);
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create canvas for painting...
		Canvas canvas = new Canvas();
		canvas.setIgnoreRepaint(true);
		canvas.setSize(800, 600);

		// Add canvas to game window...
		app.add(canvas);
		app.pack();
		app.setVisible(true);

		// Create BackBuffer...
		canvas.createBufferStrategy(2);
		BufferStrategy buffer = canvas.getBufferStrategy();

		// Get graphics configuration...
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gd.getDefaultConfiguration();

		// Create off-screen drawing surface
		BufferedImage bi = gc.createCompatibleImage(1400, 900);

		// Objects needed for rendering...
		Graphics graphics = null;
		Graphics2D g2d = null;
		Color background = Color.BLACK;
		Random rand = new Random();

		// Create the demo grid
		Grid grid = PreMilestoneApp.createDemoGame();

		grid.connectTo(canvas);

		// Variables for counting frames per seconds
		int fps = 0;
		int frames = 0;
		long totalTime = 0;
		long curTime = System.currentTimeMillis();
		long lastTime = curTime;

		while (true) {
			try {
				// count Frames per second...
				lastTime = curTime;
				curTime = System.currentTimeMillis();
				totalTime += curTime - lastTime;
				if (totalTime > 1000) {
					totalTime -= 1000;
					fps = frames;
					frames = 0;
				}
				++frames;

				// clear back buffer...
				g2d = bi.createGraphics();
				g2d.setColor(background);
				g2d.fillRect(0, 0, 1400, 900);

				// Simulate the grid
				grid.simulate(g2d, 1400, 900, curTime - lastTime);

				// display frames per second...
				g2d.setFont(new Font("Courier New", Font.PLAIN, 12));
				g2d.setColor(Color.GREEN);
				g2d.drawString(String.format("FPS: %s", fps), 20, 20);

				// Blit image and flip...
				graphics = buffer.getDrawGraphics();
				graphics.drawImage(bi, 0, 0, null);
				if (!buffer.contentsLost())
					buffer.show();

				// Let the OS have a little time...
				Thread.yield();
			} finally {
				// release resources
				if (graphics != null)
					graphics.dispose();
				if (g2d != null)
					g2d.dispose();
			}
		}
	}
}