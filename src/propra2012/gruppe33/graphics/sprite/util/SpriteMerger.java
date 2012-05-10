package propra2012.gruppe33.graphics.sprite.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 
 * @author Matthias Hesse
 */
public class SpriteMerger {

	private static final FilenameFilter imageFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			for (String extension : okFileExtensions) {
				if (name.toLowerCase().endsWith(extension)) {
					return true;
				}
			}
			return false;
		}
	};

	private static final String[] okFileExtensions = new String[] { "jpg",
			"png", "gif", "bmp" };

	public static BufferedImage[] readImages(File[] fileArray)
			throws IOException {
		BufferedImage[] subImages = new BufferedImage[fileArray.length];
		for (int i = 0; i < subImages.length; i++) {
			subImages[i] = ImageIO.read(fileArray[i]);
		}
		return subImages;
	}

	public static Color readBackgroundColor(BufferedImage image) {
		return new Color(image.getRGB(0, 0));
	}

	public static BufferedImage mergeImages(BufferedImage[] imageArray) {

		int xSize = 128;
		int ySize = 128;
		int num = imageArray.length;
		int plates = (int) Math.ceil(Math.sqrt(num));
		BufferedImage newImage = new BufferedImage(plates * xSize, plates
				* ySize, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = newImage.createGraphics();
		g.setBackground(new Color(0, 0, 0, 0));
		g.clearRect(0, 0, newImage.getWidth(), newImage.getHeight());
		try {
			int x = 0;
			for (int i = 0; i < plates; i++) {
				for (int j = 0; j < plates; j++) {
					if (imageArray.length > x) {

						int offx = (128 - imageArray[x].getWidth()) / 2;
						int offy = (128 - imageArray[x].getHeight()) / 2;

						g.drawImage(imageArray[x], offy + ySize * j, offx
								+ xSize * i, null);
					}
					x++;
				}
			}
		} finally {
			g.dispose();
		}
		return newImage;
	}

	public static BufferedImage setTransparency(BufferedImage dirtyImage,
			Color backgroundColor) {
		BufferedImage cleanImage = dirtyImage;
		for (int i = 0; i < cleanImage.getHeight(); i++) {
			for (int j = 0; j < cleanImage.getWidth(); j++) {
				if (new Color(cleanImage.getRGB(i, j)).equals(backgroundColor)) {
					cleanImage.setRGB(i, j, new Color(0, 0, 0, 0).getRGB());
				}
			}
		}

		return cleanImage;
	}

	public static void saveImage(BufferedImage image, String name)
			throws IOException {
		ImageIO.write(image, "png", new File(name));
	}

	public static void main(String[] args) throws IOException {

		if (args.length < 2) {
			throw new IllegalArgumentException(
					"You must specify at least 2 args: The path and the name.");
		}

		// Get the absolute dir
		File dir = new File(args[0]).getAbsoluteFile();

		// Filter the images
		File[] images = dir.listFiles(imageFilter);

		// Read sub images
		BufferedImage[] subImages = readImages(images);

		// Merge to big image
		BufferedImage newImage = mergeImages(subImages);

		// Make bg transparent
		newImage = setTransparency(newImage, readBackgroundColor(subImages[0]));

		// Finally safe
		saveImage(newImage, args[1]);
	}

}