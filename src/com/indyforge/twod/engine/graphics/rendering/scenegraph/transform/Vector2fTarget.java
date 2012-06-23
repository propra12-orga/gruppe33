package com.indyforge.twod.engine.graphics.rendering.scenegraph.transform;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;

/**
 * 
 * @author Christopher Probst
 * 
 */
public abstract class Vector2fTarget extends Target<Vector2f> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.transform.Target
	 * #state()
	 */
	protected Vector2f state() {
		return controlled().position();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.twod.engine.graphics.rendering.scenegraph.transform.
	 * EntityReachable#reachTarget(float)
	 */
	protected boolean reachTarget(float tpf) {

		// Get the position and the destination
		Vector2f position = state(), destination = destination();

		// Get veloctiy
		float v = veloctiy();

		// Already reached the destination ??
		if (position.equals(destination)) {
			return true;
		}

		// Calc active dist
		Vector2f activeDist = destination.sub(position);

		// Too near ?
		if (activeDist.length() < v * tpf) {

			// Clear transform
			transform(Vector2f.zero());

			// Just add the remaining distance
			addToState(activeDist);

			// Destination reached...
			return true;
		} else {
			// Set transform using the correct direction and the veloctiy
			transform(activeDist.normalizeLocal().scaleLocal(v));

			// Destination not reached...
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.twod.engine.graphics.rendering.scenegraph.transform.
	 * EntityReachable#addToState(java.lang.Object)
	 */
	protected void addToState(Vector2f state) {
		controlled().position().addLocal(state);
	}

	public Vector2fTarget(GraphicsEntity controlled, Vector2f destination,
			float velocity) {
		super(controlled, destination, velocity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.transform.Target
	 * #diff()
	 */
	@Override
	public Vector2f diff() {
		return destination().sub(state());
	}
}
