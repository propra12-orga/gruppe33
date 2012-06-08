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
	 * @see com.indyforge.twod.engine.graphics.rendering.scenegraph.transform.
	 * AbstractTarget#state()
	 */
	protected Vector2f state() {
		return includesParentState()
				&& controlled().parent() instanceof GraphicsEntity ? ((GraphicsEntity) controlled()
				.parent()).position().add(controlled().position())
				: controlled().position();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.transform.Target
	 * #reachTarget(float)
	 */
	protected boolean reachTarget(float tpf) {

		// Get the position and the target
		Vector2f position = state(), target = target();

		// Get veloctiy
		float v = veloctiy();

		// Already reached the target ??
		if (position.equals(target)) {
			return true;
		}

		// Calc active dist
		Vector2f activeDist = target.sub(position);

		// Too near ?
		if (activeDist.length() < v * tpf) {

			// Clear transform
			transform(Vector2f.zero());

			// Just add the remaining distance
			addToState(activeDist);

			// Target reached...
			return true;
		} else {
			// Set transform using the correct direction and the veloctiy
			transform(activeDist.normalizeLocal().scaleLocal(v));

			// Target not reached...
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.twod.engine.graphics.rendering.scenegraph.transform.
	 * AbstractTarget#addToState(java.lang.Object)
	 */
	protected void addToState(Vector2f state) {
		controlled().position().addLocal(state);
	}

	public Vector2fTarget(GraphicsEntity controlled, Vector2f target,
			float velocity, boolean includeParentState) {
		super(controlled, target, velocity, includeParentState);
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
		return target().sub(state());
	}
}
