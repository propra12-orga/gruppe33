package com.indyforge.twod.engine.graphics.rendering.scenegraph.transform;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.MathExt;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;

/**
 * 
 * @author Christopher Probst
 * 
 */
public abstract class Vector2fPath extends Path<Vector2f> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.twod.engine.graphics.rendering.scenegraph.transform.
	 * EntityReachable#reachTarget(float)
	 */
	@Override
	protected boolean reachTarget(float tpf) {

		// Calc the amount
		float amount = veloctiy() * tpf;

		// Calc the destination length
		float len = destination().length();

		// Get the direction
		Vector2f dir = destination().normalize();

		// Calc the time
		float time = Math.min(len, amount);

		// Scale the direction correctly
		dir.scaleLocal(time);

		// Sub local
		destination().subLocal(dir);

		// Add to state
		addToState(dir);

		// Ready ?
		return MathExt.equals(len - time, 0);
	}

	public Vector2fPath(GraphicsEntity controlled, Vector2f destination,
			float velocity) {
		super(controlled, destination, velocity);

		// Already reached...
		if (destination().equals(Vector2f.zero())) {
			reached = true;
		}
	}
}
