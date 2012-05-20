package propra2012.gruppe33.engine.graphics.rendering.scenegraph.filters;

import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.EntityFilter;

/**
 * 
 * @author Christopher Probst
 */
public final class NameFilter implements EntityFilter {

	// The name you are looking for
	private final String name;

	public NameFilter(String name) {
		if (name == null) {
			throw new NullPointerException("name");
		}
		this.name = name;
	}

	/**
	 * @return the name.
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see propra2012.gruppe33.engine.graphics.rendering.scenegraph.filters.
	 * IterationFilter#accept(java.lang.Object)
	 */
	@Override
	public boolean accept(Entity entity) {
		return entity != null ? entity.getName().equals(name) : false;
	}
}
