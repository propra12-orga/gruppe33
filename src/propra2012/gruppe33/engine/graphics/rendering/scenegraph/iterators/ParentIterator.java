package propra2012.gruppe33.engine.graphics.rendering.scenegraph.iterators;

import java.util.NoSuchElementException;

import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class ParentIterator extends AbstractEntityIterator {

	// The next entity
	private Entity next;

	public ParentIterator(Entity root, boolean includeRoot) {
		if (root == null) {
			throw new NullPointerException("root");
		}

		// Setup next
		this.next = includeRoot ? root : root.parent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return next != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public Entity next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		// Swap
		removePtr = next;
		next = next.parent();
		return removePtr;
	}
}
