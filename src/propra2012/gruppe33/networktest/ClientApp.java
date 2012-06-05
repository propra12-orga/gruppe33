package propra2012.gruppe33.networktest;

import java.awt.Frame;

import com.foxnet.rmi.transport.network.ConnectionManager;
import com.indyforge.twod.engine.graphics.GraphicsRoutines;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;

public class ClientApp {

	public static void main(String[] args) throws Exception {

		// Create a new scene
		SceneProcessor sceneProcessor = new SceneProcessor(
				new ConnectionManager(false));

		// Connect the scene
		sceneProcessor.connect("localhost", 1337).link("server", "Kr0e");

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
