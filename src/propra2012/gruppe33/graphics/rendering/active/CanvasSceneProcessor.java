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

import propra2012.gruppe33.graphics.rendering.scenegraph.Scene;
import propra2012.gruppe33.graphics.rendering.scenegraph.SceneProcessor;
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
 * @see SceneProcessor
 */
public final class CanvasSceneProcessor<S extends Scene> extends Canvas
		implements SceneProcessor<S> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * Used for tasks which should be processed in the main thread.
	 */
	private BlockingQueue<Runnable> tasks = new LinkedBlockingDeque<Runnable>();

	/*
	 * This is the thread-safe buffer strategy for rendering.
	 * 
	 * VOLATILE: The var is set in the EDT.
	 */
	private volatile BufferStrategy bufferStrategy;

	/*
	 * The fast offscreen image. Ultimate hardware performance. There is no
	 * better way to render graphics in java2d.
	 */
	private BufferedImage offscreen;

	/*
	 * The shutdown requested flag.
	 */
	private boolean shutdownRequested = false;

	/*
	 * The scene which we want to render.
	 */
	private S root;

	/*
	 * Used for timed rendering.
	 */
	private long curTime, lastTime = -1;

	/*
	 * Thread safe vars for rendering.
	 * 
	 * VOLATILE: These vars are set in the EDT.
	 */
	private volatile int peerWidth, peerHeight;

	/**
	 * Creates a new canvas scene processor.
	 */
	public CanvasSceneProcessor() {

		// The canvas is rendered by us!
		setIgnoreRepaint(true);

		// This canvas can receive input
		setFocusable(true);

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * propra2012.gruppe33.graphics.rendering.scenegraph.SceneProcessor#setRoot
	 * (propra2012.gruppe33.graphics.rendering.scenegraph.Scene)
	 */
	public void setRoot(S root) {

		// Remove old link
		if (this.root != null) {
			this.root.disconnectFrom(this);
		}

		// Save the root
		this.root = root;

		// Connect the input
		root.connectTo(this);

		// The time must be resetted.
		resetTime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * propra2012.gruppe33.graphics.rendering.scenegraph.SceneProcessor#getTasks
	 * ()
	 */
	@Override
	public BlockingQueue<Runnable> getTasks() {
		return tasks;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * propra2012.gruppe33.graphics.rendering.scenegraph.SceneProcessor#resetTime
	 * ()
	 */
	@Override
	public void resetTime() {
		lastTime = -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * propra2012.gruppe33.graphics.rendering.scenegraph.SceneProcessor#getRoot
	 * ()
	 */
	@Override
	public S getRoot() {
		return root;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Canvas#addNotify()
	 */
	@Override
	public void addNotify() {
		super.addNotify();

		// Double buffered should be fine
		createBufferStrategy(2);

		// Create the strategy
		bufferStrategy = getBufferStrategy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see propra2012.gruppe33.graphics.rendering.scenegraph.SceneProcessor#
	 * isShutdownRequested()
	 */
	@Override
	public boolean isShutdownRequested() {
		return shutdownRequested;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see propra2012.gruppe33.graphics.rendering.scenegraph.SceneProcessor#
	 * setShutdownRequest(boolean)
	 */
	@Override
	public void setShutdownRequest(boolean shutdownRequested) {
		this.shutdownRequested = shutdownRequested;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * propra2012.gruppe33.graphics.rendering.scenegraph.SceneProcessor#process
	 * (int)
	 */
	@Override
	public void process(int maxFPS) throws Exception {

		// If root is null we cannot process...
		if (root == null) {

			// Sleep the given amount of time
			if (maxFPS > 0) {
				Thread.sleep(1000 / maxFPS);
			}

			return;
		}

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

		// Load from cache
		BufferStrategy ptr = bufferStrategy;

		// Tmp var
		Runnable task;

		// Process all tasks
		while ((task = tasks.poll()) != null) {
			task.run();
		}

		// Check size!
		if (w > 0 && h > 0 && ptr != null) {
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

				// Simulate the scene
				root.simulate(g2d, w, h, curTime - lastTime);
			} finally {
				// Dispose
				g2d.dispose();
			}

			// Get the graphics context
			Graphics frameBuffer = ptr.getDrawGraphics();

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
			if (!ptr.contentsLost()) {
				ptr.show();
			}

			// Synchronize with toolkit
			Toolkit.getDefaultToolkit().sync();
		} else {
			// Simulate the scene without drawing
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
				// Sleep
				Thread.sleep(minDuration - frameDuration);
			}
		}
	}
}
