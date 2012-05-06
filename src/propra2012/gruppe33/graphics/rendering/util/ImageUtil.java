package propra2012.gruppe33.graphics.rendering.util;

import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

import propra2012.gruppe33.graphics.rendering.scenegraph.Mathf;
import propra2012.gruppe33.graphics.sprite.Sprite;

/**
 * Utility class for managing images.
 * 
 * @author Christopher Probst
 * 
 */
public class ImageUtil {

	/**
	 * Creates a compatible image.
	 * 
	 * @param width
	 *            The width of the new image.
	 * @param height
	 *            The height of the new image.
	 * @param transparency
	 *            The transparency of the new image.
	 * @return the new compatible image.
	 */
	public static BufferedImage createImage(int width, int height,
			int transparency) {
		return GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().getDefaultConfiguration()
				.createCompatibleImage(width, height, transparency);
	}

	/**
	 * Reads a sprite sub image.
	 * 
	 * @param image
	 *            The image which contains the sub images.
	 * @param rasterX
	 *            The columns of the image.
	 * @param rasterY
	 *            The rows of the image.
	 * @param x
	 *            The column of the sub image.
	 * @param y
	 *            The row of the sub image.
	 * @return the sub image.
	 * @see Sprite
	 */
	public static BufferedImage getSpriteSubImage(BufferedImage image,
			int rasterX, int rasterY, int x, int y) {

		if (image == null) {
			throw new NullPointerException("image");
		} else if (rasterX <= 0) {
			throw new IllegalArgumentException("rasterX must be > 0");
		} else if (rasterY <= 0) {
			throw new IllegalArgumentException("rasterY must be > 0");
		}

		// Clamp the coords
		x = Mathf.clamp(x, 0, rasterX);
		y = Mathf.clamp(y, 0, rasterY);

		// Calculating the size of one sub image
		int sizeXPlate = image.getWidth() / rasterX;
		int sizeYPlate = image.getHeight() / rasterY;

		// Creates the actual sub image
		return image.getSubimage(x * sizeXPlate, y * sizeYPlate, sizeXPlate,
				sizeYPlate);
	}
}
