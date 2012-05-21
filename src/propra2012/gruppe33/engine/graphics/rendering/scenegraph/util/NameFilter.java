package propra2012.gruppe33.engine.graphics.rendering.scenegraph.util;

import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.EntityFilter;

/**
 * Simple filter-entity-by-name implementation.
 * 
 * @author Christopher Probst
 */
public final class NameFilter implements EntityFilter {

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
	 * @see propra2012.gruppe33.engine.graphics.rendering.scenegraph.filters.
	 * IterationFilter#accept(java.lang.Object)
	 */
	@Override
	public boolean accept(Entity entity) {
		// Compare names
		return entity != null ? entity.name().equals(name) : false;
	}
}
