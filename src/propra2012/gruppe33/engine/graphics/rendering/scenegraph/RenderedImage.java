package propra2012.gruppe33.engine.graphics.rendering.scenegraph;

import java.awt.Graphics2D;
import java.awt.Image;

import propra2012.gruppe33.engine.resources.Resource;

/**
 * This entity simply renders a centered image. You MUST specify the correct
 * scale for the image, because this entity always renders images with a width
 * and height of ONE.
 * 
 * @author Christopher Probst
 * @see GraphicsEntity
 */
public class RenderedImage extends GraphicsEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * Used for rendering.
	 */
	private Resource<? extends Image> imageResource;

	/*
	 * Is the image centered ?
	 */
	private boolean centered = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * propra2012.gruppe33.engine.graphics.rendering.scenegraph.GraphicsEntity
	 * #onRender(java.awt.Graphics2D, java.awt.Graphics2D)
	 */
	@Override
	protected void onRender(Graphics2D original, Graphics2D transformed) {

		// Does the image exist ?
		if (imageResource != null) {

			// Is the image centered ?
			if (centered) {
				// Center the image
				transformed.translate(-0.5, -0.5);
			}

			// Try to get image
			Image image = imageResource.get();

			// Check for null image
			if (image == null) {
				throw new IllegalStateException("The image resource get() "
						+ "method cannot return null. "
						+ "Please check your code.");
			}

			// Draw image
			transformed.drawImage(image, 0, 0, 1, 1, null);
		}
	}

	/**
	 * Creates an empty image controller with no image.
	 */
	public RenderedImage() {
		this(null);
	}

	/**
	 * Creates a new image controller using the given image resource.
	 * 
	 * @param imageResource
	 *            The image resource of the image controller.
	 */
	public RenderedImage(Resource<? extends Image> imageResource) {
		imageResource(imageResource);
	}

	/**
	 * @return the centered flag.
	 */
	public boolean isCentered() {
		return centered;
	}

	/**
	 * Sets the centered flag.
	 * 
	 * @param centered
	 *            True if you want to center the image, otherwise false.
	 * @return this for chaining.
	 */
	public RenderedImage centered(boolean centered) {
		this.centered = centered;
		return this;
	}

	/**
	 * @return the image resource.
	 */
	public Resource<? extends Image> imageResource() {
		return imageResource;
	}

	/**
	 * Sets the image resource.
	 * 
	 * @param imageResource
	 *            The image resource you want to set.
	 * @return this for chaining.
	 */
	public RenderedImage imageResource(Resource<? extends Image> imageResource) {
		this.imageResource = imageResource;
		return this;
	}
}
