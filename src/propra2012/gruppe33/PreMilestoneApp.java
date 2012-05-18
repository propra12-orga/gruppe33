package propra2012.gruppe33;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import propra2012.gruppe33.bomberman.graphics.rendering.EntityRoutines;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.Grid;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridController;
import propra2012.gruppe33.bomberman.graphics.sprite.AnimationRoutines;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.EntityControllerAdapter;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.TypeFilter;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.timeout.TimeoutController;
import propra2012.gruppe33.engine.graphics.sprite.Sprite;
import propra2012.gruppe33.engine.resources.Resource;
import propra2012.gruppe33.engine.resources.assets.Asset;
import propra2012.gruppe33.engine.resources.assets.AssetManager;

/**
 * 
 * @author Christopher Probst
 */
public class PreMilestoneApp {

	public static Grid createDemoGame() throws Exception {

		AssetManager assets = new AssetManager(new File("scenes/default.zip"));

		// Load grid from file
		Grid grid = new Grid("root", assets, "assets/maps/smallmap.txt", 2048,
				2048);

		// Define the chars on which the character can move
		grid.getMaxFieldVelocities().put('0', 600f);
		grid.getMaxFieldVelocities().put('2', 300f);

		// Copy the default chars
		grid.getDefaultCollectChars().addAll(
				grid.getMaxFieldVelocities().keySet());

		// Render solid block
		Asset<BufferedImage> solidImage = grid.getAssetManager().loadImage(
				"assets/images/solid.png", true);

		Map<Character, Resource<? extends Image>> map = new HashMap<Character, Resource<? extends Image>>();
		map.put('1', solidImage);

		// Bundle render to picture
		Entity solid = grid.bundleToRenderedEntity("solid", map,
				Transparency.OPAQUE, Color.white);

		// Create a new local player
		Entity player = EntityRoutines.createFieldEntity("Kr0e",
				AnimationRoutines.createKnight(grid.getAssetManager(), 33),
				grid, 1, 1);

		player.putController(new GridController());

		// Load sprite
		final Sprite boom = new Sprite(grid.getAssetManager().loadImage(
				"assets/images/animated/boom.png", false), 5, 5);

		player.getScale().scaleLocal(2);

		solid.setOrder(-1);

		// Attach solid blocks
		grid.attach(solid);

		// Attach child
		grid.attach(player);

		player.putController(new EntityControllerAdapter() {

			boolean valid = true;

			@Override
			public void doUpdate(Entity entity, float tpf) {

				final Grid grid = (Grid) entity.findParent(new TypeFilter(
						Grid.class, false), true);

				System.out.println(grid.getChildrenCount());
				
				if (grid != null) {

					if (grid.isPressed(KeyEvent.VK_SPACE)) {

						if (valid) {
							valid = false;

							final Point exp = grid.worldToNearestPoint(entity
									.getPosition());

							TimeoutController tc = new TimeoutController(3f) {
								protected void onTimeout(Entity entity) {

									Entity bomb = EntityRoutines.createBomb(
											grid, boom, entity.getName()
													+ " bomb", exp, 3, 0.5f);

									entity.getParent().attach(bomb);

									entity.detach();
								};

								@Override
								public void doRender(Entity entity,
										Graphics2D original,
										Graphics2D transformed) {

									transformed.setColor(Color.BLACK);
									transformed.translate(-0.25f, -0.25f);
									transformed.scale(0.5f, 0.5f);
									transformed.fillArc(0, 0, 1, 1, 0, 360);
								}
							};

							Entity b = new Entity("waiting");

							b.setOrder(-1);
							b.getScale().set(grid.getRasterWidth(),
									grid.getRasterHeight());

							b.putController(tc);

							b.getPosition().set(grid.vectorAt(exp));

							entity.getParent().attach(b);
						}
					} else {
						valid = true;
					}
				}
			}
		});

		return grid;
	}
}
