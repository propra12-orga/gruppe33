package propra2012.gruppe33.graphics.rendering.util;

import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

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

}
