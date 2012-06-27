package com.indyforge.twod.engine.util.task;

/**
 * 
 * @author Christopher Probst
 * 
 */
public interface CancellableTask extends Task {

	/**
	 * Cancels the operation.
	 */
	void cancel();
}
