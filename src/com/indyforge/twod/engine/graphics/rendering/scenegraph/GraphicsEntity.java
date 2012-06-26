package com.indyforge.twod.engine.graphics.rendering.scenegraph;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;
import com.indyforge.twod.engine.graphics.sprite.Sprite;
import com.indyforge.twod.engine.resources.assets.Asset;
import com.indyforge.twod.engine.resources.assets.AssetManager;
import com.indyforge.twod.engine.util.iteration.FilteredIterator;
import com.indyforge.twod.engine.util.iteration.IterationRoutines;
import com.indyforge.twod.engine.util.iteration.TypeFilter;

/**
 * 
 * @author Christopher Probst
 * @see Entity
 * @see Scene
 */
public class GraphicsEntity extends Entity {

	public enum GraphicsEvent {
		Render
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Only accepts graphics entities.
	 */
	public static final TypeFilter TYPE_FILTER = new TypeFilter(
			GraphicsEntity.class, true);

	/*
	 * The position of this entity stored as Vector2f.
	 */
	private Vector2f position = Vector2f.zero(),

	/*
	 * The scale of this entity.
	 */
	scale = new Vector2f(1, 1);

	/*
	 * The last affine origin transform.
	 */
	private AffineTransform lastOriginWorldTransform = new AffineTransform(),
			lastWorldTransform = new AffineTransform(),
			lastTransform = new AffineTransform();

	/*
	 * The rotation of this entity.
	 */
	private float rotation = 0;

	/*
	 * Tells whether or not this entity is visible.
	 */
	private boolean visible = true;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity#onEvent
	 * (com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity,
	 * java.lang.Object, java.lang.Object[])
	 */
	@Override
	protected void onEvent(Entity source, Object event, Object... params) {
		// Process other events...
		super.onEvent(source, event, params);

		// Check
		if (event instanceof GraphicsEvent) {
			switch ((GraphicsEvent) event) {

			case Render:

				/*
				 * At first we have to update the transforms.
				 */
				updateTransforms();

				/*
				 * Do the rendering.
				 */
				if (visible) {
					// Convert and create copy
					Graphics2D original = (Graphics2D) ((Graphics2D) params[0])
							.create();

					try {

						// Create new copy
						Graphics2D transformed = (Graphics2D) original.create();

						try {
							// Use the last world transform
							transformed.transform(lastWorldTransform);

							// Render this entity
							onRender(original, transformed);
						} finally {
							// Dispose
							transformed.dispose();
						}

					} finally {
						// Dispose
						original.dispose();
					}
				}
				break;
			}
		}
	}

	/**
	 * OVERRIDE FOR CUSTOM RENDER BEHAVIOUR.
	 * 
	 * This method gets called every frame to render this entity if the visible
	 * flag is set to true.
	 * 
	 * @param original
	 *            A graphics context you can render to. There is no
	 *            transformation applied yet.
	 * @param transformed
	 *            A graphics context you can render to. The entity has already
	 *            applied transformation, so the context origin is your
	 *            position.
	 * 
	 */
	protected void onRender(Graphics2D original, Graphics2D transformed) {
	}

	public GraphicsEntity() {
		// Register the default entity events
		events().put(GraphicsEvent.Render, iterableChildren(true, true));
	}

	/**
	 * @return the next parent scene, or this if this entity is already a scene
	 *         or null if there is no scene at all.
	 */
	public Scene findScene() {
		return (Scene) findParent(new TypeFilter(Scene.class), true);
	}

	/**
	 * @return the scene processor or null.
	 */
	public SceneProcessor findSceneProcessor() {
		Scene scene = findScene();
		return scene != null ? scene.processor : null;
	}

	/**
	 * @return the visible flag.
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Updates all transforms of this entity. You should only call this method
	 * if you know what you are doing. Usually this method is called every frame
	 * before rendering.
	 * 
	 * @return this for chaining.
	 */
	public GraphicsEntity updateTransforms() {

		// Set to identity
		lastOriginWorldTransform.setToIdentity();
		lastWorldTransform.setToIdentity();
		lastTransform.setToIdentity();

		// Apply local transform
		lastTransform.translate(position.x, position.y);
		lastTransform.rotate(rotation);
		lastTransform.scale(scale.x, scale.y);

		// Try to find the next graphics parent
		GraphicsEntity result = (GraphicsEntity) IterationRoutines
				.next(new FilteredIterator<Entity>(TYPE_FILTER,
						parentIterator(false)));

		// If the parent exists
		if (result != null) {

			// Set the last world transform
			lastOriginWorldTransform.setTransform(result.lastWorldTransform());

			// Copy from origin
			lastWorldTransform.setTransform(lastOriginWorldTransform);
		}

		// Concatenate with local transform
		lastWorldTransform.concatenate(lastTransform);

		return this;
	}

	/**
	 * Sets the visible flag. True means this entity is rendered. This flag does
	 * not influence the rendering of the children, too.
	 * 
	 * @param visible
	 * @return this for chaining.
	 */
	public GraphicsEntity visible(boolean visible) {
		this.visible = visible;
		return this;
	}

	/**
	 * @return the relative scale of this entity as vector.
	 */
	public Vector2f scale() {
		return scale;
	}

	/**
	 * Sets the relative scale of this entity.
	 * 
	 * @param scale
	 *            The vector you want to use as scale now.
	 * @return this for chaining.
	 */
	public GraphicsEntity scale(Vector2f scale) {
		if (scale == null) {
			throw new NullPointerException("scale");
		}
		this.scale = scale;
		return this;
	}

	/**
	 * @return the relative rotation of this entity in radians.
	 */
	public float rotation() {
		return rotation;
	}

	/**
	 * Sets the relative rotation in radians.
	 * 
	 * @param rotation
	 *            The rotation value stored as float you want to set.
	 * @return this for chaining.
	 */
	public GraphicsEntity rotation(float rotation) {
		this.rotation = rotation;
		return this;
	}

	/**
	 * @return the relative position of this entity.
	 */
	public Vector2f position() {
		return position;
	}

	/**
	 * @return the last affine origin world transform.
	 * @see GraphicsEntity#updateTransforms()
	 */
	public AffineTransform lastOriginWorldTransform() {
		return lastOriginWorldTransform;
	}

	/**
	 * @return the last affine world transform.
	 * @see GraphicsEntity#updateTransforms()
	 */
	public AffineTransform lastWorldTransform() {
		return lastWorldTransform;
	}

	/**
	 * @return the last affine transform.
	 * @see GraphicsEntity#updateTransforms()
	 */
	public AffineTransform lastTransform() {
		return lastTransform;
	}

	/**
	 * Sets the relative position of this entity.
	 * 
	 * @param position
	 *            The vector you want to use as position now.
	 * @return this for chaining.
	 */
	public GraphicsEntity position(Vector2f position) {
		if (position == null) {
			throw new NullPointerException("position");
		}

		this.position = position;
		return this;
	}

	/**
	 * @param key
	 *            The key of the sprite you want to lookup.
	 * @return the sprite of the given key or null.
	 */
	public Sprite spriteProp(Object key) {
		return prop(key, Sprite.class);
	}

	/**
	 * @param key
	 *            The key of the image you want to lookup.
	 * @return the image of the given key or null.
	 */
	@SuppressWarnings("unchecked")
	public Asset<BufferedImage> imageProp(Object key) {
		return (Asset<BufferedImage>) prop(key, Asset.class);
	}

	/**
	 * Renders this entity and all children to an image. This method uses the
	 * visible/childrenVisible flags to determine what to render.
	 * 
	 * @param destination
	 *            The image you want to render to.
	 * @return the given destination image.
	 */
	public final Image renderTo(Image destination) {
		if (destination == null) {
			throw new NullPointerException("destination");
		} else if (!AssetManager.isHeadless()) {

			// Get the graphics context
			Graphics graphics = destination.getGraphics();

			try {
				if (graphics instanceof Graphics2D) {

					// Just render to image
					render((Graphics2D) graphics);
				} else {
					throw new IllegalArgumentException(
							"The image must return a "
									+ "Graphics2D object when calling getGraphics().");
				}
			} finally {
				// Dispose
				graphics.dispose();
			}
		}

		return destination;
	}

	/**
	 * Renders this entity and all children.
	 * 
	 * @param original
	 *            The original graphics context.
	 * @return this for chaining.
	 */
	public GraphicsEntity render(Graphics2D original) {
		return render(null, original);
	}

	/**
	 * Renders this entity and all children using the given filter.
	 * 
	 * @param EntityFilter
	 *            The entity filter or null.
	 * @param original
	 *            The original graphics context.
	 * @return this for chaining.
	 */
	public GraphicsEntity render(EntityFilter entityFilter, Graphics2D original) {

		// Not headless ?
		if (!AssetManager.isHeadless()) {
			// Fire event for all children
			fireEvent(entityFilter, GraphicsEvent.Render, original);
		}
		return this;
	}
}
