package propra2012.gruppe33.graphics.rendering.active;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import propra2012.gruppe33.graphics.rendering.passive.JSceneRenderer;
import propra2012.gruppe33.graphics.rendering.scenegraph.Scene;
import propra2012.gruppe33.graphics.rendering.util.ImageUtil;

/**
 * This class supports active rendering which is the most performant way to
 * render if you use java2d.
 * 
 * Of course active rendering has its price:
 * 
 * The nice Swing layout system and the input system could cause some issues.
 * (Mixing Swing and AWT is never a good idea!)
 * 
 * @author Christopher Probst
 * @see JSceneRenderer
 */
public class SceneProcessor<S extends Scene> extends Canvas {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * Used for tasks which should be processed in the main thread.
	 */
	private BlockingQueue<Runnable> tasks = new LinkedBlockingDeque<Runnable>();

	/*
	 * This is the buffer strategy for rendering.
	 */
	private BufferStrategy bufferStrategy = null;

	/*
	 * The fast offscreen image. Ultimate hardware performance. There is no
	 * better way to render graphics in java2d.
	 */
	private BufferedImage offscreen;

	/*
	 * The scene which we want to render.
	 */
	private final S root;

	/*
	 * Used for timed rendering.
	 */
	private long curTime, lastTime = -1;

	/*
	 * Thread safe vars for rendering.
	 */
	private volatile int peerWidth, peerHeight;

	/**
	 * 
	 * @param root
	 */
	public SceneProcessor(S root) {
		if (root == null) {
			throw new NullPointerException("root");
		}

		// Save the root
		this.root = root;

		// The canvas is rendered by us!
		setIgnoreRepaint(true);

		// This canvas can receive input
		setFocusable(true);

		// Connect the input
		root.connectTo(this);

		// Add resize listener
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				// Threading issues...
				peerWidth = getWidth();
				peerHeight = getHeight();
			}
		});
	}

	/**
	 * @return the task queue.
	 */
	public BlockingQueue<Runnable> getTasks() {
		return tasks;
	}

	/**
	 * Resets the time.
	 */
	public void resetTime() {
		lastTime = -1;
	}

	/**
	 * 
	 * @return the root scene.
	 */
	public S getRoot() {
		return root;
	}

	/**
	 * Process the whole scene.
	 * 
	 * @param maxFPS
	 *            The maximum frames per second. A value < 1 means no max.
	 */
	public void process(int maxFPS) {

		// Do the time stuff...
		if (curTime == -1) {
			lastTime = System.currentTimeMillis();
		} else {
			lastTime = curTime;
		}

		// Get active time
		curTime = System.currentTimeMillis();

		// Read into local cache
		int w = peerWidth, h = peerHeight;

		// Check for null
		if (bufferStrategy == null) {
			// Double buffered should be fine
			createBufferStrategy(2);

			// Create the strategy
			bufferStrategy = getBufferStrategy();
		}

		// Check size!
		if (w > 0 && h > 0) {
			/*
			 * Advanced volatile image rendering.
			 */
			// Create new...
			if (offscreen == null || offscreen.getWidth() != w
					|| offscreen.getHeight() != h) {

				// Create new compatible image for fast rendering
				offscreen = ImageUtil.createImage(w, h);
			}

			// Create new graphics
			Graphics2D g2d = offscreen.createGraphics();
			try {

				// Black is a good background
				g2d.setBackground(Color.BLACK);
				g2d.clearRect(0, 0, w, h);

				// Tmp var
				Runnable task;

				// Process all tasks
				while ((task = tasks.poll()) != null) {
					task.run();
				}

				// Simulate the scene
				root.simulate(g2d, w, h, curTime - lastTime);
			} finally {
				// Dispose
				g2d.dispose();
			}

			// Get the graphics context
			Graphics frameBuffer = bufferStrategy.getDrawGraphics();

			try {

				// Always clear
				frameBuffer.setColor(Color.black);
				frameBuffer.fillRect(0, 0, w, h);

				// Render the offscreen
				frameBuffer.drawImage(offscreen, 0, 0, null);
			} finally {
				// Dispose the buffer
				frameBuffer.dispose();
			}

			// Render to frame if not lost
			if (!bufferStrategy.contentsLost()) {
				bufferStrategy.show();
			}

			// Synchronize with toolkit
			Toolkit.getDefaultToolkit().sync();
		} else {
			// Simulate the scene
			root.simulate(null, -1, -1, curTime - lastTime);
		}

		// Check!
		if (maxFPS > 0) {
			// Calc the frame duration
			int frameDuration = (int) (System.currentTimeMillis() - curTime);

			// Calc the minimum duration
			int minDuration = 1000 / maxFPS;

			// Wait a bit
			if (frameDuration < minDuration) {
				LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(minDuration
						- frameDuration));
			}
		}
	}
}
