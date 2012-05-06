package propra2012.gruppe33.graphics.rendering.active;

import javax.swing.JFrame;

import propra2012.gruppe33.PreMilestoneApp;
import propra2012.gruppe33.graphics.rendering.scenegraph.SceneProcessor;
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
		SceneProcessor<Grid> gridPro = new CanvasSceneProcessor<Grid>(grid);

		// Add to frame
		frame.add((CanvasSceneProcessor<Grid>) gridPro);

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
