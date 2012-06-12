package com.indyforge.twod.engine.graphics.rendering.scenegraph.transform;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class TargetQueue<T extends Target<?>> extends ReachableQueue<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Clears the targets. If there is a first target, it will be stopped.
	 */
	public void clear() {
		if (!reachables().isEmpty()) {
			reachables().getFirst().stop();
		}
		reachables().clear();
	}
}
