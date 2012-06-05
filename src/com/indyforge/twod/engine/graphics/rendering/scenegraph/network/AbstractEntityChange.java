package com.indyforge.twod.engine.graphics.rendering.scenegraph.network;

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
		Change<SceneProcessor> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The uuid of the entity
	private final UUID registrationKey;

	protected abstract void apply(T entity);

	public AbstractEntityChange(UUID registrationKey) {
		if (registrationKey == null) {
			throw new NullPointerException("registrationKey");
		}
		this.registrationKey = registrationKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxnet.rmi.pattern.change.Change#apply(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void apply(SceneProcessor ctx) {
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
