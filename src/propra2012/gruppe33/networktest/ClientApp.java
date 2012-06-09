package propra2012.gruppe33.networktest;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor.NetworkMode;

public class ClientApp {

	public static void main(String[] args) throws Exception {

		// Create a new scene
		SceneProcessor sceneProcessor = new SceneProcessor(NetworkMode.Client,
				"Bomberman", 640, 1024);

		// Stop rendering if hidden...
		sceneProcessor.onlyRenderWithFocus(false);

		// Connect the scene
		sceneProcessor.openClient("kr0e.no-ip.info", 1337).linkClient("Kr0e");

		// Start the processor (In this thread!)
		sceneProcessor.start(60);

	}
}
