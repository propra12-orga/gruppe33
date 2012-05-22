package propra2012.gruppe33.engine.graphics.rendering.scenegraph.util;

import java.util.Iterator;

import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.engine.util.ArrayIterator;
import propra2012.gruppe33.engine.util.CompositeIterator;

/**
 * This is a child iterator implementation.
 * 
 * @author Christopher Probst
 */
public final class ChildIterator extends AbstractEntityIterator {

	// The peer iterator
	private final Iterator<? extends Entity> peerIterator;

	/**
	 * Creates a new child iterator using the given root. The root will be part
	 * of the iteration, too.
	 * 
	 * @param root
	 *            The root entity.
	 */
	public ChildIterator(Entity root) {
		this(root, true);
	}

	/**
	 * Creates a new child iterator using the given root.
	 * 
	 * @param root
	 *            The root entity.
	 * @param includeRoot
	 *            The include-root flag.
	 */
	@SuppressWarnings("unchecked")
	public ChildIterator(Entity root, boolean includeRoot) {
		if (root == null) {
			throw new NullPointerException("root");
		}

		// Save peer iterator
		peerIterator = includeRoot ? new CompositeIterator<Entity>(
				new ArrayIterator<Entity>(root), root.children().iterator())
				: root.children().iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return peerIterator.hasNext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public Entity next() {
		// Get the ptr and return
		return removePtr = peerIterator.next();
	}
}
