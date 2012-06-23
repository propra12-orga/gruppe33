package com.indyforge.twod.engine.graphics.rendering.scenegraph.transform;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;

/**
 * 
 * @author Christopher Probst
 * 
 * @param <D>
 */
public abstract class Path<D> extends EntityReachable<D, GraphicsEntity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Path(GraphicsEntity controlled, D destination, float velocity) {
		super(controlled, destination, velocity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.transform.Reachable
	 * #cancel()
	 */
	@Override
	public void cancel() {
		// Do nothing...
	}
}
