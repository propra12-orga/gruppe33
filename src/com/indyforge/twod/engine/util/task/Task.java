package com.indyforge.twod.engine.util.task;

import java.io.Serializable;

/**
 * 
 * @author Christopher Probst
 */
public interface Task extends Serializable {

	/**
	 * Updates the task.
	 * 
	 * @param tpf
	 *            The time-per-frame.
	 * @return true if the task is finished now, otherwise false.
	 */
	boolean update(float tpf);
}
