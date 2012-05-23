package propra2012.gruppe33;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import propra2012.gruppe33.bomberman.graphics.rendering.EntityRoutines;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.Grid;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridLoader;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.GraphicsEntity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Scene;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.SceneProcessor;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Text;
import propra2012.gruppe33.engine.graphics.sprite.Sprite;
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

	public static Grid createDemoGame() throws Exception {

		AssetManager assets = new AssetManager(new File("scenes/default.zip"));

		// Load grid from file
		final Grid grid = new Grid(assets, "assets/maps/smallmap.txt", 2048,
				2048);

		GridLoader.generate(grid.mapData(), System.nanoTime());

		// Define the chars on which the character can move
		grid.maxFieldVelocities().put('0', 600f);
		grid.maxFieldVelocities().put('s', 600f);
		// grid.maxFieldVelocities().put((char) ('0' + 1000), 600f);

		// Same as vec fields
		grid.defaultCollectChars().addAll(grid.maxFieldVelocities().keySet());
		grid.defaultCollectChars().add((char) ('0' + 1000));

		grid.defaultLineOfSightChars().add('0');
		grid.defaultLineOfSightChars().add('s');
		// grid.defaultLineOfSightChars().add((char) ('0' + 1000));

		// The explosion sprite
		final Sprite explosion = new Sprite(assets.loadImage(
				"assets/images/animated/boom.png", true), 5, 5);

		final Asset<BufferedImage> bomb = assets.loadImage(
				"assets/images/bomb.png", true);

		final Asset<BufferedImage> breakable = assets.loadImage(
				"assets/images/break.png", true);

		Map<Character, Asset<BufferedImage>> m = new HashMap<Character, Asset<BufferedImage>>();
		m.put((char) ('0' + 1000), breakable);

		GraphicsEntity b = grid.bundle(m);
		b.index(1);

		List<Point> ptr = new ArrayList<Point>();
		for (int y = 1; y < grid.mapData().length - 1; y++) {
			for (int x = 1; x < grid.mapData()[0].length - 1; x++) {
				if (grid.mapData()[y][x] >= 1000) {
					ptr.add(new Point(x, y));
				}
			}
		}

		Random r = new Random();
		EntityRoutines.randomExit = ptr.get(r.nextInt(ptr.size()));
		EntityRoutines.exit = assets
				.loadImage("assets/images/escape.png", true);

		for (Entity e : b) {
			GraphicsEntity ge = (GraphicsEntity) e;
			Point p = grid.worldToNearestPoint(ge.position());
			EntityRoutines.items.put(p, ge);

			grid.attach(ge);
		}

		// Create ground
		Entity ground = EntityRoutines.createGround(grid);

		// Create walls
		Entity walls = EntityRoutines.createWalls(grid);

		// Create the solid blocks
		Entity solids = EntityRoutines.createSolidBlocks(grid);

		// Create a new local player
		GraphicsEntity player = EntityRoutines.createPlayer("Kr0e", grid, 1, 1);

		player.attach(new Entity() {

			boolean valid = true;

			@Override
			protected void onUpdate(float tpf) {
				super.onUpdate(tpf);

				if (grid.isPressed(KeyEvent.VK_SPACE) && valid) {
					valid = false;
					Point p = grid
							.worldToNearestPoint(((GraphicsEntity) parent())
									.position());

					EntityRoutines.createBomb(grid, explosion, bomb, p.x, p.y,
							30, 3, 3);

				} else if (!grid.isPressed(KeyEvent.VK_SPACE)) {
					valid = true;
				}
			}

		});
		
		GraphicsEntity merged = new GraphicsEntity();
		merged.attach(ground);
		merged.attach(walls);
		merged.attach(solids);
		merged = grid.renderedOpaqueEntity(Color.white, merged);
		merged.index(-10);
		grid.attach(merged);

		// Attach child
		grid.attach(player);

		// EntityRoutines.PLAYER = player;
		//
		// player.putController(new DefaultEntityController() {
		//
		// boolean valid = true;
		//
		// @Override
		// public void doUpdate(Entity entity, float tpf) {
		//
		// final Grid grid = (Grid) entity.findParent(new TypeFilter(
		// Grid.class, false), true);
		//
		// if (grid != null) {
		//
		// if (grid.isPressed(KeyEvent.VK_SPACE)) {
		//
		// final Point exp = grid.worldToNearestPoint(entity
		// .getPosition());
		//
		// if (valid && !EntityRoutines.BOMBS.containsKey(exp)) {
		// valid = false;
		//
		// final char oldChar = grid.charAt(exp, '3');
		//
		// Timeout tc = new Timeout(3);
		//
		// EntityController ec = new DefaultEntityController() {
		//
		// public void onMessage(Entity entity,
		// Object message, Object[] args) {
		//
		// if (message == TimeoutController.Timeout.Timeout) {
		// Entity bomb = EntityRoutines
		// .createBomb(grid, boom,
		// entity.getName()
		// + " bomb", exp,
		// 10, 0.25f);
		//
		// Entity p = entity.getParent();
		//
		// entity.detach();
		//
		// p.attach(bomb);
		//
		// grid.charAt(exp, oldChar);
		//
		// }
		// }
		// };
		//
		// EntityRoutines.BOMBS.put(exp, tc);
		//
		// Entity b = new Entity("waiting");
		// b.putController(new RenderedImage(bombImage));
		//
		// b.setOrder(-1);
		// b.getScale().set(grid.getRasterWidth(),
		// grid.getRasterHeight());
		//
		// b.putController(ec);
		// b.putController(tc);
		//
		// b.getPosition().set(grid.vectorAt(exp));
		//
		// entity.getParent().attach(b);
		//
		// b.getScale().scaleLocal(0.5f);
		// b.putController(new TransformMotor());
		// b.getController(TransformMotor.class)
		// .setScaleVelocity(new Vector2f(20f, 20f));
		//
		// }
		// } else {
		// valid = true;
		// }
		// }
		// }
		// });

		return grid;
	}
}
