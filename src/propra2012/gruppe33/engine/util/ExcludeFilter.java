package propra2012.gruppe33.engine.util;

/**
 * An exlcude filter basically excludes the given elements during filtering.
 * 
 * @author Christopher Probst
 * 
 * @param <E>
 */
public final class ExcludeFilter<E> implements Filter<E> {

	// The excluded iterable elements
	private final Iterable<? extends E> excluded;

	/**
	 * Creates a new exclude filter using the given array elements.
	 * 
	 * @param excluded
	 *            The excluded elements.
	 */
	public ExcludeFilter(E... excluded) {
		this(new ArrayIterator<E>(excluded));
	}

	/**
	 * Creates a new exclude filter using the given iterable elements.
	 * 
	 * @param excluded
	 *            The excluded elements.
	 */
	public ExcludeFilter(Iterable<? extends E> excluded) {
		if (excluded == null) {
			throw new NullPointerException("excluded");
		}

		// Save the exluded elements
		this.excluded = excluded;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see propra2012.gruppe33.engine.graphics.rendering.scenegraph.filters.
	 * IterationFilter#accept(java.lang.Object)
	 */
	@Override
	public boolean accept(E element) {
		// For all excluded elements
		for (E exclude : excluded) {
			// Compare the excluded element with the given element
			if ((exclude == null && element == null)
					|| (exclude != null && exclude.equals(element))
					|| (element != null && element.equals(exclude))) {
				return false;
			}
		}
		return true;
	}
}
