package propra2012.gruppe33;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import propra2012.gruppe33.assets.AssetManager;
import propra2012.gruppe33.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.graphics.rendering.scenegraph.EntityController;
import propra2012.gruppe33.graphics.rendering.scenegraph.Vector2f;
import propra2012.gruppe33.graphics.rendering.scenegraph.grid.Grid;
import propra2012.gruppe33.graphics.rendering.scenegraph.transform.TransformController;
import propra2012.gruppe33.routines.AnimationRoutines;
import propra2012.gruppe33.routines.PlayerRoutines;

/**
 * 
 * @author Christopher Probst
 */
public class PreMilestoneApp {

	public static Grid createDemoGame() throws Exception {

		// Load grid from file
		final Grid grid = AssetManager.loadGrid("standard",
				"maps/smallmap.txt", 2048, 2048);

		
		// Define the chars on which the character can move
		grid.getMaxFieldVelocities().put('0', 600f);
		grid.getMaxFieldVelocities().put('2', 300f);

		grid.getDefaultCollectChars().addAll(
				grid.getMaxFieldVelocities().keySet());

		// Render solid block
		BufferedImage s = AssetManager.loadImage("images/solid.png");

		Map<Character, BufferedImage> map = new HashMap<Character, BufferedImage>();
		map.put('1', s);

		// Bundle render to picture
		Entity solid = grid.bundleAndRender("solid", map);

		// Create a new local player
		Entity player = PlayerRoutines.createLocalPlayer("Kr0e",
				AnimationRoutines.createKnight(20), grid, 1, 1);

		player.putController(new EntityController() {

			@Override
			public void doUpdate(Entity entity, float tpf) {
				// TODO Auto-generated method stub

			}

			@Override
			public void doRender(Entity entity, Graphics2D original,
					Graphics2D transformed) {

				// Get grid in parent hierarchy
				Grid grid = entity.findParentEntity(Grid.class);

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

		player.getScale().scaleLocal(2);

		// Attach solid blocks
		grid.attach(solid);

		// Attach child
		grid.attach(player);

		return grid;
	}
}
