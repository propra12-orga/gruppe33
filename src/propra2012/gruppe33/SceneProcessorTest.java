package propra2012.gruppe33;

import java.awt.Frame;

import propra2012.gruppe33.graphics.GraphicsRoutines;
import propra2012.gruppe33.graphics.rendering.scenegraph.SceneProcessor;
import propra2012.gruppe33.graphics.rendering.scenegraph.grid.Grid;
import propra2012.gruppe33.io.ObjectReader;
import propra2012.gruppe33.io.ObjectWriter;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class SceneProcessorTest {

	public static void main(String[] args) throws Exception {

		// Create a new world processor
		SceneProcessor<Grid> gridWorld = new SceneProcessor<Grid>();

		// Create peer
		Frame frame = GraphicsRoutines.createFrame(gridWorld, "Bomberman", 800,
				600);

		// Create the grid
		Grid grid = PreMilestoneApp.createDemoGame();

		ObjectWriter ow = new ObjectWriter();
		byte[] data = ow.put(grid);
		ObjectReader or = new ObjectReader();
		grid = (Grid) or.take(data);
		
		System.out.println(data.length);
		
		// Set root
		gridWorld.setRoot(grid);

		while (!gridWorld.isShutdownRequested()) {

			// Process the world (the main game-loop)
			gridWorld.process(60);
		}

		// Destroy
		frame.dispose();
	}
}
