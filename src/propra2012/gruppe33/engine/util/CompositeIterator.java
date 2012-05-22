package propra2012.gruppe33.engine.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * A composite iterator combines multiple iterators.
 * 
 * @author Christopher Probst
 * 
 * @param <E>
 *            The element type.
 */
public final class CompositeIterator<E> implements Iterator<E> {

	// The queue which stores all iterators
	private final Queue<Iterator<? extends E>> iterators = new LinkedList<Iterator<? extends E>>();

	// The remove pointer
	private Iterator<? extends E> removePtr = null;

	/**
	 * Creates a new composite iterator using the iterator array.
	 * 
	 * @param iterators
	 *            The iterators.
	 */
	public CompositeIterator(Iterator<? extends E>... iterators) {
		this(new ArrayIterator<Iterator<? extends E>>(iterators));
	}

	/**
	 * Creates a new composite iterator using the iterator.
	 * 
	 * @param iterators
	 */
	public CompositeIterator(Iterator<? extends Iterator<? extends E>> iterators) {
		if (iterators != null) {
			// Iterate...
			while (iterators.hasNext()) {
				// Get next
				Iterator<? extends E> iterator = iterators.next();

				// Add only valid iterators...
				if (iterator != null && iterator.hasNext()) {
					this.iterators.offer(iterator);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return !iterators.isEmpty();
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
		// Get iterator
		Iterator<? extends E> itr = iterators.element();

		// Store next object
		E next = itr.next();

		// Store remove reference
		removePtr = itr;

		// Empty now ?
		if (!itr.hasNext()) {
			iterators.remove();
		}

		// Return the next
		return next;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		if (removePtr != null) {
			removePtr.remove();
			removePtr = null;
		}
	}
}
