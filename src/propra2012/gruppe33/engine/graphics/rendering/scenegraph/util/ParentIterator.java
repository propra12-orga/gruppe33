package propra2012.gruppe33.engine.graphics.rendering.scenegraph.util;

import java.util.NoSuchElementException;

import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;

/**
 * This is a parent iterator implementation.
 * 
 * @author Christopher Probst
 */
public final class ParentIterator extends AbstractEntityIterator {

	// The next entity
	private Entity next;

	/**
	 * Creates a new parent iterator using the given root. The root will be part
	 * of the iteration, too.
	 * 
	 * @param root
	 *            The root entity.
	 */
	public ParentIterator(Entity root) {
		this(root, true);
	}

	/**
	 * Creates a new parent iterator using the given root.
	 * 
	 * @param root
	 *            The root entity.
	 * @param includeRoot
	 *            The include-root flag.
	 */
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
