package propra2012.gruppe33.graphics.rendering.scenegraph;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import propra2012.gruppe33.graphics.assets.AssetManager;
import propra2012.gruppe33.graphics.rendering.scenegraph.math.Vector2f;

/**
 * A scene is a special entity which has some more features:
 * 
 * - Keyboard input management
 * 
 * - Scaled rendering (always chooses the maximum size but never ignores the
 * ratio!!!) This is very handy.
 * 
 * Since this is also an entity you have all basic features already provided by
 * the entity class. Obviously you can add multiple entities to a scene :-) .
 * 
 * @author Christopher Probst
 * @see Entity
 * @see Vector2f
 */
public class Scene extends Entity implements KeyListener, FocusListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * The keyboard state which is used to process the input.
	 * 
	 * IMPORTANT: This attribute is not serialized!
	 */
	private transient final Map<Integer, Boolean> keyboardState = new HashMap<Integer, Boolean>();

	// The asset manager of this scene
	private final AssetManager assetManager;

	// The width and height of the scene
	private final int width, height;

	// The ratio of this entity
	private final float ratio;

	/*
	 * The processor which processes this scene.
	 * 
	 * IMPORTANT: This attribute is transient.
	 */
	transient SceneProcessor<?> processor = null;

	/**
	 * Creates a new scene.
	 * 
	 * @param name
	 *            The name of the scene.
	 * @param assetBundle
	 *            The file of the asset bundle.
	 * @param width
	 *            The provided viewport width.
	 * @param height
	 *            The provided viewport height.
	 */
	public Scene(String name, File assetBundle, int width, int height)
			throws Exception {
		super(name);

		if (width <= 0) {
			throw new IllegalArgumentException("width must be > 0");
		} else if (height <= 0) {
			throw new IllegalArgumentException("height must be > 0");
		}

		// Initialize the asset manager
		assetManager = new AssetManager(assetBundle);

		// Init
		this.width = width;
		this.height = height;

		// Calc new ratio
		ratio = width / (float) height;
	}

	/**
	 * @return the asset manager of this scene.
	 */
	public AssetManager getAssetManager() {
		return assetManager;
	}

	/**
	 * @return the scene processor.
	 */
	public SceneProcessor<?> getProcessor() {
		return processor;
	}

	/**
	 * Connect to an awt component.
	 * 
	 * @param component
	 *            The component which should manages this scene with events.
	 */
	public void connectTo(Component component) {
		// Add listener
		component.addKeyListener(this);
		component.addFocusListener(this);
	}

	/**
	 * Disconnect from an awt component.
	 * 
	 * @param component
	 *            The component which manages this scene with events.
	 */
	public void disconnectFrom(Component component) {
		component.removeKeyListener(this);
		component.removeFocusListener(this);
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
		// Clear keyboard
		clearKeyboardState();
	}

	/**
	 * Clears all keyboard states.
	 */
	public void clearKeyboardState() {
		// Clear the complete keyboard state
		keyboardState.clear();
	}

	/**
	 * Returns a given key's pressed status.
	 * 
	 * @param vk
	 *            The keycode you want to check. (KeyEvent.VK_****)
	 * @return true if the key is pressed otherwise false.
	 */
	public boolean isPressed(int vk) {
		Boolean pressed = keyboardState.get(vk);
		return pressed != null ? pressed : false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		keyboardState.put(e.getKeyCode(), Boolean.TRUE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		keyboardState.put(e.getKeyCode(), Boolean.FALSE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent e) {
	}

	/**
	 * @return the x-y ratio as float.
	 */
	public float getRatio() {
		return ratio;
	}

	/**
	 * @return the viewport width.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the viewport height.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Simulates the scene with a time-per-frame of 0. This method will
	 * transforms the rendering in a way, that the viewport of the scene is
	 * translated to the destination image. Basically needed for simple
	 * rendering without modifying the states of the children.
	 * 
	 * 
	 * @param destination
	 *            The image you want to render to.
	 */
	public final void simulate(Image destination) {
		simulate(destination, 0);
	}

	/**
	 * Simulates the scene with a given time-per-frame. This method will
	 * transforms the rendering in a way, that the viewport of the scene is
	 * translated to the destination image.
	 * 
	 * @param destination
	 *            The image you want to render to.
	 * @param tpf
	 *            The time-per-frame to update the children states.
	 */
	public final void simulate(Image destination, long tpf) {
		if (destination == null) {
			throw new NullPointerException("destination");
		}

		// Get the graphics context
		Graphics graphics = destination.getGraphics();

		try {
			if (graphics instanceof Graphics2D) {

				// Simulate the scene to image
				simulate((Graphics2D) graphics, destination.getWidth(null),
						destination.getHeight(null), tpf);

			} else {
				throw new IllegalArgumentException("The image must return a "
						+ "Graphics2D object when calling getGraphics().");
			}
		} finally {
			// Dispose
			graphics.dispose();
		}
	}

	/**
	 * Simulates the scene with a time-per-frame of 0. This method will
	 * transforms the rendering in a way, that the viewport of the scene is
	 * translated to the graphics context. Basically needed for simple rendering
	 * without modifying the states of the children.
	 * 
	 * @param graphics
	 *            The graphics context you want to render to. If null the scene
	 *            is only updated.
	 * @param width
	 *            The width of the destination frame. If < 1 the scene is only
	 *            update.
	 * @param height
	 *            The height of the destination frame. If < 1 the scene is only
	 *            update.
	 */
	public final void simulate(Graphics2D graphics, int width, int height) {
		simulate(graphics, width, height, 0);
	}

	/**
	 * Simulates the scene with a given time-per-frame. This method will
	 * transforms the rendering in a way, that the viewport of the scene is
	 * translated to the graphics context.
	 * 
	 * @param graphics
	 *            The graphics context you want to render to. If null the scene
	 *            is only updated.
	 * @param width
	 *            The width of the destination frame. If < 1 the scene is only
	 *            update.
	 * @param height
	 *            The height of the destination frame. If < 1 the scene is only
	 *            update.
	 * @param tpf
	 *            The time-per-frame to update the children states.
	 */
	public final void simulate(Graphics2D graphics, int width, int height,
			long tpf) {

		// Only update if tpf is > 0
		if (tpf > 0) {

			// Update the complete scene
			update(tpf * 0.001f);
		}

		// Render if graphics exists and dimension is correct
		if (graphics != null && width > 0 && height > 0) {

			// Calc the ratio which we have to fit
			float dstRatio = ratio;

			// Calc new dimension
			float newWidth = width, newHeight = width / dstRatio;

			// Check height
			if (newHeight > height) {

				// Recalc
				newHeight = height;
				newWidth = dstRatio * newHeight;
			}

			// Copy the graphics
			Graphics2D copy = (Graphics2D) graphics.create();

			try {
				// At first translate the context
				copy.translate(width * 0.5f - newWidth * 0.5f, height * 0.5f
						- newHeight * 0.5f);

				// Scale using the vector
				copy.scale(newWidth / this.width, newHeight / this.height);

				// Limit the clip
				copy.setClip(0, 0, this.width, this.height);

				// Set background color
				copy.setBackground(Color.white);

				// Clear the rect
				copy.clearRect(0, 0, this.width, this.height);

				// Finally render
				render(copy, copy);
			} finally {
				copy.dispose();
			}
		}
	}
}
