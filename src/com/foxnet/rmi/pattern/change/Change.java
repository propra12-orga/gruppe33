package com.foxnet.rmi.pattern.change;

import java.io.Serializable;

/**
 * 
 * @author Christopher Probst
 * @param <T>
 *            The context type.
 */
public interface Change<T> extends Serializable {

	void apply(T ctx);
}
