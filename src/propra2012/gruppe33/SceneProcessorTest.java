package propra2012.gruppe33;

import java.awt.Frame;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import propra2012.gruppe33.graphics.GraphicsRoutines;
import propra2012.gruppe33.graphics.rendering.scenegraph.SceneProcessor;
import propra2012.gruppe33.graphics.rendering.scenegraph.grid.Grid;

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
//		Grid grid = PreMilestoneApp.createDemoGame();
//		// //
//		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
//				new File("C:/test.txt")));
//		oos.writeObject(grid);
//		oos.close();
		//
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				"C:/test.txt"));
		Grid grid = (Grid) ois.readObject();
		ois.close();

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
