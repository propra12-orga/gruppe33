package com.indyforge.twod.engine.graphics.rendering.scenegraph.util.iteration;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.EntityFilter;

/**
 * A simple root entity filter.
 * 
 * @author Christopher Probst
 */
public final class RootFilter implements EntityFilter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Public instance for more performance.
	 */
	public static final RootFilter INSTANCE = new RootFilter();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.util.iteration.Filter#accept(java.lang.Object)
	 */
	@Override
	public boolean accept(Entity entity) {
		return entity != null && entity.isRoot();
	}

	// Should be instantiated
	private RootFilter() {
	}
}
