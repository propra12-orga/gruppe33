package propra2012.gruppe33.assets;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import propra2012.gruppe33.graphics.rendering.scenegraph.grid.Grid;
import propra2012.gruppe33.graphics.rendering.scenegraph.grid.GridLoader;
import propra2012.gruppe33.graphics.sprite.Sprite;

/**
 * This class simplifies the handling of resources. Let it be images, maps,
 * sounds or something else. All useable assets are located in THIS package.
 * 
 * @author Christopher Probst
 */
public final class AssetManager {

	/**
	 * Loads a grid from the asset path.
	 * 
	 * @param name
	 *            The name of the grid.
	 * @param jarPath
	 *            The grid path inside the jar file.
	 * @param pixelWidth
	 *            The pixel width of the grid scene.
	 * @param pixelHeight
	 *            The pixel height of the grid scene.
	 * @return the loaded grid.
	 * @throws IOException
	 *             If an I/O error occures.
	 */
	public static Grid loadGrid(String name, String jarPath, int pixelWidth,
			int pixelHeight) throws IOException {
		return new Grid(name, pixelWidth, pixelHeight,
				GridLoader.load(open(jarPath)));
	}

	/**
	 * Loads a sprite from the asset path.
	 * 
	 * @param jarPath
	 *            The sprite path inside the jar file.
	 * @param rasterX
	 *            The columns of the sprite.
	 * @param rasterY
	 *            The rows of the sprite.
	 * @return the loaded sprite.
	 * @throws IOException
	 *             If an I/O error occures.
	 */
	public static Sprite loadSprite(String jarPath, int rasterX, int rasterY)
			throws IOException {
		// Load an image an create a new sprite
		return new Sprite(loadImage(jarPath), rasterX, rasterY);
	}

	/**
	 * Loads an image from the asset path.
	 * 
	 * @param jarPath
	 *            The image path inside the jar file.
	 * @return the loaded image.
	 * @throws IOException
	 *             If an I/O error occures.
	 */
	public static BufferedImage loadImage(String jarPath) throws IOException {
		return ImageIO.read(open(jarPath));
	}

	/**
	 * Opens an input stream to an asset.
	 * 
	 * @param jarPath
	 *            The
	 * @return the opened input stream.
	 * @throws IOException
	 *             If an I/O error occures.
	 */
	public static InputStream open(String jarPath) throws IOException {
		// Open the resource
		return AssetManager.class.getResourceAsStream(jarPath);
	}

	// Should be instantiated
	private AssetManager() {
	}
}
