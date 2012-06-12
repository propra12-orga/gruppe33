package com.indyforge.twod.engine.graphics.rendering.scenegraph.transform;

import java.io.Serializable;

/**
 * 
 * @author Christopher Probst
 */
public interface Reachable extends Serializable {

	/**
	 * Tries to reach the reachable.
	 * 
	 * @param tpf
	 *            The time-per-frame.
	 * @return true if the reachable is reached now, otherwise false.
	 */
	boolean reach(float tpf);
}
