package com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.indyforge.foxnet.rmi.pattern.change.Change;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;

/**
 * An abstact One-To-Many entity change.
 * 
 * @author Christopher Probst
 */
public abstract class Many<T extends Entity> implements Change<SceneProcessor> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The uuid of the entity
	private final List<UUID> registrationKeys = new LinkedList<UUID>();

	protected abstract void apply(T entity);

	/**
	 * @return a list with all registration keys.
	 */
	public List<UUID> entities() {
		return registrationKeys;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.foxnet.rmi.pattern.change.Change#apply(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void apply(SceneProcessor ctx) {
		// Get root
		Scene root = ctx.root();

		// Is there a root ?
		if (root != null) {

			// Apply to all entities
			for (UUID registrationKey : registrationKeys) {

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
