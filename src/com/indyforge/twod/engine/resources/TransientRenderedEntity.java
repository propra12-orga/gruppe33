package com.indyforge.twod.engine.resources;

import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.Image;
import java.io.IOException;
import java.io.ObjectInputStream;

import com.indyforge.twod.engine.graphics.GraphicsRoutines;
import com.indyforge.twod.engine.graphics.ImageDesc;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.resources.assets.AssetManager;

/**
 * This class represents a "rendered entity". This means that you provide a
 * graphics entity which is rendered into an image. But to reduce serialization
 * overhead this class only serializes the entity hierarchy instead of the
 * image. This means you serialize the "construction" of the image instead of
 * the image.
 * 
 * @author Christopher Probst
 * @see Resource
 * @see GraphicsEntity
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
	private final ImageDesc imageDesc;
	private final Color background;
	private final GraphicsEntity graphicsEntity;

	/**
	 * Renders the graphics entity to an image.
	 */
	private void createResource() {
		if (!AssetManager.isHeadless()) {
			// Create resource
			resource = graphicsEntity.renderTo(GraphicsRoutines.clear(
					GraphicsRoutines.createImage(imageDesc), background));

			// Check resource
			if (resource == null) {
				throw new IllegalStateException("The resource is null. "
						+ "Please check your code.");
			}
		} else {
			resource = null;
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
	 * @param imageDesc
	 *            The image description.
	 * @param background
	 *            The background of the rendered image.
	 * @param graphicsEntity
	 *            The graphics entity which is rendered into an image.
	 */
	public TransientRenderedEntity(ImageDesc imageDesc, Color background,
			GraphicsEntity graphicsEntity) {

		if (graphicsEntity == null) {
			throw new NullPointerException("graphicsEntity");
		} else if (imageDesc == null) {
			throw new NullPointerException("imageDesc");
		}

		// Save
		this.imageDesc = imageDesc;
		this.background = background;
		this.graphicsEntity = graphicsEntity;

		// Just create the resource
		createResource();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.twod.engine.resources.Resource#get()
	 */
	@Override
	public Image get() {
		if (AssetManager.isHeadless()) {
			throw new HeadlessException();
		}
		return resource;
	}
}
