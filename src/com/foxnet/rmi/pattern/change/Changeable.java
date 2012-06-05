package com.foxnet.rmi.pattern.change;

import com.foxnet.rmi.Remote;

/**
 * The interface is used to make an object changeable.
 * 
 * @author Christopher Probst
 * @param <T>
 *            The context type.
 */
public interface Changeable<T> extends Remote {

	/**
	 * Applies the given change in a thread-safe way.
	 * 
	 * @param change
	 *            The {@link Change} you want to apply.
	 */
	void applyChange(Change<T> change);
}
