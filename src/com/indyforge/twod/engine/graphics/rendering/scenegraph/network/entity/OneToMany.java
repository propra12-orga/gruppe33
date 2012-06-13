package com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;

/**
 * 
 * @author Kr0e
 * 
 * @param <T>
 * @param <V>
 */
public abstract class OneToMany<T extends Entity, V> extends Many<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private V value;

	protected abstract void apply(T entity, V value);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity
	 * .OneToMany
	 * #apply(com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity)
	 */
	protected final void apply(T entity) {
		apply(entity, value);
	}

	public V value() {
		return value;
	}

	public OneToMany<T, V> value(V value) {
		this.value = value;
		return this;
	}
}
