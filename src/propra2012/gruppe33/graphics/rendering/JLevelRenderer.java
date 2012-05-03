package propra2012.gruppe33.graphics.rendering;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JPanel;
import javax.swing.Timer;

import propra2012.gruppe33.graphics.rendering.level.Level;
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
public class JLevelRenderer extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The root node of this renderer
	private final Scene scene;

	// The level of this renderer
	private final Level level;

	// The "repaint" timer - every 25 ms
	private final Timer timer = new Timer(33, this);

	// Time vars
	private long timestamp = -1, minDelay = 20;

	/**
	 * Creates a new level renderer using the given level.
	 * 
	 * @param level
	 *            The level which you want to render.
	 */
	public JLevelRenderer(Level level) {
		if (level == null) {
			throw new NullPointerException("level");
		}

		// Create a new scene
		scene = new Scene("main_scene", level.getWidth(), level.getHeight());

		// Save the level
		this.level = level;

		// Use scene as key listener
		addKeyListener(scene);

		/*
		 * IMPORTANT:
		 * 
		 * Clear the keyboard status if we lost tehe focus
		 */
		addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				scene.clearKeyboardState();
			}
		});

		// Focusable!
		setFocusable(true);
	}

	/**
	 * @return the level.
	 */
	public Level getLevel() {
		return level;
	}

	/**
	 * Starts the level renderer (the animated rendereing).
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
	 * Stops the level renderer (the animated rendereing).
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

	/**
	 * @return the scene.
	 */
	public Scene getScene() {
		return scene;
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

		// Render the root node
		scene.simulate(g, getWidth(), getHeight(), delay);

		// Reset
		timestamp = time;
	}
}
