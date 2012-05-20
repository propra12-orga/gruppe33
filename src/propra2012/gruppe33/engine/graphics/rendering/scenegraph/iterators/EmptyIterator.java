package propra2012.gruppe33.engine.graphics.rendering.scenegraph.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
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
