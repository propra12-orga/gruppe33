package propra2012.gruppe33.engine.graphics.rendering.scenegraph.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 
 * @author Christopher Probst
 * 
 * @param <E>
 *            The iteration type.
 */
public class ArrayIterator<E> implements Iterator<E> {

	private final Object[] elements;
	private int index = 0;

	public ArrayIterator(E... elements) {
		this.elements = elements;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return index < elements.length;
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
		return (E) elements[index++];
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
