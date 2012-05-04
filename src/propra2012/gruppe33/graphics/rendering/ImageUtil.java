package propra2012.gruppe33.graphics.rendering;

import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

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

	public static List<BufferedImage> loadWithSchema(File dir, String prefix,
			String postfix, int count) throws IOException {

		// Initialise an array list
		List<BufferedImage> images = new ArrayList<BufferedImage>(count);

		// Read all images
		for (int i = 0; i < count; i++) {
			images.add(ImageIO.read(new File(dir, prefix + i + postfix)));
		}

		return images;
	}

}
