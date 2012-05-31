package com.indyforge.twod.engine.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * An exlcude filter basically excludes the given elements during filtering.
 * 
 * @author Christopher Probst
 * 
 * @param <E>
 */
public final class ExcludeFilter<E> implements Filter<E> {

	// The excluded iterable elements
	private final List<E> excluded = new LinkedList<E>();

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
	 * Creates a new exclude filter using the given element iterator.
	 * 
	 * @param excluded
	 *            The excluded elements.
	 */
	public ExcludeFilter(Iterator<? extends E> excluded) {
		if (excluded == null) {
			throw new NullPointerException("excluded");
		}

		// Add all iterator elements
		while (excluded.hasNext()) {
			this.excluded.add(excluded.next());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.twod.engine.graphics.rendering.scenegraph.filters.
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
