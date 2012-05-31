package com.indyforge.twod.engine.util;

import java.util.Iterator;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class IterationRoutines {

	/**
	 * @param iterator
	 *            The iterator.
	 * @return the next element or null.
	 */
	public static <E> E next(Iterator<E> iterator) {
		return iterator != null ? (iterator.hasNext() ? iterator.next() : null)
				: null;
	}

	// Should be instantiated...
	private IterationRoutines() {
	}
}
