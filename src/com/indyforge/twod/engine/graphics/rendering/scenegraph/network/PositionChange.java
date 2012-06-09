package com.indyforge.twod.engine.graphics.rendering.scenegraph.network;

import java.util.UUID;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class PositionChange extends Vector2fChange {

	public PositionChange() {
		super();
	}

	public PositionChange(Vector2f vector, UUID registrationKey) {
		super(vector, registrationKey);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.twod.engine.graphics.rendering.scenegraph.network.
	 * AbstractEntityChange
	 * #apply(com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity)
	 */
	@Override
	protected void apply(GraphicsEntity entity) {
		entity.position().addLocal(vector());
	}
}
