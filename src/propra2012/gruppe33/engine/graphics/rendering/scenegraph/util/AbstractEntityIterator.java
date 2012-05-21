package propra2012.gruppe33.engine.graphics.rendering.scenegraph.util;

import java.util.Iterator;

import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;

/**
 * An abstract entity iterator implementation.
 * 
 * @author Christopher Probst
 * 
 */
public abstract class AbstractEntityIterator implements Iterator<Entity>,
		Iterable<Entity> {

	// The root entity
	protected final Entity root;

	// The include root flag
	protected final boolean includeRoot;

	/**
	 * Creates a new abstract entity iterator using the given root.
	 * 
	 * @param root
	 *            The root entity.
	 * @param includeRoot
	 *            The include-root flag.
	 */
	public AbstractEntityIterator(Entity root, boolean includeRoot) {
		if (root == null) {
			throw new NullPointerException("root");
		}

		// Save the parameter
		this.root = root;
		this.includeRoot = includeRoot;
	}
}
