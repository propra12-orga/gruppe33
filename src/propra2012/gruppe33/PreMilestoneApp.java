package propra2012.gruppe33;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridLoader;
import propra2012.gruppe33.bomberman.graphics.sprite.AnimationRoutines;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.GraphicsEntity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Scene;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.SceneProcessor;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.TransformMotor;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.animation.RenderedAnimation;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.math.Grid;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.math.Vector2f;
import propra2012.gruppe33.engine.resources.assets.Asset;
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

		GridLoader.generate(map, 5678);

		// Parse and setup map
		GraphicsEntity grid = GridLoader.parse(map, scene);

		GraphicsEntity player = new GraphicsEntity();

		// Set player name
		player.name("Kr0e");

		// Create knight animation
		RenderedAnimation charAni = new RenderedAnimation(
				AnimationRoutines.createKnight(scene.assetManager(), 35, 35));

		// Attach char ani
		player.attach(charAni);

		// // Use default grid control
		// player.attach(new GridController().attach(AnimationRoutines
		// .createGridControllerAnimationHandler(charAni)),
		// new GridPositionUpdater() {
		// @Override
		// protected void onPositionChanged(
		// GridPositionUpdater gridPositionUpdater,
		// Point from, Point to) {
		// System.out.println("from " + from + " to " + to);
		// }
		//
		// });

		charAni.animationName("run_north").paused(false);

		player.scale().scaleLocal(1.5f);
		player.attach(new TransformMotor().linearVelocity(Vector2f.left()
				.scaleLocal(1)));

		Grid g = (Grid) grid.prop("grid");

		grid.childAt(g.index(5, 3)).attach(player);

		return scene;
	}

	// public static Scene createDemoGame() throws Exception {
	//
	// AssetManager assets = new AssetManager(new File("scenes/default.zip"));
	//
	//
	//
	// // Load grid from file
	// final Grid grid = new Grid(assets, "assets/maps/smallmap.txt", 2048,
	// 2048);
	//
	// GridLoader.generate(grid.mapData(), System.nanoTime());
	//
	// // Define the chars on which the character can move
	// grid.maxFieldVelocities().put('0', 600f);
	// grid.maxFieldVelocities().put('s', 600f);
	// // grid.maxFieldVelocities().put((char) ('0' + 1000), 600f);
	//
	// // Same as vec fields
	// grid.defaultCollectChars().addAll(grid.maxFieldVelocities().keySet());
	// grid.defaultCollectChars().add((char) ('0' + 1000));
	//
	// grid.defaultLineOfSightChars().add('0');
	// grid.defaultLineOfSightChars().add('s');
	// // grid.defaultLineOfSightChars().add((char) ('0' + 1000));
	//
	// // The explosion sprite
	// final Sprite explosion = new Sprite(assets.loadImage(
	// "assets/images/animated/boom.png", true), 5, 5);
	//
	// final Asset<BufferedImage> bomb = assets.loadImage(
	// "assets/images/bomb.png", true);
	//
	// final Asset<BufferedImage> breakable = assets.loadImage(
	// "assets/images/break.png", true);
	//
	// Map<Character, Asset<BufferedImage>> m = new HashMap<Character,
	// Asset<BufferedImage>>();
	// m.put((char) ('0' + 1000), breakable);
	//
	// GraphicsEntity b = grid.bundle(m);
	// b.index(1);
	//
	// List<Point> ptr = new ArrayList<Point>();
	// for (int y = 1; y < grid.mapData().length - 1; y++) {
	// for (int x = 1; x < grid.mapData()[0].length - 1; x++) {
	// if (grid.mapData()[y][x] >= 1000) {
	// ptr.add(new Point(x, y));
	// }
	// }
	// }
	//
	// Random r = new Random();
	// EntityRoutines.randomExit = ptr.get(r.nextInt(ptr.size()));
	// EntityRoutines.exit = assets
	// .loadImage("assets/images/escape.png", true);
	//
	// for (Entity e : b) {
	// GraphicsEntity ge = (GraphicsEntity) e;
	// Point p = grid.worldToNearestPoint(ge.position());
	// EntityRoutines.items.put(p, ge);
	//
	// grid.attach(ge);
	// }
	//
	// // Create ground
	// Entity ground = EntityRoutines.createGround(grid);
	//
	// // Create walls
	// Entity walls = EntityRoutines.createWalls(grid);
	//
	// // Create the solid blocks
	// Entity solids = EntityRoutines.createSolidBlocks(grid);
	//
	// // Create a new local player
	// GraphicsEntity player = EntityRoutines.createPlayer("Kr0e", grid, 1, 1);
	//
	// player.attach(new Entity() {
	//
	// boolean valid = true;
	//
	// @Override
	// protected void onUpdate(float tpf) {
	// super.onUpdate(tpf);
	//
	// if (grid.isPressed(KeyEvent.VK_SPACE) && valid) {
	// valid = false;
	// Point p = grid
	// .worldToNearestPoint(((GraphicsEntity) parent())
	// .position());
	//
	// EntityRoutines.createBomb(grid, explosion, bomb, p.x, p.y,
	// 30, 3, 3);
	//
	// } else if (!grid.isPressed(KeyEvent.VK_SPACE)) {
	// valid = true;
	// }
	// }
	//
	// });
	//
	// GraphicsEntity merged = new GraphicsEntity();
	// merged.attach(ground);
	// merged.attach(walls);
	// merged.attach(solids);
	// merged = grid.renderedOpaqueEntity(Color.white, merged);
	// merged.index(-10);
	// grid.attach(merged);
	//
	// // Attach child
	// grid.attach(player);
	//
	// return grid;
	// }

}
