package com.indyforge.twod.engine.util.iteration;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator implementation which uses a filter to iterator over a peer
 * iterator.
 * 
 * @author Chistopher Probst
 * 
 * @param <E>
 *            The element type.
 */
public final class FilteredIterator<E> extends WrappedIterator<E> {

	// Used to filter elements during iteration
	private final Filter<? super E> filter;

	// The next and the remove pointer
	private E next = null, removePtr = null;

	/**
	 * Creates a new filtered iterator.
	 * 
	 * @param filter
	 *            The filter.
	 * @param peerIterator
	 *            The peer iterator which will be filtered.
	 */
	public FilteredIterator(Filter<? super E> filter,
			Iterator<? extends E> peerIterator) {
		super(peerIterator);

		// Save the iteration filter
		this.filter = filter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		// Alright!
		if (next != null) {
			return true;
		}

		// As long as there are elements...
		while (peerIterator.hasNext()) {
			// Get next
			E ptr = peerIterator.next();

			// Seems to be a valid value!
			if (filter == null || filter.accept(ptr)) {
				// Save to next
				next = ptr;

				// Success...
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public E next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		// Swap
		removePtr = next;

		// Consume
		next = null;

		return removePtr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		if (removePtr != null) {
			peerIterator.remove();
			removePtr = null;
		}
	}
}
