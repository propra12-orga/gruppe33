package com.indyforge.twod.engine.graphics.rendering.scenegraph.transform;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class PositionTarget extends Vector2fTarget {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.transform.Target
	 * #transform(java.lang.Object)
	 */
	@Override
	protected void transform(Vector2f state) {
		if (state == null) {
			state = Vector2f.zero();
		}

		transformMotor().linearVelocity(state);
	}

	public PositionTarget(GraphicsEntity controlled, Vector2f target,
			float velocity) {
		super(controlled, target, velocity);
	}
}
