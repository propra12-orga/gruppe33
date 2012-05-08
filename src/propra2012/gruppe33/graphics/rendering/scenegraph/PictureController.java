package propra2012.gruppe33.graphics.rendering.scenegraph;

import java.awt.Graphics2D;
import java.awt.Image;

import propra2012.gruppe33.graphics.rendering.util.ImageUtil;

/**
 * This class simply renders a centered image. You MUST specify the correct
 * scale for the image, because this entity always renders images with a width
 * and height of ONE.
 * 
 * @author Christopher Probst
 * @see EntityController
 * @see Entity
 */
public class PictureController extends EntityControllerAdapter {

	// Used for rendering
	private Image image;

	/**
	 * Creates a new picture.
	 * 
	 * @param width
	 *            The width of the new image.
	 * @param height
	 *            The height of the new image.
	 * @param transparency
	 *            The transparency of the new image.
	 */
	public PictureController(int width, int height, int transparency) {
		this(ImageUtil.createImage(width, height, transparency));
	}

	/**
	 * Creates a new picture.
	 * 
	 * @param image
	 *            The image of the picture.
	 */
	public PictureController(Image image) {
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
	 * propra2012.gruppe33.graphics.rendering.scenegraph.EntityController#doRender
	 * (propra2012.gruppe33.graphics.rendering.scenegraph.Entity,
	 * java.awt.Graphics2D, java.awt.Graphics2D)
	 */
	@Override
	public void doRender(Entity entity, Graphics2D original,
			Graphics2D transformed) {

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
