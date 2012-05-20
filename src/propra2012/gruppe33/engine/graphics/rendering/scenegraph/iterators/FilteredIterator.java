package propra2012.gruppe33.engine.graphics.rendering.scenegraph.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

import propra2012.gruppe33.engine.graphics.rendering.scenegraph.filters.IterationFilter;

public final class FilteredIterator<E> implements Iterator<E> {

	// Used to filter elements during iteration
	private final IterationFilter<? super E> filter;

	// Get iterator
	private final Iterator<? extends E> peerIterator;

	// The next entity
	private E next = null, removePtr = null;

	public FilteredIterator(IterationFilter<? super E> filter,
			Iterator<? extends E> peerIterator) {
		if (peerIterator == null) {
			throw new NullPointerException("peerIterator");
		}

		// Save the iteration filter
		this.filter = filter;

		// Save the peer iterator
		this.peerIterator = peerIterator;
	}

	public IterationFilter<? super E> filter() {
		return filter;
	}

	public Iterator<? extends E> peerIterator() {
		return peerIterator;
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

		while (peerIterator.hasNext()) {
			// Get next
			next = peerIterator.next();

			// Seems to be a valid value!
			if (filter == null || filter.accept(next)) {
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
