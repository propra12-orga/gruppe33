package propra2012.gruppe33;

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
		SceneProcessor sceneProcessor = new SceneProcessor(NetworkMode.Offline,
				"bomberman", 800, 600);

		// Set root
		sceneProcessor.root(PreMilestoneApp.createDemoGame(1, 2, 3, 4, 5, 6, 7, 8, 9));
		sceneProcessor.start(60);
	}
}
