package propra2012.gruppe33;

import java.awt.Frame;

import com.indyforge.twod.engine.graphics.GraphicsRoutines;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor.NetworkMode;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class SceneProcessorTest {

	public static void main(String[] args) throws Exception {

		// Create a new scene
		SceneProcessor sceneProcessor = new SceneProcessor(NetworkMode.Offline);

		// Create peer
		Frame frame = GraphicsRoutines.createFrame(sceneProcessor, "Bomberman",
				800, 600);

		// Set root
		sceneProcessor.root(PreMilestoneApp.createDemoGame(1));

		while (!sceneProcessor.isShutdownRequested()) {

			// Process the world (the main game-loop)
			sceneProcessor.process(60);
		}

		// Destroy
		frame.dispose();
	}
}
