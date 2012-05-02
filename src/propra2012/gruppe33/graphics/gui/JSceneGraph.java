package propra2012.gruppe33.graphics.gui;

import java.awt.Graphics;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import propra2012.gruppe33.graphics.scenegraph.SceneGraph;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class JSceneGraph extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final SceneGraph sceneGraph;

	// The scheduler of this scene graph
	private final ScheduledExecutorService scheduler = Executors
			.newSingleThreadScheduledExecutor();

	/**
	 * Create the panel.
	 */
	public JSceneGraph(int width, int height) {

		// Create scene graph
		sceneGraph = new SceneGraph(width, height);

		// Update every 33 ms
		scheduler.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						// Compute actions
						sceneGraph.updateLayers();
					}
				});

				// Add repaint request
				repaint();
			}
		}, 0, 33, TimeUnit.MILLISECONDS);
	}

	public SceneGraph getSceneGraph() {
		return sceneGraph;
	}

	public void paintComponent(Graphics g) {
		// Paint the native stuff
		super.paintComponent(g);

		// Repaint all layers
		sceneGraph.repaintLayers(g, getWidth(), getHeight());
	}
}
