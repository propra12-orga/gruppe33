package propra2012.gruppe33.engine.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A simple array iterator. You can specify the direction of iteration. The
 * {@link Iterator#remove()} is not supported.
 * 
 * @author Christopher Probst
 * 
 * @param <E>
 *            The iteration type.
 */
public final class ArrayIterator<E> implements Iterator<E>, Iterable<E> {

	// The internal array
	private final Object[] elements;

	// The ascending flag
	private final boolean ascending;

	// The index of this iterator
	private int index;

	/**
	 * Creates a new ascending array iterator.
	 * 
	 * @param elements
	 *            The elements.
	 */
	public ArrayIterator(E... elements) {
		this(true, elements);
	}

	/**
	 * Creates a new array iterator.
	 * 
	 * @param ascending
	 *            The ascending-flag.
	 * @param elements
	 *            The elements.
	 */
	public ArrayIterator(boolean ascending, E... elements) {

		// Save the elements
		this.elements = elements;

		// Save the ascending-flag
		this.ascending = ascending;

		// Evaluate the flag and setup the index
		index = elements != null && elements.length > 0 ? (ascending ? 0
				: elements.length - 1) : -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Iterator<E> iterator() {
		return new ArrayIterator<E>(ascending, (E[]) elements);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return index >= 0 && index < elements.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public E next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return (E) elements[ascending ? index++ : index--];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
