package propra2012.gruppe33.engine.util;

/**
 * A composite filter combines multiple filters to accept elements.
 * 
 * @author Christopher Probst
 * 
 * @param <E>
 *            The element type.
 */
public final class CompositeFilter<E> implements Filter<E> {

	// The filters
	private final Iterable<? extends Filter<? super E>> filters;

	/**
	 * Creates a new composite filter using the given filter array.
	 * 
	 * @param filters
	 *            The filters.
	 */
	public CompositeFilter(Filter<? super E>... filters) {
		this(new ArrayIterator<Filter<? super E>>(filters));
	}

	/**
	 * Creates a new composite filter using the given iterable filters.
	 * 
	 * @param filters
	 *            The filters.
	 */
	public CompositeFilter(Iterable<? extends Filter<? super E>> filters) {
		if (filters == null) {
			throw new NullPointerException("filters");
		}

		// Save the filters
		this.filters = filters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see propra2012.gruppe33.engine.graphics.rendering.scenegraph.filters.
	 * IterationFilter#accept(java.lang.Object)
	 */
	@Override
	public boolean accept(E element) {
		// For all filters
		for (Filter<? super E> filter : filters) {

			// Check the filter
			if (filter != null && !filter.accept(element)) {
				return false;
			}
		}
		return true;
	}
}
