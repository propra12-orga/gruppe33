package propra2012.gruppe33.engine.graphics.rendering.scenegraph.iterators;

import java.util.Iterator;

import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class ChildIterator extends AbstractEntityIterator {

	// The peer iterator
	private final Iterator<? extends Entity> peerIterator;

	public ChildIterator(Entity root) {
		this(root, true);
	}

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
