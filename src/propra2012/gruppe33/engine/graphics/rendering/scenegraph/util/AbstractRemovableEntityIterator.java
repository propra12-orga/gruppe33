package propra2012.gruppe33.engine.graphics.rendering.scenegraph.util;

import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;

/**
 * An abstract entity iterator implementation which detaches an entity when
 * removing.
 * 
 * @author Christopher Probst
 */
public abstract class AbstractRemovableEntityIterator extends
		AbstractEntityIterator {

	// The remove pointer
	protected Entity removePtr = null;

	/**
	 * Creates a new abstract removable entity iterator using the given root.
	 * 
	 * @param root
	 *            The root entity.
	 * @param includeRoot
	 *            The include-root flag.
	 */
	public AbstractRemovableEntityIterator(Entity root, boolean includeRoot) {
		super(root, includeRoot);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		// Remove by detaching
		if (removePtr != null) {
			removePtr.detach();
			removePtr = null;
		}
	}
}
