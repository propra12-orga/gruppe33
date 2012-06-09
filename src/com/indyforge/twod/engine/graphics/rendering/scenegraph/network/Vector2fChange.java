package com.indyforge.twod.engine.graphics.rendering.scenegraph.network;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.UUID;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;

/**
 * 
 * @author Christopher Probst
 * 
 */
public abstract class Vector2fChange extends
		AbstractEntityChange<GraphicsEntity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The vector of this change
	private Vector2f vector;

	public Vector2fChange() {
	}

	public Vector2fChange(Vector2f vector, UUID registrationKey) {
		super(registrationKey);
		this.vector = vector;
	}

	public Vector2f vector() {
		return vector;
	}

	public void vector(Vector2f vector) {
		this.vector = vector;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.twod.engine.graphics.rendering.scenegraph.network.
	 * AbstractEntityChange#readExternal(java.io.ObjectInput)
	 */
	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		super.readExternal(in);
		vector = in.readBoolean() ? new Vector2f(in.readFloat(), in.readFloat())
				: null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.twod.engine.graphics.rendering.scenegraph.network.
	 * AbstractEntityChange#writeExternal(java.io.ObjectOutput)
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeBoolean(vector != null);
		if (vector != null) {
			out.writeFloat(vector.x);
			out.writeFloat(vector.y);
		}
	}
}
