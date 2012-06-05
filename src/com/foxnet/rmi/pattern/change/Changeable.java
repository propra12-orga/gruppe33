package com.foxnet.rmi.pattern.change;

import com.foxnet.rmi.Remote;

/**
 * 
 * @author Christopher Probst
 * 
 */
public interface Changeable<T> extends Remote {

	/**
	 * Applies the given change in a thread-safe way.
	 * 
	 * @param change
	 *            The change you want to apply.
	 */
	void applyChange(Change<T> change);
}
