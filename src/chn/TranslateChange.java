package chn;

import java.util.UUID;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridRoutines;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.transform.Vector2fChange;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class TranslateChange extends Vector2fChange {

	public TranslateChange() {
		super();
	}

	public TranslateChange(Vector2f vector, UUID registrationKey) {
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
		GridRoutines.rearrangeGridNode((GraphicsEntity) entity.parent());
	}
}
