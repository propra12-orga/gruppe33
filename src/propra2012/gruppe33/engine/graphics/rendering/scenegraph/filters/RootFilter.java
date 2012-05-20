package propra2012.gruppe33.engine.graphics.rendering.scenegraph.filters;

import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.EntityFilter;

/**
 * @author Christopher Probst
 * 
 */
public final class RootFilter implements EntityFilter {

	public static final RootFilter INSTANCE = new RootFilter();

	/*
	 * (non-Javadoc)
	 * 
	 * @see propra2012.gruppe33.engine.graphics.rendering.scenegraph.filters.
	 * IterationFilter#accept(java.lang.Object)
	 */
	@Override
	public boolean accept(Entity entity) {
		return entity != null && entity.isRoot();
	}

	private RootFilter() {
	}
}
