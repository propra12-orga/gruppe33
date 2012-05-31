package com.indyforge.twod.engine.graphics.rendering.scenegraph.util;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.EntityFilter;

/**
 * A simple root entity filter.
 * 
 * @author Christopher Probst
 */
public final class RootFilter implements EntityFilter {

	/**
	 * Public instance for more performance.
	 */
	public static final RootFilter INSTANCE = new RootFilter();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.twod.engine.graphics.rendering.scenegraph.filters.
	 * IterationFilter#accept(java.lang.Object)
	 */
	@Override
	public boolean accept(Entity entity) {
		return entity != null && entity.isRoot();
	}

	// Should be instantiated
	private RootFilter() {
	}
}
