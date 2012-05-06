package propra2012.gruppe33;

import java.io.IOException;

import javax.swing.JFrame;

import propra2012.gruppe33.assets.AssetManager;
import propra2012.gruppe33.graphics.rendering.passive.JGridRenderer;
import propra2012.gruppe33.graphics.rendering.scenegraph.grid.Grid;
import propra2012.gruppe33.graphics.rendering.scenegraph.grid.GridLoader;

/**
 * 
 * @author Christopher Probst
 */
public class AppStart {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		// Create grid
		final Grid grid = new Grid("standard", 2048, 2048,
				GridLoader.load(AssetManager.open("maps/map.txt")));

		// Create new level renderer
		final JGridRenderer renderer = new JGridRenderer(grid);

		// Start rendering
		renderer.start();

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(renderer);
		frame.setSize(500, 500);
		frame.setVisible(true);
	}
}
