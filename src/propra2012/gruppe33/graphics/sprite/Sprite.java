package propra2012.gruppe33.graphics.sprite;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import propra2012.gruppe33.graphics.GraphicsRoutines;
import propra2012.gruppe33.graphics.rendering.scenegraph.math.Mathf;

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
public final class Sprite {

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
				subImages[y][x] = GraphicsRoutines.getSpriteSubImage(image,
						rasterX, rasterY, x, y);
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

	/**
	 * Creates a new animation using a given range of sub images.
	 * 
	 * @param name
	 *            The name of the new animation.
	 * @param timePerImage
	 *            The time-per-frame of the animation.
	 * @param fromX
	 *            The x start component.
	 * @param fromY
	 *            The y start component.
	 * @param count
	 *            The number of sub images read step by step.
	 * @return a new animation containig the given sub images.
	 */
	public Animation newAnimationFromRange(String name, long timePerImage,
			int fromX, int fromY, int count) {

		if (count <= 0) {
			throw new IllegalArgumentException("count must be > 0");
		}

		// Clamp the coords
		fromX = Mathf.clamp(fromX, 0, rasterX - 1);
		fromY = Mathf.clamp(fromY, 0, rasterY - 1);

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
	 * Creates a new animation using the given points.
	 * 
	 * @param name
	 *            The name of the new animation.
	 * @param timePerImage
	 *            The time-per-frame of the animation.
	 * @param points
	 *            The points of the sub images.
	 * @return a new animation containig the given sub images.
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
