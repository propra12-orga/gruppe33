package propra2012.gruppe33;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import propra2012.gruppe33.assets.AssetManager;
import propra2012.gruppe33.graphics.rendering.JGridRenderer;
import propra2012.gruppe33.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.graphics.rendering.scenegraph.animation.AnimationController;
import propra2012.gruppe33.graphics.rendering.scenegraph.grid.Grid;
import propra2012.gruppe33.graphics.rendering.scenegraph.grid.GridController;
import propra2012.gruppe33.routines.AnimationRoutines;
import propra2012.gruppe33.routines.PlayerRoutines;

/**
 * 
 * @author Christopher Probst
 */
public class PreMilestoneApp {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

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
		
		// Create new level renderer
		final JGridRenderer renderer = new JGridRenderer(grid);

		// Attach solid blocks
		renderer.getRoot().attach(solid);

		// Attach child
		renderer.getRoot().attach(player);

		// Start rendering
		renderer.start();

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(renderer);
		frame.setSize(500, 500);
		frame.setVisible(true);
	}
}
