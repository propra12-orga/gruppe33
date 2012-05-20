package propra2012.gruppe33.engine.graphics.rendering.scenegraph.iterators;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * 
 * @author Christopher Probst
 * 
 * @param <E>
 *            The element type.
 */
public final class CompositeIterator<E> implements Iterator<E> {

	// The queue which stores all iterators
	private final Queue<Iterator<? extends E>> iterators = new LinkedList<Iterator<? extends E>>();

	private Iterator<? extends E> removePtr = null;

	public CompositeIterator(Iterator<? extends E>... iterators) {
		this(new ArrayIterator<Iterator<? extends E>>(iterators));
	}

	public CompositeIterator(Iterator<Iterator<? extends E>> iterators) {
		if (iterators != null) {
			// Add all collection elements
			while (iterators.hasNext()) {

				// Get next
				Iterator<? extends E> iterator = iterators.next();

				if (iterator != null && iterator.hasNext()) {
					this.iterators.offer(iterator);
				}
			}
		}
	}

	@Override
	public boolean hasNext() {
		return !iterators.isEmpty();
	}

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

	@Override
	public void remove() {
		if (removePtr != null) {
			removePtr.remove();
			removePtr = null;
		}
	}
}
