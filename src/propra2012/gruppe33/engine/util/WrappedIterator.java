package propra2012.gruppe33.engine.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A simple wrapped iterator.
 * 
 * @author Christopher Probst
 * 
 * @param <E>
 *            The element type.
 */
public class WrappedIterator<E> implements Iterator<E> {

	// The peer iterator which is wrapped
	protected final Iterator<? extends E> peerIterator;

	/**
	 * Creates a new wrapped iterator using the given peer iterator.
	 * 
	 * @param peerIterator
	 *            The peer iterator.
	 */
	public WrappedIterator(Iterator<? extends E> peerIterator) {
		this.peerIterator = peerIterator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return peerIterator != null && peerIterator.hasNext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public E next() {
		if (peerIterator == null) {
			throw new NoSuchElementException();
		}
		return peerIterator.next();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		if (peerIterator == null) {
			throw new IllegalStateException("Peer iterator is null");
		}
		peerIterator.remove();
	}
}
