package com.indyforge.twod.engine.graphics.rendering.scenegraph.transform;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A simple reachable queue.
 * 
 * @author Christopher Probst
 */
public class ReachableQueue<R extends Reachable> implements Reachable,
		Iterable<R> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
	 * Here we store all reachables.
	 */
	private final Deque<R> reachables = new LinkedList<R>();

	/**
	 * @return all reachables.
	 */
	public Deque<R> reachables() {
		return reachables;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<R> iterator() {
		return reachables.iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.transform.Reachable
	 * #reach(float)
	 */
	@Override
	public boolean reach(float tpf) {
		// Peek the next reachable
		R reachable = reachables.peek();

		// Not empty ?
		if (reachable != null) {

			// Update...
			if (reachable.reach(tpf)) {

				// If reachable is reached, remove it!
				reachables.remove();
			}
		}
		return reachables.isEmpty();
	}
}
