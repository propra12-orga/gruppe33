package propra2012.gruppe33.graphics.rendering.scenegraph.image;

import java.awt.Graphics2D;
import java.awt.Image;

import propra2012.gruppe33.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.graphics.rendering.scenegraph.EntityController;
import propra2012.gruppe33.graphics.rendering.scenegraph.EntityControllerAdapter;
import propra2012.gruppe33.resources.Resource;

/**
 * This class simply renders a centered image. You MUST specify the correct
 * scale for the image, because this entity always renders images with a width
 * and height of ONE.
 * 
 * @author Christopher Probst
 * @see EntityController
 * @see Entity
 */
public class ImageController extends EntityControllerAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * Used for rendering.
	 */
	private Resource<? extends Image> imageResource;

	/**
	 * Creates an empty image controller with no image.
	 */
	public ImageController() {
	}

	/**
	 * Creates a new image controller using the given image resource.
	 * 
	 * @param imageResource
	 *            The image resource of the image controller.
	 */
	public ImageController(Resource<? extends Image> imageResource) {
		setImageResource(imageResource);
	}

	/**
	 * @return the image resource.
	 */
	public Resource<? extends Image> getImageResource() {
		return imageResource;
	}

	/**
	 * Sets the image resource.
	 * 
	 * @param imageResource
	 *            The image resource you want to set.
	 */
	public void setImageResource(Resource<? extends Image> imageResource) {
		this.imageResource = imageResource;
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
		if (imageResource != null) {

			// Create a copy...
			Graphics2D copy = (Graphics2D) transformed.create();
			try {
				// Center the image
				copy.translate(-0.5, -0.5);

				// Try to get image
				Image image = imageResource.get();

				// Check for null image
				if (image == null) {
					throw new IllegalStateException("The image resource get() "
							+ "method cannot return null. "
							+ "Please check your code.");
				}

				// Draw image
				copy.drawImage(image, 0, 0, 1, 1, null);
			} finally {
				// Always dispose
				copy.dispose();
			}
		}
	}
}
