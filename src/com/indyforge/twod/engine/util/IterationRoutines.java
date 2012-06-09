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

	public static <E> E filterNext(Filter<? super E> filter, Iterator<E> peer) {
		return next(filter(filter, peer));
	}

	public static <E> Iterator<E> filter(Filter<? super E> filter,
			Iterator<E> peer) {
		return new FilteredIterator<E>(filter, peer);
	}

	// Should be instantiated...
	private IterationRoutines() {
	}
}
