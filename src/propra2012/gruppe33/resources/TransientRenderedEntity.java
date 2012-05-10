package propra2012.gruppe33.resources;

import java.awt.Color;
import java.awt.Image;
import java.io.IOException;
import java.io.ObjectInputStream;

import propra2012.gruppe33.graphics.GraphicsRoutines;
import propra2012.gruppe33.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.graphics.rendering.scenegraph.Scene;

/**
 * This class represents a "rendered entity". This means that you provide an
 * entity which is rendered in an image. But to reduce serialization overhead
 * this class only serializes the entity hierarchy instead of the image.
 * 
 * @author Christopher Probst
 * @see Resource
 * @see Entity
 */
public final class TransientRenderedEntity implements Resource<Image> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * The transient resource.
	 */
	private transient Image resource;
	private final int width, height, transparency;
	private final Color background;
	private final Entity entity;

	/**
	 * Renders the entity to an image.
	 */
	private void createResource() {
		// Create entity
		resource = entity.renderTo(GraphicsRoutines.clear(
				GraphicsRoutines.createImage(width, height, transparency),
				background));

		// Check entity
		if (resource == null) {
			throw new IllegalStateException("The resource is null. "
					+ "Please check your code.");
		}
	}

	/*
	 * When deserializing we want to recreate the resource.
	 */
	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {

		// Restore all vars
		in.defaultReadObject();

		// Recreate the resource
		createResource();
	}

	/**
	 * Creates a transient rendered entity using the given parameters.
	 * 
	 * @param scene
	 *            The width and height of the scene will be used.
	 * @param transparency
	 *            The transparency of the rendered image.
	 * @param background
	 *            The background of the rendered image.
	 * @param entity
	 *            The entity which is rendered into an image.
	 */
	public TransientRenderedEntity(Scene scene, int transparency,
			Color background, Entity entity) {
		this(scene.getWidth(), scene.getHeight(), transparency, background,
				entity);
	}

	/**
	 * Creates a transient rendered entity using the given parameters.
	 * 
	 * @param width
	 *            The width of the rendered image.
	 * @param height
	 *            The height of the rendered image.
	 * @param transparency
	 *            The transparency of the rendered image.
	 * @param background
	 *            The background of the rendered image.
	 * @param entity
	 *            The entity which is rendered into an image.
	 */
	public TransientRenderedEntity(int width, int height, int transparency,
			Color background, Entity entity) {

		if (entity == null) {
			throw new NullPointerException("entity");
		}

		// Save
		this.width = width;
		this.height = height;
		this.transparency = transparency;
		this.background = background;
		this.entity = entity;

		// Just create the resource
		createResource();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see propra2012.gruppe33.resources.Resource#get()
	 */
	@Override
	public Image get() {
		return resource;
	}
}
