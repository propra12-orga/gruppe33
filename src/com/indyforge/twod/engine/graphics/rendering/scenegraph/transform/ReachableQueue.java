package com.indyforge.twod.engine.graphics.rendering.scenegraph.transform;

import java.util.Deque;
import java.util.LinkedList;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;

/**
 * A simple reachable queue.
 * 
 * @author Christopher Probst
 */
public class ReachableQueue extends Entity implements Reachable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
	 * Here we store all reachables.
	 */
	private final Deque<Reachable> reachables = new LinkedList<Reachable>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity#onUpdate
	 * (float)
	 */
	@Override
	protected void onUpdate(float tpf) {
		super.onUpdate(tpf);
		reach(tpf);
	}

	/**
	 * @return all reachables.
	 */
	public Deque<Reachable> reachables() {
		return reachables;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.transform.Reachable
	 * #cancel()
	 */
	@Override
	public void cancel() {
		Reachable reachable;
		while ((reachable = reachables.poll()) != null) {
			reachable.cancel();
		}
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
		Reachable reachable = reachables.peek();

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
