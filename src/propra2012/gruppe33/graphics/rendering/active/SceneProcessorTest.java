package propra2012.gruppe33.graphics.rendering.active;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import propra2012.gruppe33.PreMilestoneApp;
import propra2012.gruppe33.graphics.rendering.scenegraph.grid.Grid;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class SceneProcessorTest {

	public static void main(String[] args) throws Exception {

		// Create the grid
		Grid grid = PreMilestoneApp.createDemoGame();

		JFrame frame = new JFrame("Fast frame");
		frame.setIgnoreRepaint(true);

		// Create a grid processor
		SceneProcessor<Grid> gridPro = new SceneProcessor<Grid>(grid);

		// Add to frame
		frame.add(gridPro);

		// Set size
		frame.setSize(800, 600);

		// Make visible
		frame.setVisible(true);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		while (true) {
			gridPro.process(150);
		}
	}
}
