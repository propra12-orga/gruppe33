package com.indyforge.twod.engine.graphics.rendering.scenegraph;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

import com.indyforge.foxnet.rmi.InvokerManager;
import com.indyforge.foxnet.rmi.LookupException;
import com.indyforge.foxnet.rmi.pattern.change.AdminSessionServer;
import com.indyforge.foxnet.rmi.pattern.change.Change;
import com.indyforge.foxnet.rmi.pattern.change.Changeable;
import com.indyforge.foxnet.rmi.pattern.change.ChangeableQueue;
import com.indyforge.foxnet.rmi.pattern.change.Session;
import com.indyforge.foxnet.rmi.pattern.change.SessionServer;
import com.indyforge.foxnet.rmi.pattern.change.impl.DefaultChangeableQueue;
import com.indyforge.foxnet.rmi.pattern.change.impl.DefaultSessionServer;
import com.indyforge.foxnet.rmi.transport.network.Broadcaster;
import com.indyforge.foxnet.rmi.transport.network.ConnectionManager;
import com.indyforge.twod.engine.graphics.GraphicsRoutines;
import com.indyforge.twod.engine.resources.assets.AssetManager;
import com.indyforge.twod.engine.util.task.Task;
import com.indyforge.twod.engine.util.task.TaskQueue;

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
public final class SceneProcessor implements Changeable<SceneProcessor>,
		KeyListener, FocusListener {

	/**
	 * @see Broadcaster#receiveBroadcast(int, int, int)
	 */
	public static List<Object> receiveBroadcast(int port, int maxResults,
			int timeoutmillies) throws IOException, ClassNotFoundException {
		return Broadcaster.receiveBroadcast(port, maxResults, timeoutmillies);
	}

	/**
	 * @see Broadcaster#receiveBroadcast(SocketAddress, int, int)
	 */
	public static List<Object> receiveBroadcast(SocketAddress target,
			int maxResults, int timeoutmillies) throws IOException,
			ClassNotFoundException {
		return Broadcaster.receiveBroadcast(target, maxResults, timeoutmillies);
	}

	public static final String REMOTE_NAME = "session_server";

	/*
	 * Used for tasks which should be processed in the main thread.
	 */
	private final TaskQueue taskQueue = new TaskQueue(
			new LinkedBlockingDeque<Task>());

	/*
	 * ************************************************************************
	 * NETWORK PART BEGINS ****************************************************
	 * ************************************************************************
	 */

	/*
	 * Used to lock the access.
	 */
	private final Object netLock = new Object();

	/*
	 * The connection manager of this scene processor.
	 */
	private ConnectionManager connectionManager;

	/*
	 * The network mode of this processor.
	 */
	private NetworkMode networkMode;

	/*
	 * The message broadcaster.
	 */
	private Broadcaster broadcaster;

	/*
	 * Will be set by the server to "reset" the network time. The user can
	 * calculate the active network time using this timestamp.
	 */
	private long networkInitTimestamp = -1;

	/*
	 * This flag is used for applying queued changes.
	 */
	private boolean synchronousQueue = false;

	/*
	 * ************************************************************************
	 * NETWORK CLIENT PART ****************************************************
	 * ************************************************************************
	 */

	/*
	 * Here we can store the invoker manager of this scene processor.
	 */
	private InvokerManager invokerManager;

	/*
	 * Here we can store the session of this scene processor.
	 */
	private Session<SceneProcessor> session;

	/*
	 * The changeable session instances.
	 */
	private ChangeableQueue<SceneProcessor> changeableClient;
	private ChangeableQueue<SceneProcessor> changeableServer;

	/*
	 * ************************************************************************
	 * NETWORK SERVER PART ****************************************************
	 * ************************************************************************
	 */

	/*
	 * Here we can store the admin session server of this scene processor.
	 */
	private AdminSessionServer<SceneProcessor> adminSessionServer;

	/*
	 * ************************************************************************
	 * NETWORK PART ENDS ******************************************************
	 * ************************************************************************
	 */

	/*
	 * The frame of this scene processor or null (If headless).
	 */
	private final Frame frame;

	/*
	 * The canvas of this scene processor or null (If headless).
	 */
	private final Canvas canvas;

	/*
	 * This is the thread-safe buffer strategy for rendering.
	 * 
	 * VOLATILE: The var is set in the EDT.
	 */
	private volatile BufferStrategy bufferStrategy;

	/*
	 * The last thread which processed this scene processor.
	 */
	private volatile Thread lastProcessorThread;

	/*
	 * The fast offscreen image. Ultimate hardware performance. There is no
	 * better way to render graphics in java2d.
	 */
	private BufferedImage offscreen;

	/*
	 * The shutdown requested flag.
	 */
	private volatile boolean shutdownRequested = false,

	/*
	 * The only render with focus flag.
	 */
	onlyRenderWithFocus = true;

	/*
	 * The scene which we want to render.
	 */
	private Scene root;

	/*
	 * Used for timing.
	 */
	private long curTime, lastTime = -1;

	/*
	 * Thread safe vars for rendering.
	 * 
	 * VOLATILE: These vars are set in the EDT.
	 */
	private volatile int peerWidth, peerHeight;

	/**
	 * 
	 * @author Christopher Probst
	 * 
	 */
	public enum NetworkMode {
		Server, Client, Offline
	}

	/**
	 * Creates a new offline scene processor.
	 * 
	 */
	public SceneProcessor() {
		this(NetworkMode.Offline, null, -1, -1);
	}

	/**
	 * Creates a new offline scene processor.
	 * 
	 * @param title
	 *            The title of the frame. (Ignored if headless mode)
	 * @param width
	 *            The width of the frame. (Ignored if headless mode)
	 * @param height
	 *            The height of the frame. (Ignored if headless mode)
	 */
	public SceneProcessor(String title, int width, int height)
			throws InterruptedException, InvocationTargetException {
		this(NetworkMode.Offline, title, width, height);
	}

	/**
	 * Creates a new scene processor using the given network mode.
	 * 
	 * @param networkMode
	 *            The network mode of this processor.
	 */
	public SceneProcessor(NetworkMode networkMode) {
		this(networkMode, null, -1, -1);
	}

	/**
	 * Creates a new scene processor using the given network mode.
	 * 
	 * @param networkMode
	 *            The network mode of this processor.
	 * @param title
	 *            The title of the frame. (Ignored if headless mode)
	 * @param width
	 *            The width of the frame. (Ignored if headless mode)
	 * @param height
	 *            The height of the frame. (Ignored if headless mode)
	 */
	public SceneProcessor(NetworkMode networkMode, String title, int width,
			int height) {

		// Set the network mode
		networkMode(networkMode);

		/*
		 * Very important! The twod-engine decides between headless and
		 * not-headless mode. This is very useful for server systems. In
		 * headless mode no resources like images, sound, etc. are really
		 * loaded. Only the references to these resources are used.
		 */
		if (!AssetManager.isHeadless()) {

			/*
			 * Create a new canvas implementation if not headless.
			 */
			canvas = new Canvas() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

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
			};

			// The canvas is rendered by us!
			canvas.setIgnoreRepaint(true);

			// This canvas can receive input
			canvas.setFocusable(true);

			// Add resize listener
			canvas.addComponentListener(new ComponentAdapter() {

				@Override
				public void componentResized(ComponentEvent e) {
					// Threading issues...
					peerWidth = canvas.getWidth();
					peerHeight = canvas.getHeight();
				}
			});

			// Add listener
			canvas.addKeyListener(this);
			canvas.addFocusListener(this);

			/*
			 * Create a new frame using this scene processor.
			 */
			try {
				frame = title != null && width > 0 && height > 0 ? GraphicsRoutines
						.createFrame(this, title, width, height) : null;
			} catch (Exception e1) {
				throw new RuntimeException("Failed to create scene processor "
						+ "frame. Reason: " + e1.getMessage(), e1);
			}
		} else {
			// Headless...
			frame = null;
			canvas = null;
			bufferStrategy = null;
			offscreen = null;
			peerWidth = -1;
			peerHeight = -1;
		}
	}

	/**
	 * @return the frame (which contains the canvas) of this scene processor or
	 *         null in headless mode.
	 */
	public Frame frame() {
		return frame;
	}

	/**
	 * @return the canvas of this scene processor or null in headless mode.
	 */
	public Canvas canvas() {
		return canvas;
	}

	/**
	 * @return the network mode of this processor.
	 */
	public NetworkMode networkMode() {
		synchronized (netLock) {
			return networkMode;
		}
	}

	/**
	 * Sets the network mode and releases old resources.
	 * 
	 * @param networkMode
	 *            The new network mode.
	 * @return
	 */
	public SceneProcessor networkMode(NetworkMode networkMode) {
		synchronized (netLock) {
			if (networkMode == null) {
				throw new NullPointerException("networkMode");
			} else if (hasNetworkResources()) {
				releaseNetworkResources();
			}
			// Init the connection manager
			connectionManager = (this.networkMode = networkMode) != NetworkMode.Offline ? new ConnectionManager(
					networkMode == NetworkMode.Server ? true : false) : null;

			return this;
		}
	}

	/**
	 * @return true if this processor has any active network resources,
	 *         otherwise false.
	 */
	public boolean hasNetworkResources() {
		synchronized (netLock) {
			return (connectionManager != null ? !connectionManager.isDisposed()
					: false) || broadcaster != null;
		}
	}

	/**
	 * Resets the network. All active connections (broadcasting is not affected
	 * by this method) will be terminated and internal bindings will be removed.
	 * Please note that the network is still able to work after this method.
	 * <p>
	 * If you want to DESTROY the active network please use
	 * {@link SceneProcessor#releaseNetworkResources()}.
	 */
	public void resetNetwork() {
		synchronized (netLock) {
			if (networkMode != NetworkMode.Offline) {
				// Shutdown all connections
				connectionManager.channels().close().awaitUninterruptibly();
				networkInitTimestamp = -1;

				// Unbind all bindings
				connectionManager.staticReg().unbindAll();

				// Reset server stuff
				adminSessionServer = null;

				// Reset client stuff
				invokerManager = null;
				session = null;
				changeableClient = null;
				changeableServer = null;
			}
		}
	}

	/**
	 * Releases all active network resources and sets the network mode to
	 * offline.
	 */
	public void releaseNetworkResources() {
		synchronized (netLock) {
			if (hasNetworkResources()) {
				if (connectionManager != null) {
					connectionManager.dispose();
					connectionManager = null;
				}
				networkMode = NetworkMode.Offline;
				networkInitTimestamp = -1;
				closeBroadcaster();

				// Delete server stuff
				adminSessionServer = null;

				// Delete client stuff
				invokerManager = null;
				session = null;
				changeableClient = null;
				changeableServer = null;
			}
		}
	}

	/**
	 * Disposes this scene processor which is not able to work after calling
	 * this method.
	 */
	public void dispose() {
		releaseNetworkResources();
		if (frame != null) {
			frame.dispose();
		}
	}

	/**
	 * Connects to the given server. If the scene processor is already
	 * connected, the old connection will be cancelled first.
	 * 
	 * @param socketAddress
	 *            The host address.
	 * @return this for chaining;
	 * @throws IOException
	 *             If the connection attempt failed.
	 */
	public SceneProcessor openClient(SocketAddress socketAddress)
			throws IOException {
		synchronized (netLock) {
			if (networkMode != NetworkMode.Client) {
				throw new IllegalStateException("Wrong network mode");
			} else if (invokerManager != null) {
				// Reset the network
				resetNetwork();
			}
			try {
				// Try to open the connection
				invokerManager = connectionManager.openClient(socketAddress);
			} catch (IOException e) {
				/*
				 * Reset the network if the connection failed.
				 */
				resetNetwork();

				// Throw again!
				throw e;
			}
			return this;
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
	public SceneProcessor openClient(String host, int port) throws IOException {
		return openClient(new InetSocketAddress(host, port));
	}

	/**
	 * Resets the network time.
	 */
	public void resetNetworkTime() {
		synchronized (netLock) {
			if (networkMode == NetworkMode.Offline) {
				throw new IllegalStateException("Wrong network mode");
			}
			// Init the local network time
			networkInitTimestamp = System.currentTimeMillis();
		}
	}

	/**
	 * @return the synchronous queue flag.
	 */
	public boolean isSynchronousQueue() {
		synchronized (netLock) {
			return synchronousQueue;
		}
	}

	/**
	 * Sets the synchronous queue flag.
	 * 
	 * @param synchronousQueue
	 *            If true, the queued changes will be applied synchronously,
	 *            otherwise they are applies asynchronously.
	 * @return this for chaining.
	 */
	public SceneProcessor synchronousQueue(boolean synchronousQueue) {
		synchronized (netLock) {
			this.synchronousQueue = synchronousQueue;
			return this;
		}
	}

	/**
	 * @return the network time.
	 */
	public float networkTime() {
		synchronized (netLock) {
			if (networkMode == NetworkMode.Offline) {
				throw new IllegalStateException("Wrong network mode");
			} else if (networkInitTimestamp < 0) {
				return -1f;
			} else {
				return (System.currentTimeMillis() - networkInitTimestamp) * 0.001f;
			}
		}
	}

	/**
	 * Opens a server with the given port. If the scene processor has already an
	 * open server the old one will be disposed.
	 * 
	 * @param port
	 *            The port.
	 * @return this for chaining.
	 */
	public SceneProcessor openServer(int port) {
		synchronized (netLock) {
			if (networkMode != NetworkMode.Server) {
				throw new IllegalStateException("Wrong network mode");
			} else if (adminSessionServer != null) {
				// Reset network
				resetNetwork();
			}

			// Create a new instance
			adminSessionServer = new DefaultSessionServer<SceneProcessor>(this);

			// Bind the server instance
			connectionManager.staticReg().bind(REMOTE_NAME, adminSessionServer);

			// Open the server channel
			connectionManager.openServer(port);

			return this;
		}
	}

	/**
	 * 
	 * Opens a broadcaster using all known ip addresses of this computer.
	 * 
	 * @param broadcastPort
	 *            The broadcasting port.
	 * @param networkPort
	 *            The network port.
	 * @return this for chaining.
	 * @throws UnknownHostException
	 *             If an host exception occurs.
	 * @throws IOException
	 *             If an IO exception occurs.
	 */
	public SceneProcessor openBroadcaster(int broadcastPort, int networkPort)
			throws UnknownHostException, IOException {

		// At first get all addresses
		InetAddress[] addresses = InetAddress.getAllByName(InetAddress
				.getLocalHost().getHostName());

		// Create new array of inet socket addresses
		InetSocketAddress[] inetAddresses = new InetSocketAddress[addresses.length];

		// Copy
		for (int i = 0; i < addresses.length; i++) {
			inetAddresses[i] = new InetSocketAddress(addresses[i], networkPort);
		}

		// Open broadcaster
		return openBroadcaster(broadcastPort, inetAddresses);
	}

	/**
	 * Opens a broadcaster.
	 * 
	 * @param broadcastPort
	 *            The broadcasting port.
	 * @param message
	 *            The broadcasted message.
	 * @return this for chaining.
	 * @throws IOException
	 *             If an IO exception occurs.
	 */
	public SceneProcessor openBroadcaster(int broadcastPort, Object message)
			throws IOException {
		synchronized (netLock) {
			if (networkMode == NetworkMode.Offline) {
				throw new IllegalStateException("Wrong network mode");
			} else if (broadcaster != null) {
				closeBroadcaster();
			}

			// Try to create a new broadcaster
			broadcaster = new Broadcaster(broadcastPort, message);

			// Start the broadcaster
			broadcaster.start();

			return this;
		}
	}

	/**
	 * @return true if this scene processor has broadcaster, otherwise false.
	 */
	public boolean hasBroadcaster() {
		synchronized (netLock) {
			return broadcaster != null;
		}
	}

	/**
	 * Closes the broadcaster.
	 * 
	 * @return this for chaining.
	 */
	public SceneProcessor closeBroadcaster() {
		synchronized (netLock) {
			if (broadcaster != null) {
				broadcaster.interrupt();
			}
			broadcaster = null;
			return this;
		}
	}

	/**
	 * @return the broadcaster.
	 */
	public Broadcaster broadcaster() {
		synchronized (netLock) {
			return broadcaster;
		}
	}

	/**
	 * Links this scene processor with a session server.
	 * 
	 * @param name
	 *            The user name of this scene processor.
	 * @return a {@link Session} implementation.
	 * @throws LookupException
	 *             If the lookup failed.
	 */
	@SuppressWarnings("unchecked")
	public Session<SceneProcessor> linkClient(String name)
			throws LookupException {
		synchronized (netLock) {
			if (invokerManager == null) {
				throw new IllegalStateException("No invoker manager. "
						+ "Please open the client first.");
			} else if (session != null) {
				return session;
			}

			// Try to lookup the server
			SessionServer<SceneProcessor> server = (SessionServer<SceneProcessor>) invokerManager
					.lookupProxy(REMOTE_NAME);

			/*
			 * Open a session for this scene processor and save it.
			 */
			if ((session = server.openSession(this, name)) != null) {

				/*
				 * Init both changeable instances.
				 */
				changeableClient = new DefaultChangeableQueue<SceneProcessor>(
						session.client());
				changeableServer = new DefaultChangeableQueue<SceneProcessor>(
						session.server());

				return session;
			} else {
				throw new IllegalStateException("Null session returned");
			}
		}
	}

	/**
	 * @return true if this scene processor has an admin session server,
	 *         otherwise false.
	 */
	public boolean hasAdminSessionServer() {
		synchronized (netLock) {
			return adminSessionServer != null;
		}
	}

	/**
	 * @return the admin session server.
	 */
	public AdminSessionServer<SceneProcessor> adminSessionServer() {
		synchronized (netLock) {
			return adminSessionServer;
		}
	}

	/**
	 * @return true if this scene processor has a session, otherwise false.
	 */
	public boolean hasSession() {
		synchronized (netLock) {
			return session != null;
		}
	}

	/**
	 * @return the session.
	 */
	public Session<SceneProcessor> session() {
		synchronized (netLock) {
			return session;
		}
	}

	/**
	 * @return the changeable client (client-side).
	 */
	public ChangeableQueue<SceneProcessor> changeableClient() {
		synchronized (netLock) {
			return changeableClient;
		}
	}

	/**
	 * @return the changeable server (client-side).
	 */
	public ChangeableQueue<SceneProcessor> changeableServer() {
		synchronized (netLock) {
			return changeableServer;
		}
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
	 * com.indyforge.foxnet.rmi.pattern.change.Changeable#applyChange(com.indyforge
	 * .foxnet.rmi.pattern.change.Change)
	 */
	@Override
	public void applyChange(final Change<SceneProcessor> change) {
		if (Thread.currentThread().equals(lastProcessorThread)) {
			change.apply(this);
		} else {
			taskQueue.tasks().offer(new Task() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				/*
				 * (non-Javadoc)
				 * 
				 * @see com.indyforge.twod.engine.util.task.Task#update(float)
				 */
				@Override
				public boolean update(float tpf) {
					applyChange(change);
					return true;
				}
			});
		}
	}

	/**
	 * Sets the given root scene.
	 * 
	 * @param root
	 *            The root scene.
	 * @return this for chaining.
	 */
	public SceneProcessor root(Scene root) {
		// Remove old link
		if (this.root != null) {
			this.root.processor = null;
		}

		// Save the root
		this.root = root;

		// Connect if not null...
		if (root != null) {

			// Set processor
			root.processor = this;

			// The time must be resetted.
			resetTime();
		}

		return this;
	}

	/**
	 * @return the task deque.
	 */
	public TaskQueue taskQueue() {
		return taskQueue;
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
	 * @return this for chaining.
	 */
	public SceneProcessor shutdownRequest(final boolean shutdownRequested) {
		if (Thread.currentThread().equals(lastProcessorThread)) {
			this.shutdownRequested = shutdownRequested;
		} else {
			taskQueue.tasks().offer(new Task() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				/*
				 * (non-Javadoc)
				 * 
				 * @see com.indyforge.twod.engine.util.task.Task#update(float)
				 */
				@Override
				public boolean update(float tpf) {
					shutdownRequest(shutdownRequested);
					return true;
				}
			});
		}
		return this;
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

	/**
	 * Starts the game loop. This method blocks until shutdown is requested or
	 * an exception is thrown. This method {@link SceneProcessor#dispose()
	 * disposes} this scene processor when returning.
	 * 
	 * @param maxFPS
	 *            The maximum frames per second. A value < 1 means no max.
	 * @return this for chaining.
	 * @throws Exception
	 *             If some kind of error occurs.
	 */
	public SceneProcessor start(int maxFps) throws Exception {
		try {
			while (!isShutdownRequested()) {
				// Process the whole scene!
				process(maxFps);
			}

			return this;
		} finally {

			// Dispose the scene processor
			dispose();
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

		// Save the current thread
		lastProcessorThread = Thread.currentThread();

		// Do the time stuff...
		lastTime = lastTime == -1 ? System.currentTimeMillis() : curTime;

		// Get active time
		curTime = System.currentTimeMillis();

		// The passed time
		float tpf = (curTime - lastTime) * 0.001f;

		// Execute all pending tasks
		taskQueue.update(tpf);

		// If root is null we cannot process...
		if (root == null) {

			// Sleep the given amount of time
			if (maxFPS > 0) {
				Thread.sleep(1000 / maxFPS);
			}

			return;
		}

		// Was the scene processed ?
		boolean processed = false;

		/*
		 * Do only render if NOT in headless mode!
		 */
		if (canvas != null) {

			// Read into local cache
			int w = peerWidth, h = peerHeight;

			// Load from cache
			BufferStrategy ptr = bufferStrategy;

			if (w > 0 && h > 0 && ptr != null
					&& (!onlyRenderWithFocus || canvas.isFocusOwner())) {

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
					root.simulate(g2d, w, h, tpf);

					// Scene is processed now
					processed = true;
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
			}
		}

		/*
		 * If the scene was still not processed yet...
		 */
		if (!processed) {
			// Simulate the scene without drawing
			root.simulate(null, -1, -1, tpf);
		}

		/*
		 * Send all queued changes if not offline...
		 */
		synchronized (netLock) {
			if (networkMode != NetworkMode.Offline) {
				if (synchronousQueue) {
					if (hasAdminSessionServer()) {
						adminSessionServer.composite().applyQueuedChanges();
					} else if (hasSession()) {
						changeableClient.applyQueuedChanges();
						changeableServer.applyQueuedChanges();
					}
				} else {
					if (hasAdminSessionServer()) {
						adminSessionServer.composite().applyQueuedChangesLater(
								null);
					} else if (hasSession()) {
						changeableClient.applyQueuedChangesLater(null);
						changeableServer.applyQueuedChangesLater(null);
					}
				}
			}
		}

		// Check!
		if (maxFPS > 0) {
			// Calc the frame duration
			long frameDuration = System.currentTimeMillis() - curTime;

			// Calc the minimum duration
			long minDuration = 1000 / maxFPS;

			// Wait a bit
			if (frameDuration < minDuration) {
				// Sleep
				Thread.sleep(minDuration - frameDuration);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusGained(FocusEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusLost(FocusEvent e) {
		taskQueue.tasks().offer(new Task() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.indyforge.twod.engine.util.task.Task#update(float)
			 */
			@Override
			public boolean update(float tpf) {
				if (root != null) {
					// Clear keyboard
					root.clearKeyboardState();
				}
				return true;
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(final KeyEvent e) {

		taskQueue.tasks().offer(new Task() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.indyforge.twod.engine.util.task.Task#update(float)
			 */
			@Override
			public boolean update(float tpf) {
				if (root != null) {
					root.pressed(e.getKeyCode(), Boolean.TRUE);
				}
				return true;
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(final KeyEvent e) {
		taskQueue.tasks().offer(new Task() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.indyforge.twod.engine.util.task.Task#update(float)
			 */
			@Override
			public boolean update(float tpf) {
				if (root != null) {
					root.pressed(e.getKeyCode(), Boolean.FALSE);
				}
				return true;
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent e) {
	}
}
