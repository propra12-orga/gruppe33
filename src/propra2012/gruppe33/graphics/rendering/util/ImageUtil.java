package propra2012.gruppe33.graphics.rendering.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;

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
	 * @return the new compatible image.
	 */
	public static BufferedImage createImage(int width, int height) {
		return getGC().createCompatibleImage(width, height);
	}

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
		return getGC().createCompatibleImage(width, height, transparency);
	}

	/**
	 * Creates a compatible volatile image.
	 * 
	 * @param width
	 *            The width of the new volatile image.
	 * @param height
	 *            The height of the new volatile image.
	 * @return the new compatible volatile image.
	 */
	public static VolatileImage createVolatileImage(int width, int height) {
		return getGC().createCompatibleVolatileImage(width, height);
	}

	/**
	 * Creates a compatible volatile image.
	 * 
	 * @param width
	 *            The width of the new volatile image.
	 * @param height
	 *            The height of the new volatile image.
	 * @param transparency
	 *            The transparency of the new volatile image.
	 * @return the new compatible volatile image.
	 */
	public static VolatileImage createVolatileImage(int width, int height,
			int transparency) {
		return getGC().createCompatibleVolatileImage(width, height,
				transparency);
	}

	/**
	 * @return the active graphics configuration.
	 */
	public static GraphicsConfiguration getGC() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().getDefaultConfiguration();
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
		x = Mathf.clamp(x, 0, rasterX - 1);
		y = Mathf.clamp(y, 0, rasterY - 1);

		// Calculating the size of one sub image
		int sizeXPlate = image.getWidth() / rasterX;
		int sizeYPlate = image.getHeight() / rasterY;

		// Create compatible transparent image
		BufferedImage subImage = createImage(sizeXPlate, sizeYPlate,
				Transparency.TRANSLUCENT);

		// Get graphics object
		Graphics2D g2d = subImage.createGraphics();

		try {

			// Draw image to compatible image
			g2d.drawImage(image, 0, 0, sizeXPlate, sizeYPlate, x * sizeXPlate,
					y * sizeYPlate, (x + 1) * sizeXPlate, (y + 1) * sizeYPlate,
					new Color(0, 0, 0, 0), null);

		} finally {
			g2d.dispose();
		}

		// Creates the actual sub image
		return image.getSubimage(x * sizeXPlate, y * sizeYPlate, sizeXPlate,
				sizeYPlate);
	}
}
