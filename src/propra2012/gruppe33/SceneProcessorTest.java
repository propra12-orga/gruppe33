package propra2012.gruppe33;

import java.awt.Frame;

import javax.sound.sampled.Clip;

import com.indyforge.twod.engine.graphics.GraphicsRoutines;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;


/**
 * 
 * @author Christopher Probst
 * 
 */
public class SceneProcessorTest {

	public static void main(String[] args) throws Exception {

		// Create a new scene
		SceneProcessor sceneProcessor = new SceneProcessor();

		// Create peer
		Frame frame = GraphicsRoutines.createFrame(sceneProcessor, "Bomberman",
				800, 600);

		// Set root
		sceneProcessor.root(PreMilestoneApp.createGUI());

		
		
		while (!sceneProcessor.isShutdownRequested()) {

			// Process the world (the main game-loop)
			sceneProcessor.process(60);
		}

		// Destroy
		frame.dispose();
	}
}
