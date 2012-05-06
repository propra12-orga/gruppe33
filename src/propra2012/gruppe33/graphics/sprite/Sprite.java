package propra2012.gruppe33.graphics.sprite;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import propra2012.gruppe33.graphics.rendering.util.ImageUtil;

/**
 * 
 * This class represents a sprite. A sprite is basically an image which contains
 * sub-images. This is typically used for animations but you can use this as a
 * bundle of images, too.
 * 
 * @author Matthias Hesse
 * @see Animation
 * @see AnimationMap
 */
public class Sprite {

	// Here we store all sub images
	private final BufferedImage[][] subImages;

	// Here we store the raster information
	private final int rasterX, rasterY;

	/**
	 * Creates a sprite.
	 * 
	 * @param image
	 *            The sprite image.
	 * @param rasterX
	 *            The columns of the sprite.
	 * @param rasterY
	 *            The rows of the sprite.
	 */
	public Sprite(BufferedImage image, int rasterX, int rasterY) {
		if (image == null) {
			throw new NullPointerException("image");
		} else if (rasterX <= 0) {
			throw new IllegalArgumentException("rasterX must be > 0");
		} else if (rasterY <= 0) {
			throw new IllegalArgumentException("rasterY must be > 0");
		}

		// Save vars
		this.rasterX = rasterX;
		this.rasterY = rasterY;

		// Create new 2-dim array containing all sub-images
		subImages = new BufferedImage[rasterY][rasterX];

		// Iterate for x and y
		for (int x = 0; x < rasterX; x++) {
			for (int y = 0; y < rasterY; y++) {
				// Create sub-image
				subImages[y][x] = ImageUtil.getSpriteSubImage(image, rasterX,
						rasterY, x, y);
			}
		}
	}

	/**
	 * @return the sub images.
	 */
	public BufferedImage[][] getSubImages() {
		return subImages;
	}

	/**
	 * @return the columns of the sprite.
	 */
	public int getRasterX() {
		return rasterX;
	}

	/**
	 * @return the rows of the sprite.
	 */
	public int getRasterY() {
		return rasterY;
	}

	public Animation newAnimationFromRange(String name, long timePerImage,
			int fromX, int fromY, int count) {

		// Create a new List for every sub-image
		List<BufferedImage> animationImages = new ArrayList<BufferedImage>();

		// Iteration vars
		int x = fromX, y = fromY;

		// Iterate
		for (int i = 0; i < count; i++) {

			// Just take the the already created sub-images
			animationImages.add(subImages[y][x]);

			// Inc
			x++;

			// Adjust
			if (x >= rasterX) {
				x = 0;
				y++;
			}
		}

		// Create a new instance of animation
		return new Animation(name, animationImages, timePerImage);
	}

	/**
	 * This method creates a new animation instance using the already created
	 * sub-images. This is the key-feature for fast rendering.
	 * 
	 * @param name
	 * @param timePerImage
	 * @param points
	 * @return
	 */
	public Animation newAnimation(String name, long timePerImage,
			Point... points) {

		// Create a new List for every sub-image
		List<BufferedImage> animationImages = new ArrayList<BufferedImage>(
				points.length);

		// Creating every single SubImage
		for (int i = 0; i < points.length; i++) {
			// Just take the the already created sub-images
			animationImages.add(subImages[points[i].y][points[i].x]);
		}

		// Create a new instance of animation
		return new Animation(name, animationImages, timePerImage);
	}
}
