package com.indyforge.twod.engine.graphics.rendering.scenegraph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.util.HashSet;
import java.util.Set;

import com.indyforge.twod.engine.graphics.ImageDesc;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;
import com.indyforge.twod.engine.resources.TransientRenderedEntity;
import com.indyforge.twod.engine.resources.assets.AssetManager;
import com.indyforge.twod.engine.sound.SoundManager;

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
 * @see GraphicsEntity
 * @see Vector2f
 */
public class Scene extends GraphicsEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * The keyboard state which is used to process the input.
	 * 
	 * IMPORTANT: This attribute is not serialized!
	 */
	private transient Set<Integer> keyboardState, singleKeyboardState,
			singleDiscardedKeyboardState;

	// The asset manager of this scene
	private final AssetManager assetManager;

	// The sound manager of this scene
	private final SoundManager soundManager;

	// The width and height of the scene
	private final int width, height;

	// The ratio of this entity
	private final float ratio;

	/*
	 * The processor which processes this scene.
	 * 
	 * IMPORTANT: This attribute is transient.
	 */
	transient SceneProcessor processor = null;

	/**
	 * @return the single discarded keyboard state. If the set does not exist
	 *         yet it will be created.
	 */
	private Set<Integer> singleDiscardedKeyboardState() {
		if (singleDiscardedKeyboardState == null) {
			singleDiscardedKeyboardState = new HashSet<Integer>();
		}
		return singleDiscardedKeyboardState;
	}

	/**
	 * @return the single keyboard state. If the set does not exist yet it will
	 *         be created.
	 */
	private Set<Integer> singleKeyboardState() {
		if (singleKeyboardState == null) {
			singleKeyboardState = new HashSet<Integer>();
		}
		return singleKeyboardState;
	}

	/**
	 * @return the keyboard state. If the set does not exist yet it will be
	 *         created.
	 */
	private Set<Integer> keyboardState() {
		if (keyboardState == null) {
			keyboardState = new HashSet<Integer>();
		}
		return keyboardState;
	}

	/**
	 * Creates a new scene without an asset manager.
	 * 
	 * @param width
	 *            The provided viewport width.
	 * @param height
	 *            The provided viewport height.
	 */
	public Scene(int width, int height) throws Exception {
		this(null, width, height);
	}

	/**
	 * Creates a new scene.
	 * 
	 * @param assetManager
	 *            The asset manager of this scene. Can be null if you do not
	 *            need assets.
	 * @param width
	 *            The provided viewport width.
	 * @param height
	 *            The provided viewport height.
	 */
	public Scene(AssetManager assetManager, int width, int height)
			throws Exception {

		if (width <= 0) {
			throw new IllegalArgumentException("width must be > 0");
		} else if (height <= 0) {
			throw new IllegalArgumentException("height must be > 0");
		}

		// Save the asset manager
		this.assetManager = assetManager;

		// Create and save the new sound manager
		soundManager = new SoundManager(assetManager);

		// Init
		this.width = width;
		this.height = height;

		// Calc new ratio
		ratio = width / (float) height;
	}

	/**
	 * @return the asset manager of this scene.
	 */
	public AssetManager assetManager() {
		return assetManager;
	}

	/**
	 * @return the sound manager of this scene.
	 */
	public SoundManager soundManager() {
		return soundManager;
	}

	/**
	 * @return the scene processor.
	 */
	public SceneProcessor processor() {
		return processor;
	}

	/**
	 * Clears the keyboard state.
	 * 
	 * @return this scene for chaining.
	 */
	public Scene clearKeyboardState() {
		// Clear the complete keyboard state
		if (keyboardState != null && !keyboardState.isEmpty()) {
			keyboardState.clear();
		}
		return this;
	}

	/**
	 * Sets the pressed flag for the given key code.
	 * 
	 * @param vk
	 *            The virtual key code.
	 * @param pressed
	 *            The pressed flag.
	 * @return this for chaining.
	 */
	public Scene pressed(int vk, boolean pressed) {

		// Add or remove the key code
		if (pressed) {
			// Already exists ?
			if (keyboardState().add(vk)) {

				// This key was not pressed yet!
				singleKeyboardState().add(vk);
			}
		} else {
			/*
			 * Remove from all maps!
			 */
			if (keyboardState != null) {
				keyboardState.remove(vk);
			}
			if (singleKeyboardState != null) {
				singleKeyboardState.remove(vk);
			}
			if (singleDiscardedKeyboardState != null) {
				singleDiscardedKeyboardState.remove(vk);
			}
		}

		return this;
	}

	/**
	 * Returns a given key's pressed status. If this method returns true for a
	 * given key code the key must be released and pressed again before this
	 * method returns true again for the given key code.
	 * 
	 * @param vk
	 *            The keycode you want to check. (KeyEvent.VK_****)
	 * @return true if the key is pressed otherwise false.
	 */
	public boolean isSinglePressed(int vk) {
		if (singleKeyboardState != null && singleKeyboardState.contains(vk)) {
			singleDiscardedKeyboardState().add(vk);
			return true;
		}
		return false;
	}

	/**
	 * Returns a given key's pressed status.
	 * 
	 * @param vk
	 *            The keycode you want to check. (KeyEvent.VK_****)
	 * @return true if the key is pressed otherwise false.
	 */
	public boolean isPressed(int vk) {
		return keyboardState != null && keyboardState.contains(vk);
	}

	/**
	 * @return the x-y ratio as float.
	 */
	public float ratio() {
		return ratio;
	}

	/**
	 * @return the viewport width.
	 */
	public int width() {
		return width;
	}

	/**
	 * @return the viewport height.
	 */
	public int height() {
		return height;
	}

	/**
	 * @return the size as vector.
	 */
	public Vector2f sizeAsVector() {
		return new Vector2f(width, height);
	}

	/**
	 * @return the size as dimension.
	 */
	public Dimension size() {
		return new Dimension(width, height);
	}

	/**
	 * Creates a rendered opaque image using the given graphics entity.
	 * 
	 * @param bgColor
	 *            The background color.
	 * @param graphicsEntity
	 *            The graphics entity you want to render.
	 * @return a rendered opaque image.
	 */
	public RenderedImage renderedOpaqueEntity(Color bgColor,
			GraphicsEntity graphicsEntity) {
		return renderedEntity(Transparency.OPAQUE, bgColor, graphicsEntity);
	}

	/**
	 * Creates a rendered image using the given graphics entity.
	 * 
	 * @param transparency
	 *            The transparency.
	 * @param bgColor
	 *            The background color.
	 * @param graphicsEntity
	 *            The graphics entity you want to render.
	 * @return a rendered image.
	 */
	public RenderedImage renderedEntity(int transparency, Color bgColor,
			GraphicsEntity graphicsEntity) {

		// Bundle to entity and store as rendered transient entity
		TransientRenderedEntity renderedEntity = new TransientRenderedEntity(
				new ImageDesc().width(width).height(height)
						.transparency(transparency), bgColor, graphicsEntity);

		// Create a new entity
		RenderedImage root = new RenderedImage(renderedEntity);

		// This image is centered
		root.centered(true);

		// Set position
		root.position().set(width * 0.5f, height * 0.5f);

		// Adjust scale
		root.scale().set(width, height);

		return root;
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
	 * @return this scene for chaining.
	 */
	public final Scene simulate(Image destination) {
		return simulate(destination, 0);
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
	 * @return this scene for chaining.
	 */
	public final Scene simulate(Image destination, long tpf) {
		if (destination == null) {
			throw new NullPointerException("destination");
		}

		// Get the graphics context
		Graphics graphics = destination.getGraphics();

		try {
			if (graphics instanceof Graphics2D) {

				// Simulate the scene to image
				return simulate((Graphics2D) graphics,
						destination.getWidth(null),
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
	 * @return this scene for chaining.
	 */
	public final Scene simulate(Graphics2D graphics, int width, int height) {
		return simulate(graphics, width, height, 0);
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
	 *            The time-per-frame (ms) to update the children states.
	 * @return this scene for chaining.
	 */
	public final Scene simulate(Graphics2D graphics, int width, int height,
			long tpf) {

		// Remove the discarded single keyboard state
		if (singleDiscardedKeyboardState != null) {
			for (Integer vk : singleDiscardedKeyboardState) {
				singleKeyboardState.remove(vk);
			}
			singleDiscardedKeyboardState.clear();
		}

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
				render(copy);
			} finally {
				copy.dispose();
			}
		}
		return this;
	}
}
