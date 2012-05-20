package propra2012.gruppe33;

import java.awt.Frame;
import java.io.File;

import propra2012.gruppe33.engine.graphics.GraphicsRoutines;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.RenderedImage;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Scene;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.SceneProcessor;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.TransformMotor;
import propra2012.gruppe33.engine.resources.assets.AssetManager;

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
		// Grid grid = PreMilestoneApp.createDemoGame();

		// Set root
		// gridWorld.setRoot(grid);

		Scene scene = new Scene(
				new AssetManager(new File("scenes/default.zip")), 1024, 1024);

		RenderedImage ic = new RenderedImage(scene.assetManager().loadImage(
				"assets/images/solid.png", true));

		ic.scale().scaleLocal(400);
		ic.position().set(512, 512);
		scene.attach(ic);

		TransformMotor tm = new TransformMotor();
		tm.angularAcceleration(1);

		ic.attach(tm);

		sceneProcessor.setRoot(scene);

		while (!sceneProcessor.isShutdownRequested()) {

			// Process the world (the main game-loop)
			sceneProcessor.process(60);
		}

		// Destroy
		frame.dispose();
	}
}
