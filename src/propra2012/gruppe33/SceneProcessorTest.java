package propra2012.gruppe33;

import java.awt.Frame;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.Grid;
import propra2012.gruppe33.engine.graphics.GraphicsRoutines;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Scene;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.SceneProcessor;
import propra2012.gruppe33.engine.io.IoRoutines;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class SceneProcessorTest {

	public static void main(String[] args) throws Exception {

		// Create a new scene
		SceneProcessor<Scene> sceneProcessor = new SceneProcessor<Scene>();

		// Create peer
		Frame frame = GraphicsRoutines.createFrame(sceneProcessor, "Bomberman",
				800, 600);

		// Create the grid
		Grid grid = PreMilestoneApp.createDemoGame();

		grid = (Grid) IoRoutines.deserialize(IoRoutines.serialize(grid));

		// Set root
		sceneProcessor.root(grid);

		while (!sceneProcessor.isShutdownRequested()) {

			// Process the world (the main game-loop)
			sceneProcessor.process(60);
		}

		// Destroy
		frame.dispose();
	}
}
