package propra2012.gruppe33;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import propra2012.gruppe33.graphics.assets.AssetManager;
import propra2012.gruppe33.graphics.rendering.EntityRoutines;
import propra2012.gruppe33.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.graphics.rendering.scenegraph.EntityController;
import propra2012.gruppe33.graphics.rendering.scenegraph.EntityControllerAdapter;
import propra2012.gruppe33.graphics.rendering.scenegraph.grid.Grid;
import propra2012.gruppe33.graphics.rendering.scenegraph.math.Vector2f;
import propra2012.gruppe33.graphics.sprite.AnimationRoutines;
import propra2012.gruppe33.graphics.sprite.Sprite;

/**
 * 
 * @author Christopher Probst
 */
public class PreMilestoneApp {

	public static Grid createDemoGame() throws Exception {

		AssetManager assets = new AssetManager(new File("scenes/default.zip"));

		// Load grid from file
		final Grid grid = new Grid("root", assets, "assets/maps/map.txt", 2048,
				2048);

		// Define the chars on which the character can move
		grid.getMaxFieldVelocities().put('0', 600f);
		grid.getMaxFieldVelocities().put('2', 300f);

		// Copy the default chars
		grid.getDefaultCollectChars().addAll(
				grid.getMaxFieldVelocities().keySet());

		// Render solid block
		BufferedImage s = grid.getAssetManager().loadImage(
				"assets/images/solid.png");

		Map<Character, BufferedImage> map = new HashMap<Character, BufferedImage>();
		map.put('1', s);

		// Bundle render to picture
		Entity solid = grid.bundleAndRender("solid", map);

		// Create a new local player
		Entity player = EntityRoutines.createLocalPlayer("Kr0e",
				AnimationRoutines.createKnight(grid.getAssetManager(), 20),
				grid, 1, 1);

		// Load sprite
		Sprite boom = grid.getAssetManager().loadSprite(
				"assets/images/animated/boom.png", 5, 5);

		Entity ex = EntityRoutines.createBoom(grid, boom, 1, 1, 5);

		player.getScale().scaleLocal(2);

		// Attach solid blocks
		grid.attach(solid);

		// Attach child
		grid.attach(player);

		grid.attach(ex);

		player.putController(new EntityControllerAdapter() {

			@Override
			public void doRender(Entity entity, Graphics2D original,
					Graphics2D transformed) {

				// Get grid in parent hierarchy
				Grid grid = entity.findParentByClass(Grid.class);

				if (grid != null) {
					List<Point> points = grid.collectPoints(
							grid.worldToNearestPoint(entity.getPosition()), 3,
							null);

					for (Point p : points) {
						Vector2f v = grid.vectorAt(p);

						original.translate(v.x, v.y);
						original.setColor(Color.blue);
						original.fillArc(-10, -10, 20, 20, 0, 360);

						original.translate(-v.x, -v.y);
					}
				}
			}
		});

		return grid;
	}
}
