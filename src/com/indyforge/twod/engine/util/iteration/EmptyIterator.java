package com.indyforge.twod.engine.util.iteration;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An empty iterator of the given element type.
 * 
 * @author Christopher Probst
 * 
 * @param <E>
 *            The element type.
 */
public final class EmptyIterator<E> implements Iterator<E> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public E next() {
		throw new NoSuchElementException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException("Emtpty iterator");
	}
}
