package com.foxnet.rmi.pattern.change;

import java.io.Serializable;

/**
 * Represents some kind of change. Basically this interface is used to replace
 * the network protocol. If you want to do something you just have to implement
 * a change and send it to the remote where it will processed.
 * 
 * @author Christopher Probst
 * @param <T>
 *            The context type.
 */
public interface Change<T> extends Serializable {

	/**
	 * Applies the change to the given context.
	 * 
	 * @param ctx
	 *            The context.
	 */
	void apply(T ctx);
}
