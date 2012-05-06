package propra2012.gruppe33;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import propra2012.gruppe33.assets.AssetManager;
import propra2012.gruppe33.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.graphics.rendering.scenegraph.grid.Grid;
import propra2012.gruppe33.routines.AnimationRoutines;
import propra2012.gruppe33.routines.PlayerRoutines;

/**
 * 
 * @author Christopher Probst
 */
public class PreMilestoneApp {

	public static Grid createDemoGame() throws Exception {

		// Load grid from file
		final Grid grid = AssetManager.loadGrid("standard", "maps/map.txt",
				2048, 2048);

		// Define the chars on which the character can move
		grid.getMaxFieldVelocities().put('0', 600f);
		grid.getMaxFieldVelocities().put('2', 300f);

		// Render solid block
		BufferedImage s = AssetManager.loadImage("images/solid.png");

		Map<Character, BufferedImage> map = new HashMap<Character, BufferedImage>();
		map.put('1', s);

		// Bundle render to picture
		Entity solid = grid.bundleAndRender("solid", map);

		// Create a new local player
		Entity player = PlayerRoutines.createLocalPlayer("Kr0e",
				AnimationRoutines.createKnight(33), grid, 1, 1);

		player.getScale().scaleLocal(2);

		// Attach solid blocks
		grid.attach(solid);

		// Attach child
		grid.attach(player);

		return grid;
	}
}
