package propra2012.gruppe33.graphics.sprite;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import propra2012.gruppe33.graphics.GraphicsRoutines;
import propra2012.gruppe33.graphics.rendering.scenegraph.math.Mathf;
import propra2012.gruppe33.resources.Resource;

/**
 * 
 * A sprite is basically an image which contains sub-images which is typically
 * used for animations.
 * 
 * @author Matthias Hesse
 * @see Animation
 * @see AnimationMap
 */
public final class Sprite implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Here we store all sub images
	private transient BufferedImage[][] subImages;

	// The sprite image resource
	private final Resource<BufferedImage> imageResource;

	// Here we store the raster information
	private final int rasterX, rasterY;

	/*
	 * When deserializing load the sub-images directly.
	 */
	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {

		// Read the default stuff
		in.defaultReadObject();

		try {
			// Try to load the images
			loadSubImages();
		} catch (Exception e) {
			throw new IOException("Failed to load sub images", e);
		}
	}

	/**
	 * Loads the sub-images.
	 * 
	 * @throws Exception
	 *             If an exception occurs.
	 */
	private void loadSubImages() throws Exception {
		// Create new 2-dim array containing all sub-images
		subImages = new BufferedImage[rasterY][rasterX];

		// Iterate for x and y
		for (int x = 0; x < rasterX; x++) {
			for (int y = 0; y < rasterY; y++) {
				// Create sub-image
				subImages[y][x] = GraphicsRoutines.getSpriteSubImage(
						imageResource.get(), rasterX, rasterY, x, y);
			}
		}
	}

	/**
	 * Creates a sprite.
	 * 
	 * @param imageResource
	 *            The sprite image resource.
	 * @param rasterX
	 *            The columns of the sprite.
	 * @param rasterY
	 *            The rows of the sprite.
	 * @throws Exception
	 *             If an exception occurs.
	 */
	public Sprite(Resource<BufferedImage> imageResource, int rasterX,
			int rasterY) throws Exception {
		if (imageResource == null) {
			throw new NullPointerException("imageResource");
		} else if (rasterX <= 0) {
			throw new IllegalArgumentException("rasterX must be > 0");
		} else if (rasterY <= 0) {
			throw new IllegalArgumentException("rasterY must be > 0");
		}

		// Save vars
		this.imageResource = imageResource;
		this.rasterX = rasterX;
		this.rasterY = rasterY;

		// Load the sub images
		loadSubImages();
	}

	public Resource<BufferedImage> getImageResource() {
		return imageResource;
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

		// Create a list holding the image coords
		List<Point> imageCoords = new ArrayList<Point>(count);

		// Iteration vars
		int x = fromX, y = fromY;

		// Iterate
		for (int i = 0; i < count; i++) {

			// Save the coord
			imageCoords.add(new Point(y, x));

			// Inc
			x++;

			// Adjust
			if (x >= rasterX) {
				x = 0;
				y++;
			}
		}

		// Create a new instance of animation
		return new Animation(this, name, imageCoords, timePerImage);
	}

	/**
	 * Creates a new animation using the given points.
	 * 
	 * @param name
	 *            The name of the new animation.
	 * @param timePerImage
	 *            The time-per-frame of the animation.
	 * @param coords
	 *            The coords of the sub images.
	 * @return a new animation containig the given sub images.
	 */
	public Animation newAnimation(String name, long timePerImage,
			Point... coords) {

		// Create a list holding the image coords
		List<Point> imageCoords = new ArrayList<Point>(coords.length);

		// Iterate over all coords
		for (Point coord : coords) {
			// Save coord
			imageCoords.add(coord);
		}

		// Create a new instance of animation
		return new Animation(this, name, imageCoords, timePerImage);
	}
}
