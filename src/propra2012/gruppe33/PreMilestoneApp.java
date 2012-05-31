package propra2012.gruppe33;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.File;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridController;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridLoader;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridRoutines;
import propra2012.gruppe33.bomberman.graphics.sprite.AnimationRoutines;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.GraphicsEntity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Scene;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.SceneProcessor;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.animation.RenderedAnimation;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.math.Grid;
import propra2012.gruppe33.engine.resources.assets.AssetManager;

/**
 * 
 * @author Christopher Probst
 */
public class PreMilestoneApp {

	public static Scene createGUI() throws Exception {

		final Scene gui = new Scene(new AssetManager(new File(
				"scenes/default.zip")), 1024, 1024);

		gui.attach(new GraphicsEntity() {

			@Override
			protected void onRender(Graphics2D original, Graphics2D transformed) {
				super.onRender(original, transformed);

				transformed.setColor(Color.black);
				transformed.drawString("START DEMO", 0, 0);
				transformed.drawString("PRESS ENTER", -3, 10);
			}

			@Override
			protected void onUpdate(float tpf) {
				super.onUpdate(tpf);

				if (gui.isPressed(KeyEvent.VK_ENTER)) {
					SceneProcessor sp = gui.processor();
					try {
						sp.root(createDemoGame());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		gui.scale().set(10, 10);
		gui.position().set(120, 512);

		return gui;
	}

	public static Scene createDemoGame() throws Exception {

		// Create new asset manager using the given zip file
		AssetManager assets = new AssetManager(new File("scenes/default.zip"));

		// Create new scene with the given assets
		Scene scene = new Scene(assets, 1024, 1024);

		// Load the char array
		char[][] map = assets.loadAsset("assets/maps/smallmap.txt",
				GridLoader.LOADER).get();

		// GridLoader.generate(map, 5678);

		// Parse and setup map
		GraphicsEntity grid = GridLoader.parse(map, scene);

		// Create new player as knight
		GraphicsEntity player = GridRoutines.createLocalKnight(assets, "Kr0e");

		// Place to spawn
		grid.childAt(grid.typeProp(Grid.class).index(1, 1)).attach(player);

		return scene;
	}
}
