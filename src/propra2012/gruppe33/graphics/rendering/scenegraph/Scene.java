package propra2012.gruppe33.graphics.rendering.scenegraph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

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
public class Scene extends Entity implements KeyListener {

	// The keyboard state which is used to process the input
	private Map<Integer, Boolean> keyboardState = new HashMap<Integer, Boolean>();

	// The width and height of the scene
	private int width, height;

	/**
	 * Creates a new scene.
	 * 
	 * @param name
	 *            The name of the scene.
	 * @param width
	 *            The provided viewport width.
	 * @param height
	 *            The provided viewport height.
	 */
	public Scene(String name, int width, int height) {
		super(name);
		setWidth(width);
		setHeight(height);
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
		return width / (float) height;
	}

	/**
	 * @return the viewport width.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets the viewport width.
	 * 
	 * @param width
	 *            The new width of the viewport.
	 */
	public void setWidth(int width) {
		if (width <= 0) {
			throw new IllegalArgumentException("width must be > 0");
		}

		this.width = width;
	}

	/**
	 * @return the viewport height.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Sets the viewport height.
	 * 
	 * @param height
	 *            The new height of the viewport.
	 */
	public void setHeight(int height) {
		if (height <= 0) {
			throw new IllegalArgumentException("height must be > 0");
		}

		this.height = height;
	}

	/**
	 * Simulates the scene with a time-per-frame of 0. This method will
	 * transforms the rendering in a way, that the viewport of the scene is
	 * translated to the destination frame (on what you are drawing). Basically
	 * needed for simple rendering without modifying the states of the children.
	 * 
	 * @param graphics
	 *            The graphics context you want to render to.
	 * @param width
	 *            The width of the destination frame.
	 * @param height
	 *            The height of the destination frame.
	 */
	public void simulate(Graphics graphics, int width, int height) {
		simulate(graphics, width, height, 0);
	}

	/**
	 * Simulates the scene with a given time-per-frame. This method will
	 * transforms the rendering in a way, that the viewport of the scene is
	 * translated to the destination frame (on what you are drawing).
	 * 
	 * @param graphics
	 *            The graphics context you want to render to.
	 * @param width
	 *            The width of the destination frame.
	 * @param height
	 *            The height of the destination frame.
	 * @param tpf
	 *            The time-per-frame to update the children states.
	 */
	public void simulate(Graphics graphics, int width, int height, long tpf) {

		// Only update if tpf is > 0
		if (tpf > 0) {

			// Update the complete scene
			update(tpf * 0.001f);
		}

		// Not visible...
		if (width <= 0 || height <= 0) {
			return;
		}

		// Convert
		Graphics2D g2d = (Graphics2D) graphics;

		// Calc the ratio which we have to fit
		float dstRatio = getRatio();

		// Calc new dimension
		float newWidth = width, newHeight = width / dstRatio;

		// Check height
		if (newHeight > height) {

			// Recalc
			newHeight = height;
			newWidth = dstRatio * newHeight;
		}

		// At first translate the context
		g2d.translate(width * 0.5f - newWidth * 0.5f, height * 0.5f - newHeight
				* 0.5f);

		// Scale using the vector
		g2d.scale(newWidth / this.width, newHeight / this.height);

		// Limit the clip
		g2d.setClip(0, 0, this.width, this.height);

		// Set background color
		g2d.setBackground(Color.white);

		// Clear the rect
		g2d.clearRect(0, 0, this.width, this.height);

		// Finally render
		render(g2d);
	}
}
