package propra2012.gruppe33.graphics.rendering;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JPanel;
import javax.swing.Timer;

import propra2012.gruppe33.graphics.rendering.scenegraph.Scene;

/**
 * A naiv approach to render a game. A JPanel is not very performant but it
 * should fit the needs since the game represents a simple task.
 * 
 * Basically this class renders every 33ms the game but never more often than
 * once in 20ms. This reduces overhead while resizing.
 * 
 * @author Christopher Probst
 * 
 */
public class JSceneRenderer<S extends Scene> extends JPanel implements
		ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The root node of this renderer
	private final S root;

	// The "repaint" timer
	private final Timer timer = new Timer(33, this);

	// Time vars
	private long timestamp = -1, minDelay = 20;

	/**
	 * OVERRIDE FOR CUSTOM BEHAVIOUR!
	 * 
	 * Here you can setup graphics settings.
	 * 
	 * @param graphics
	 *            The graphics context you want to set up.
	 */
	protected void setupGraphics(Graphics2D graphics) {
		// Apply graphics settings here!
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);

	}

	/**
	 * Creates a new scene renderer using the given scene.
	 * 
	 * @param root
	 *            The root scene which you want to render.
	 */
	public JSceneRenderer(S root) {
		if (root == null) {
			throw new NullPointerException("root");
		}

		// Save the root
		this.root = root;

		// Use root as key listener
		addKeyListener(root);

		/*
		 * IMPORTANT:
		 * 
		 * Clear the keyboard status if we lost the focus.
		 */
		addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				JSceneRenderer.this.root.clearKeyboardState();
			}
		});

		// Focusable!
		setFocusable(true);
	}

	/**
	 * @return the root scene.
	 */
	public S getRoot() {
		return root;
	}

	/**
	 * Starts the renderer (the animated rendereing).
	 * 
	 * @return true if the start was successful, otherwise false.
	 */
	public boolean start() {
		if (timer.isRunning()) {
			return false;
		} else {
			// Init timestamp
			timestamp = System.currentTimeMillis();

			// Activate timer
			timer.start();
			return true;
		}
	}

	/**
	 * Stops the renderer (the animated rendereing).
	 * 
	 * @return true if the stop was successful, otherwise false.
	 */
	public boolean stop() {
		if (!timer.isRunning()) {
			return false;
		} else {
			// Restore
			timestamp = -1;

			// Stop the running timer
			timer.stop();

			// Everything ok
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		// Just request a repaint
		repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Get current time
		long time = System.currentTimeMillis(), delay;

		// Check the current timestamp
		if (timestamp <= 0 || (delay = time - timestamp) < minDelay) {
			return;
		}

		// Convert
		Graphics2D g2d = (Graphics2D) g;

		// Apply graphics settings
		setupGraphics(g2d);

		// Render the root node
		root.simulate(g2d, getWidth(), getHeight(), delay);

		// Reset
		timestamp = time;
	}
}
