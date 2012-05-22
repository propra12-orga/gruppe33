package propra2012.gruppe33.engine.graphics.rendering.scenegraph.util;

import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.engine.util.EmptyIterator;
import propra2012.gruppe33.engine.util.WrappedIterator;

/**
 * This is a sibling iterator implementation.
 * 
 * @author Christopher Probst
 */
public final class SiblingIterator extends WrappedIterator<Entity> {

	/**
	 * Creates a new sibling iterator using the given root. The root parent will
	 * be part of the iteration, too.
	 * 
	 * @param root
	 *            The root entity.
	 */
	public SiblingIterator(Entity root) {
		this(root, true);
	}

	/**
	 * Creates a new sibling iterator using the given root.
	 * 
	 * @param root
	 *            The root entity.
	 * @param includeRootParent
	 *            The include-root-parent flag.
	 */
	public SiblingIterator(Entity root, boolean includeRootParent) {
		super(root.parent() != null ? new ChildIterator(root.parent(),
				includeRootParent) : new EmptyIterator<Entity>());
	}
}
