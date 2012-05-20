package propra2012.gruppe33.engine.graphics.rendering.scenegraph.filters;

/**
 * @author Christopher Probst
 * 
 */
public final class CompositeFilter<E> implements IterationFilter<E> {

	private final IterationFilter<E>[] filters;

	public CompositeFilter(IterationFilter<E>... filters) {
		this.filters = filters;
	}

	public IterationFilter<E>[] filters() {
		return filters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see propra2012.gruppe33.engine.graphics.rendering.scenegraph.filters.
	 * IterationFilter#accept(java.lang.Object)
	 */
	@Override
	public boolean accept(E element) {
		for (IterationFilter<E> filter : filters) {
			if (filter != null && !filter.accept(element)) {
				return false;
			}
		}
		return true;
	}
}
