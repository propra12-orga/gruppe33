package com.indyforge.twod.engine.graphics.rendering.scenegraph.network.transform;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.UUID;

import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.GridRoutines;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class PositionChange extends Vector2fChange {

	/*
	 * If true the given vector will be added to the position, otherwise the
	 * position will be set.
	 */
	private boolean translate;

	public PositionChange() {
		super();
	}

	public PositionChange(Vector2f vector, boolean translate,
			UUID registrationKey) {
		super(vector, registrationKey);
		this.translate = translate;
	}

	public boolean isTranslate() {
		return translate;
	}

	public void translate(boolean translate) {
		this.translate = translate;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		super.readExternal(in);
		translate = in.readBoolean();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeBoolean(translate);
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
		if (translate) {
			entity.position().addLocal(vector());
		} else {
			entity.position().set(vector());
		}
		GridRoutines.rearrangeGridNode((GraphicsEntity) entity.parent());
	}
}
