package com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.indyforge.foxnet.rmi.pattern.change.Change;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;

/**
 * An abstact Many-To-Many entity change.
 * 
 * @author Christopher Probst
 * @param <T>
 * @param <V>
 */
public abstract class ManyToMany<T extends Entity, V> implements
		Change<SceneProcessor> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// All mappings
	private final Map<UUID, V> entityMap;

	protected abstract void apply(T entity, V value);

	public ManyToMany() {
		this(null);
	}

	public ManyToMany(Map<UUID, V> entityMap) {
		if (entityMap == null) {
			entityMap = new HashMap<UUID, V>();
		}
		this.entityMap = entityMap;
	}

	/**
	 * @return a map with all registration key mappings.
	 */
	public Map<UUID, V> entityMap() {
		return entityMap;
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
			for (Entry<UUID, V> entry : entityMap.entrySet()) {

				// Lookup
				Entity entity = root.registry().get(entry.getKey());

				// Is the key valid ?
				if (entity != null) {
					apply((T) entity, entry.getValue());
				}
			}
		}
	}
}