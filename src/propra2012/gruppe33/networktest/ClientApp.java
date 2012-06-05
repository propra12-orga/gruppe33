package propra2012.gruppe33.networktest;

import java.awt.Frame;

import com.indyforge.twod.engine.graphics.GraphicsRoutines;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor.NetworkMode;

public class ClientApp {

	public static void main(String[] args) throws Exception {

		// Create a new scene
		SceneProcessor sceneProcessor = new SceneProcessor(NetworkMode.Client);
		sceneProcessor.onlyRenderWithFocus(false);

		// Connect the scene
		sceneProcessor.openClient("localhost", 1337).linkClient("Kr0e");

		// Create peer
		Frame frame = GraphicsRoutines.createFrame(sceneProcessor, "Bomberman",
				800, 600);

		while (!sceneProcessor.isShutdownRequested()) {

			// Process the world (the main game-loop)
			sceneProcessor.process(60);
		}

		// Release all network resources
		sceneProcessor.releaseNetworkResources();

		// Destroy
		frame.dispose();
	}
}
