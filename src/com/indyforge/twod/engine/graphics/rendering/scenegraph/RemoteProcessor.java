package com.indyforge.twod.engine.graphics.rendering.scenegraph;

import com.foxnet.rmi.Remote;

/**
 * 
 * @author Christopher Probst
 * 
 */
public interface RemoteProcessor extends Remote {

	/**
	 * @see Entity#fireEvent(EntityFilter, Object, Object...)
	 */
	void fireSceneEvent(EntityFilter entityFilter, Object event,
			Object... params);

	/**
	 * @see Entity#fireEvent(Object, Object...)
	 */
	void fireSceneEvent(Object event, Object... params);

	/**
	 * Applies the given change in a thread-safe way.
	 * 
	 * @param change
	 *            The change you want to apply.
	 */
	void addChange(Change change);

	/**
	 * Sets the root of this processor and resets the time. This method is
	 * thread-safe.
	 * 
	 * @param root
	 *            The root you want to set.
	 */
	void root(Scene root);

}
