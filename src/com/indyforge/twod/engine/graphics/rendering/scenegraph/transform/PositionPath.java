package com.indyforge.twod.engine.graphics.rendering.scenegraph.transform;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class PositionPath extends Vector2fPath {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.twod.engine.graphics.rendering.scenegraph.transform.
	 * EntityTask#addToState(java.lang.Object)
	 */
	@Override
	protected void addToState(Vector2f state) {
		controlled().position().addLocal(state);
	}

	public PositionPath(GraphicsEntity controlled, Vector2f destination,
			float velocity) {
		super(controlled, destination, velocity);
	}
}
