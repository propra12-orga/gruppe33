package com.indyforge.twod.engine.graphics.rendering.scenegraph.util.iteration;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.EntityFilter;

/**
 * Simple filter-entity-by-name implementation.
 * 
 * @author Christopher Probst
 */
public final class NameFilter implements EntityFilter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The name you are looking for
	private final String name;

	/**
	 * Creates a new name filter using the given name.
	 * 
	 * @param name
	 *            The name.
	 */
	public NameFilter(String name) {
		if (name == null) {
			throw new NullPointerException("name");
		}
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.util.iteration.Filter#accept(java.lang.Object)
	 */
	@Override
	public boolean accept(Entity entity) {
		// Compare names
		return entity != null ? entity.name().equals(name) : false;
	}
}
