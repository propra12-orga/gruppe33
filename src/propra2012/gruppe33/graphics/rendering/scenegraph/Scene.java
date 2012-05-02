package propra2012.gruppe33.graphics.rendering.scenegraph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class Scene extends Entity implements KeyListener {

	// The keyboard state which is used to process the input
	private Map<Integer, Boolean> keyboardState = new HashMap<Integer, Boolean>();

	// The width and height of the scene
	private int width, height;

	public Scene(String id, int width, int height) {
		super(id);
		setWidth(width);
		setHeight(height);
	}

	public void clearKeyboardState() {
		// Clear the complete keyboard state
		keyboardState.clear();
	}

	public boolean isPressed(int vk) {
		Boolean pressed = keyboardState.get(vk);
		return pressed != null ? pressed : false;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keyboardState.put(e.getKeyCode(), Boolean.TRUE);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keyboardState.put(e.getKeyCode(), Boolean.FALSE);
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		if (width <= 0) {
			throw new IllegalArgumentException("width must be > 0");
		}

		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		if (height <= 0) {
			throw new IllegalArgumentException("height must be > 0");
		}

		this.height = height;
	}

	public void simulate(Graphics g, int w, int h) {
		simulate(g, w, h, 0);
	}

	public void simulate(Graphics g, int w, int h, long tpf) {

		// Only update if tpf is > 0
		if (tpf > 0) {

			// Update the complete scene
			update(tpf * 0.001f);
		}

		// Not visible...
		if (w <= 0 || h <= 0) {
			return;
		}

		// Convert
		Graphics2D g2d = (Graphics2D) g;

		// Calc the ratio which we have to fit
		float dstRatio = width / (float) height;

		// Calc new dimension
		float newWidth = w, newHeight = w / dstRatio;

		// Check height
		if (newHeight > h) {

			// Recalc
			newHeight = h;
			newWidth = dstRatio * newHeight;
		}

		// At first translate the context
		g2d.translate(w * 0.5f - newWidth * 0.5f, h * 0.5f - newHeight * 0.5f);

		// Scale using the vector
		g2d.scale(newWidth / width, newHeight / height);

		// Limit the clip
		g2d.setClip(0, 0, width, height);

		// Set background color
		g2d.setBackground(Color.white);

		// Clear the rect
		g2d.clearRect(0, 0, width, height);

		// Finally render
		render(g2d);
	}
}
