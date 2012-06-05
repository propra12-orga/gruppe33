package com.indyforge.twod.engine.graphics.rendering.scenegraph;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import com.foxnet.rmi.InvokerManager;
import com.foxnet.rmi.LookupException;
import com.foxnet.rmi.RemoteInterfaces;
import com.foxnet.rmi.transport.network.ConnectionManager;
import com.indyforge.twod.engine.graphics.GraphicsRoutines;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.Session;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.SessionServer;

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
 * @see Scene
 */
@RemoteInterfaces(RemoteProcessor.class)
public final class SceneProcessor extends Canvas implements RemoteProcessor {

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
	 * The connection manager of this scene processor.
	 */
	private final ConnectionManager connectionManager;

	/*
	 * The invoker manager of this scene processor.
	 */
	private volatile InvokerManager invokerManager;

	/*
	 * Here we can store a session instance.
	 */
	private volatile Session session;

	/*
	 * The fast offscreen image. Ultimate hardware performance. There is no
	 * better way to render graphics in java2d.
	 */
	private BufferedImage offscreen;

	/*
	 * The shutdown requested flag.
	 */
	private boolean shutdownRequested = false,

	/*
	 * The only render with focus flag.
	 */
	onlyRenderWithFocus = true;

	/*
	 * The scene which we want to render.
	 */
	private Scene root;

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
	 * Creates a new scene processor using the given connection manager.
	 * 
	 * @param connectionManager
	 *            The connection manager.
	 */
	public SceneProcessor(ConnectionManager connectionManager) {

		// Save the connection manager
		this.connectionManager = connectionManager;

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

	/**
	 * Disconnects this scene processor from the server.
	 */
	public synchronized void disconnect() {
		if (invokerManager != null) {
			invokerManager.close();
			invokerManager = null;
			session = null;
		}
	}

	/**
	 * Connects to the given server. If the scene processor is already
	 * connected, the old connection will be cancelled first.
	 * 
	 * @param host
	 *            The host address.
	 * @param port
	 *            The port.
	 * @return this for chaining;
	 * @throws IOException
	 *             If the connection attempt failed.
	 */
	public synchronized SceneProcessor connect(String host, int port)
			throws IOException {
		if (connectionManager == null) {
			throw new IllegalStateException("No connection manager");
		} else if (invokerManager != null) {
			disconnect();
		}
		// Try to open the connection
		invokerManager = connectionManager.openClient(host, port);
		return this;
	}

	/**
	 * Links this scene processor with a session server.
	 * 
	 * @param remoteName
	 *            The remote name of the server binding.
	 * @param name
	 *            The user name of this scenep processor.
	 * @return a {@link Session} implementation.
	 * @throws LookupException
	 *             If the lookup failed.
	 */
	public synchronized Session link(String remoteName, String name)
			throws LookupException {
		if (invokerManager == null) {
			throw new IllegalStateException("No invoker manager");
		} else if (session != null) {
			return session;
		}

		// Try to lookup the server
		SessionServer server = (SessionServer) invokerManager
				.lookupProxy(remoteName);

		/*
		 * Open a session for this scene processor and save it.
		 */
		return session = server.openSession(this, name);
	}

	/**
	 * @return true if this scene processor has a invoker manager, otherwise
	 *         false.
	 */
	public boolean hasInvokerManager() {
		return invokerManager != null;
	}

	/**
	 * @return the invoker manager.
	 */
	public InvokerManager invokerManager() {
		return invokerManager;
	}

	/**
	 * @return true if this scene processor has a connection manager, otherwise
	 *         false.
	 */
	public boolean hasConnectionManager() {
		return connectionManager != null;
	}

	/**
	 * @return the connection manager.
	 */
	public ConnectionManager connectionManager() {
		return connectionManager;
	}

	/**
	 * Should be called when the scene processor is terminated.
	 */
	public void releaseNetworkResources() {
		if (connectionManager != null) {
			connectionManager.shudown();
		}
	}

	/**
	 * @return true if this scene processor has a session, otherwise false.
	 */
	public boolean hasSession() {
		return session != null;
	}

	/**
	 * @return the session.
	 */
	public Session session() {
		return session;
	}

	/**
	 * @return the scene root.
	 */
	public Scene root() {
		return root;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.RemoteProcessor
	 * #fireSceneEvent
	 * (com.indyforge.twod.engine.graphics.rendering.scenegraph.EntityFilter,
	 * java.lang.Object, java.lang.Object[])
	 */
	@Override
	public void fireSceneEvent(final EntityFilter entityFilter,
			final Object event, final Object... params) {
		tasks().offer(new Runnable() {

			@Override
			public void run() {
				if (root != null) {
					root.fireEvent(entityFilter, event, params);
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.RemoteProcessor
	 * #fireSceneEvent(java.lang.Object, java.lang.Object[])
	 */
	@Override
	public void fireSceneEvent(final Object event, final Object... params) {
		tasks().offer(new Runnable() {

			@Override
			public void run() {
				if (root != null) {
					root.fireEvent(event, params);
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.RemoteProcessor
	 * #addChange
	 * (com.indyforge.twod.engine.graphics.rendering.scenegraph.Change)
	 */
	@Override
	public void addChange(final Change change) {
		tasks().offer(new Runnable() {

			@Override
			public void run() {
				change.apply(SceneProcessor.this);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.RemoteProcessor
	 * #root(com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene)
	 */
	@Override
	public void root(final Scene root) {
		/*
		 * Set the root in the game thread!
		 */
		tasks().offer(new Runnable() {

			@Override
			public void run() {

				// Remove old link
				if (SceneProcessor.this.root != null) {
					SceneProcessor.this.root
							.disconnectFrom(SceneProcessor.this);
					SceneProcessor.this.root.processor = null;
				}

				// Save the root
				SceneProcessor.this.root = root;

				// Connect if not null...
				if (root != null) {
					// Connect the input
					root.connectTo(SceneProcessor.this);

					// Set processor
					root.processor = SceneProcessor.this;

					// The time must be resetted.
					resetTime();
				}
			}
		});
	}

	/**
	 * The returned queue is thread-safe.
	 * 
	 * @return the task queue.
	 */
	public BlockingQueue<Runnable> tasks() {
		return tasks;
	}

	/**
	 * Resets the processor time.
	 */
	public void resetTime() {
		lastTime = -1;
	}

	/**
	 * @return the shutdown-requested flag.
	 */
	public boolean isShutdownRequested() {
		return shutdownRequested;
	}

	/**
	 * Sets the shutdown-requested flag.
	 * 
	 * @param shutdownRequested
	 *            If true the shutdown will be initiated.
	 */
	public void shutdownRequest(boolean shutdownRequested) {
		this.shutdownRequested = shutdownRequested;
	}

	/**
	 * @return the only-render-with-focus flag.
	 */
	public boolean isOnlyRenderWithFocus() {
		return onlyRenderWithFocus;
	}

	/**
	 * Sets the only-render-with-focus flag.
	 * 
	 * @param onlyRenderWithFocus
	 *            If true the processor will stop rendering without focus.
	 */
	public void onlyRenderWithFocus(boolean onlyRenderWithFocus) {
		this.onlyRenderWithFocus = onlyRenderWithFocus;
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

	/**
	 * Processes all pending tasks. BE VERY CAREFUL with this method. It should
	 * only be called in the game thread. Otherwise you will loose
	 * thread-safety.
	 */
	public void processTasks() {
		// Tmp var
		Runnable task;

		// Process all tasks
		while ((task = tasks.poll()) != null) {
			task.run();
		}
	}

	/**
	 * Process the whole scene.
	 * 
	 * @param maxFPS
	 *            The maximum frames per second. A value < 1 means no max.
	 * @throws Exception
	 *             If some kind of error occurs.
	 */
	public void process(int maxFPS) throws Exception {

		// Process all pending tasks
		processTasks();

		// Do the time stuff...
		lastTime = lastTime == -1 ? System.currentTimeMillis() : curTime;

		// Get active time
		curTime = System.currentTimeMillis();

		// If root is null we cannot process...
		if (root == null) {

			// Sleep the given amount of time
			if (maxFPS > 0) {
				Thread.sleep(1000 / maxFPS);
			}

			return;
		}

		// Read into local cache
		int w = peerWidth, h = peerHeight;

		// Load from cache
		BufferStrategy ptr = bufferStrategy;

		// Check size!
		if (w > 0 && h > 0 && ptr != null
				&& (!onlyRenderWithFocus || isFocusOwner())) {
			/*
			 * Advanced image rendering.
			 */
			// Create new...
			if (offscreen == null || offscreen.getWidth() != w
					|| offscreen.getHeight() != h) {

				// Create new compatible image for fast rendering
				offscreen = GraphicsRoutines.createImage(w, h);
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
