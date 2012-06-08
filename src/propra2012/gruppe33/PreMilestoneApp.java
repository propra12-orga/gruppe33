package propra2012.gruppe33;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.File;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridLoader;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridRoutines;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Grid;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.transform.PositionTarget;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.transform.ReachableQueue;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.transform.TransformMotor;
import com.indyforge.twod.engine.graphics.sprite.Sprite;
import com.indyforge.twod.engine.resources.assets.AssetManager;

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

		// Generate the map randomally
		// GridLoader.generate(map, System.nanoTime());

		// Put the new sound
		scene.soundManager().putSound("exp", "assets/sounds/exp.wav");

		// Load boom
		final Sprite boom = new Sprite(assets.loadImage(
				"assets/images/animated/boom.png", true), 5, 5);

		// Parse and setup map
		final GraphicsEntity grid = GridLoader.parse(map, scene);

		// Create new player as knight
		GraphicsEntity player = GridRoutines.createLocalKnight(assets, "Kr0e");

		// player.attach(new Entity() {
		//
		// @Override
		// protected void onUpdate(float tpf) {
		// super.onUpdate(tpf);
		//
		// // Get scene
		// Scene scene = ((GraphicsEntity) parent()).findScene();
		//
		// GraphicsEntity node = ((GraphicsEntity) parent().parent());
		//
		// if (scene.isPressed(KeyEvent.VK_SPACE)
		// && !GridRoutines.hasFieldExplosion(node)) {
		//
		// GraphicsEntity bomb = GridRoutines
		// .createExplosion(boom, 33);
		//
		// scene.soundManager().playSound("exp", true);
		//
		// // Attach the bomb
		// grid.childAt(
		// grid.typeProp(Grid.class).index(node.position()))
		// .attach(bomb);
		//
		// }
		// }
		// });

		// Place to spawn
		grid.childAt(grid.typeProp(Grid.class).index(1, 1)).attach(player);

		return scene;
	}
}
