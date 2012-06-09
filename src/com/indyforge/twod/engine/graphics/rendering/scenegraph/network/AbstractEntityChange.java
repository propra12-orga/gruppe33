package com.indyforge.twod.engine.graphics.rendering.scenegraph.network;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.UUID;

import com.foxnet.rmi.pattern.change.Change;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;

/**
 * An abstact entity change.
 * 
 * @author Christopher Probst
 */
public abstract class AbstractEntityChange<T extends Entity> implements
		Change<SceneProcessor>, Externalizable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The uuid of the entity
	private UUID registrationKey;

	protected abstract void apply(T entity);

	public AbstractEntityChange() {
		this(null);
	}

	public AbstractEntityChange(UUID registrationKey) {
		this.registrationKey = registrationKey;
	}

	public UUID registrationKey() {
		return registrationKey;
	}

	public void registrationKey(UUID registrationKey) {
		this.registrationKey = registrationKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		registrationKey = in.readBoolean() ? new UUID(in.readLong(),
				in.readLong()) : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeBoolean(registrationKey != null);
		if (registrationKey != null) {
			out.writeLong(registrationKey.getMostSignificantBits());
			out.writeLong(registrationKey.getLeastSignificantBits());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxnet.rmi.pattern.change.Change#apply(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void apply(SceneProcessor ctx) {
		if (registrationKey != null) {

			// Get root
			Scene root = ctx.root();

			// Is there a root ?
			if (root != null) {

				// Lookup
				Entity entity = root.registry().get(registrationKey);

				// Is the key valid ?
				if (entity != null) {
					apply((T) entity);
				}
			}
		}
	}
}
