package propra2012.gruppe33.engine.graphics.rendering.scenegraph.iterators;

import java.util.Iterator;

/**
 * 
 * @author Christopher Probst
 * 
 * @param <E>
 *            The element type.
 */
public class PeerIterator<E> implements Iterator<E> {

	private final Iterator<? extends E> peerIterator;

	public PeerIterator(Iterator<? extends E> peerIterator) {
		if (peerIterator == null) {
			throw new NullPointerException("peerIterator");
		}
		this.peerIterator = peerIterator;
	}

	public Iterator<? extends E> peerIterator() {
		return peerIterator;
	}

	@Override
	public boolean hasNext() {
		return peerIterator.hasNext();
	}

	@Override
	public E next() {
		return peerIterator.next();
	}

	@Override
	public void remove() {
		peerIterator.remove();
	}
}
