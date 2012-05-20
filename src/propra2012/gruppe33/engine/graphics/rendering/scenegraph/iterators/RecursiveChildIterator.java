package propra2012.gruppe33.engine.graphics.rendering.scenegraph.iterators;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;

/**
 * @author Christopher Probst
 * 
 */
public final class RecursiveChildIterator extends AbstractEntityIterator {

	// The iteration stack
	private final Deque<Iterator<? extends Entity>> stack = new LinkedList<Iterator<? extends Entity>>();

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
