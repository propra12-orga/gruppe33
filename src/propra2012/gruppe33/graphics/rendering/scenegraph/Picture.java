package propra2012.gruppe33.graphics.rendering.scenegraph;

import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * This class simply renders a centered image. You MUST specify the correct
 * scale for the image, because this entity always renders images with a width
 * and height of ONE.
 * 
 * @author Christopher Probst
 * @see Entity
 */
public class Picture extends Entity {

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

	// Used for rendering
	private Image image;

	/**
	 * Creates a new empty picture.
	 * 
	 * @param name
	 *            The name of the picture.
	 */
	public Picture(String name) {
		this(name, null);
	}

	/**
	 * Creates a new picture.
	 * 
	 * @param name
	 *            The name of the picture.
	 * @param width
	 *            The width of the new image.
	 * @param height
	 *            The height of the new image.
	 * @param transparency
	 *            The transparency of the new image.
	 */
	public Picture(String name, int width, int height, int transparency) {
		this(name, createImage(width, height, transparency));
	}

	/**
	 * Creates a new picture.
	 * 
	 * @param name
	 *            The name of the picture.
	 * @param image
	 *            The image of the picture.
	 */
	public Picture(String name, Image image) {
		super(name);
		setImage(image);
	}

	/**
	 * @return the image.
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * Sets the image.
	 * 
	 * @param image
	 *            The image you want to set.
	 */
	public void setImage(Image image) {
		this.image = image;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * propra2012.gruppe33.graphics.rendering.scenegraph.Entity#doRender(java
	 * .awt.Graphics2D, java.awt.Graphics2D)
	 */
	@Override
	protected void doRender(Graphics2D original, Graphics2D transformed) {
		super.doRender(original, transformed);

		// Does the image exist ?
		if (image != null) {

			// Create a copy...
			Graphics2D copy = (Graphics2D) transformed.create();
			try {
				// Center the image
				copy.translate(-0.5, -0.5);

				// Draw image
				copy.drawImage(image, 0, 0, 1, 1, null);
			} finally {
				// Always dispose
				copy.dispose();
			}
		}
	}
}
