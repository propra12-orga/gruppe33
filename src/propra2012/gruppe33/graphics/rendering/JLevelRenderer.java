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

	public Level getLevel() {
		return level;
	}

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

	public Scene getScene() {
		return scene;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		// Just request a repaint
		repaint();
	}

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
