package propra2012.gruppe33.engine.graphics.rendering.scenegraph;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;

import propra2012.gruppe33.engine.graphics.rendering.scenegraph.util.ParentIterator;
import propra2012.gruppe33.engine.util.FilteredIterator;
import propra2012.gruppe33.engine.util.IterationRoutines;
import propra2012.gruppe33.engine.util.TypeFilter;

/**
 * 
 * @author Christopher Probst
 * @see Entity
 * @see Scene
 */
public class GraphicsEntity extends Entity {

	public enum GraphicsEntityEvent {
		Render
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * The position of this entity stored as Vector2f.
	 */
	private Vector2f position = Vector2f.zero();

	/*
	 * The rotation of this entity.
	 */
	private float rotation = 0;

	/*
	 * The scale of this entity.
	 */
	private Vector2f scale = new Vector2f(1, 1);

	/*
	 * Tells whether or not this entity and/or its children are visible.
	 */
	private boolean visible = true, childrenVisible = true;

	@Override
	protected void onEvent(Object event, Object... params) {
		// Process other events...
		super.onEvent(event, params);

		// Check
		if (event instanceof GraphicsEntityEvent) {
			switch ((GraphicsEntityEvent) event) {

			case Render:
				// Convert and invoke
				onRender((Graphics2D) params[0], (Graphics2D) params[1]);
				break;
			}
		}
	}

	/**
	 * OVERRIDE FOR CUSTOM RENDER BEHAVIOUR:
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

	/**
	 * @return the next parent scene, or this if this entity is already a scene
	 *         or null if there is no scene at all.
	 */
	public Scene findScene() {
		return (Scene) IterationRoutines.next(new FilteredIterator<Entity>(
				new TypeFilter(Scene.class, true), new ParentIterator(this,
						true)));
	}

	/**
	 * @return the children visible flag.
	 */
	public boolean childrenVisible() {
		return childrenVisible;
	}

	/**
	 * @return the visible flag.
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Sets the visible flag. True means this entity is rendered. This flag does
	 * not influence the rendering of the children, too.
	 * 
	 * @param visible
	 */
	public void visible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Sets the children visible flag. True means all children are rendered.
	 * This flag does not influence the rendering of this entity.
	 * 
	 * @param childrenVisible
	 */
	public void areChildrenVisible(boolean childrenVisible) {
		this.childrenVisible = childrenVisible;
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
	 */
	public void scale(Vector2f scale) {
		if (scale != null) {
			throw new NullPointerException("scale");
		}
		this.scale = scale;
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
	 */
	public void rotation(float rotation) {
		this.rotation = rotation;
	}

	/**
	 * @return the relative position of this entity.
	 */
	public Vector2f position() {
		return position;
	}

	/**
	 * Sets the relative position of this entity.
	 * 
	 * @param position
	 *            The vector you want to use as position now.
	 */
	public void position(Vector2f position) {
		if (position == null) {
			throw new NullPointerException("position");
		}

		this.position = position;
	}

	/**
	 * Applies local transform to a given graphics context.
	 * 
	 * @param graphics
	 *            The graphics context you want to transform.
	 * @return the given transformed graphics context for chaining.
	 */
	public final Graphics2D transform(Graphics2D graphics) {
		if (graphics == null) {
			throw new NullPointerException("graphics");
		}

		// Translate
		graphics.translate(position.x, position.y);

		// Rotate the graphics
		graphics.rotate(rotation);

		// Scale the graphics
		graphics.scale(scale.x, scale.y);

		return graphics;
	}

	/**
	 * Applies local transform to a given affine transform.
	 * 
	 * @param transform
	 *            The affine transform you want to transform.
	 * @return the given transformed affine transform for chaining.
	 */
	public final AffineTransform transform(AffineTransform transform) {
		if (transform == null) {
			throw new NullPointerException("transform");
		}
		transform.translate(position.x, position.y);
		transform.rotate(rotation);
		transform.scale(scale.x, scale.y);
		return transform;
	}

	/**
	 * @return a affine transform containing the state of this entity.
	 */
	public AffineTransform transform() {
		return transform(new AffineTransform());
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
		}

		// Get the graphics context
		Graphics graphics = destination.getGraphics();

		try {
			if (graphics instanceof Graphics2D) {

				// Just render to image
				render((Graphics2D) graphics, (Graphics2D) graphics);
			} else {
				throw new IllegalArgumentException("The image must return a "
						+ "Graphics2D object when calling getGraphics().");
			}

			return destination;
		} finally {
			// Dispose
			graphics.dispose();
		}
	}

	/**
	 * Renders this entity and all children to a graphics context. This method
	 * uses the visible/childrenVisible flags to determine what to render.
	 * 
	 * @param original
	 *            The graphics context which is NOT affected by entity
	 *            transformation.
	 * @param transformed
	 *            The graphics context which is affected by entity
	 *            transformation.
	 * 
	 */
	public final void render(Graphics2D original, Graphics2D transformed) {

		if (original == null) {
			throw new NullPointerException("original");
		} else if (transformed == null) {
			throw new NullPointerException("transformed");
		}

		// Create a copy of the transformed graphics
		transformed = transform((Graphics2D) transformed.create());
		try {

			if (visible) {
				// Create a copy the original graphics
				Graphics2D originalCopy = (Graphics2D) original.create();

				try {

					// Create a copy of the transformed graphics
					Graphics2D transformedCopy = (Graphics2D) transformed
							.create();

					try {

						// Render this entity
						fireEvent(this, GraphicsEntityEvent.Render,
								originalCopy, transformedCopy);

					} finally {
						// Dispose the transformed copy
						transformedCopy.dispose();
					}
				} finally {
					// Dispose the original copy
					originalCopy.dispose();
				}
			}

			if (childrenVisible) {
				// Now render the other entities
				for (Entity child : this) {
					if (child instanceof GraphicsEntity) {
						((GraphicsEntity) child).render(original, transformed);
					}
				}
			}
		} finally {
			// Dispose the transformed copy
			transformed.dispose();
		}
	}
}
