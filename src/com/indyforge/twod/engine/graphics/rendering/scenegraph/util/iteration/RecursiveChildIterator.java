package com.indyforge.twod.engine.graphics.rendering.scenegraph.util.iteration;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.util.iteration.ArrayIterator;


/**
 * This is a recursive-child iterator implementation.
 * 
 * @author Christopher Probst
 */
public final class RecursiveChildIterator extends
		AbstractEntityIterator {

	// The iteration stack
	private final Deque<Iterator<? extends Entity>> stack = new LinkedList<Iterator<? extends Entity>>();

	/**
	 * Creates a new recursive child iterator using the given root. The root
	 * will be part of the iteration, too.
	 * 
	 * @param root
	 *            The root entity.
	 */
	public RecursiveChildIterator(Entity root) {
		this(root, true);
	}

	/**
	 * Creates a new recursive child iterator using the given root.
	 * 
	 * @param root
	 *            The root entity.
	 * @param includeRoot
	 *            The include-root flag.
	 */
	public RecursiveChildIterator(Entity root, boolean includeRoot) {
		if (root == null) {
			throw new NullPointerException("root");
		}

		// Create new iterator
		Iterator<? extends Entity> itr = includeRoot ? new ArrayIterator<Entity>(
				root) : new ChildIterator(root, false);

		// Only push if valid
		if (itr.hasNext()) {
			// Start with the root iterator
			stack.push(itr);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return !stack.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public Entity next() {
		// Do we have any elements ?
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		// Get itr
		Iterator<? extends Entity> itr = stack.getFirst();

		// Get next entity
		removePtr = itr.next();

		// Pop
		if (!itr.hasNext()) {
			stack.pop();
		}

		// Are there any children ?
		if (removePtr.hasChildren()) {

			// Offer next child iterator if valid
			stack.push(new ChildIterator(removePtr, false));
		}

		return removePtr;
	}
}
