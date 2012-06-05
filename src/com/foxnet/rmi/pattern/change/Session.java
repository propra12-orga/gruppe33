package com.foxnet.rmi.pattern.change;

import java.util.Map;

import com.foxnet.rmi.AsyncVoid;
import com.foxnet.rmi.OrderedExecution;

/**
 * 
 * @author Christopher Probst
 * 
 */
public interface Session<T> extends Changeable<T> {

	long sessionId();

	String name();

	void name(String name);

	Map<Long, String> users();

	Changeable<T> changeable();

	@OrderedExecution
	@AsyncVoid
	@Override
	public void applyChange(Change<T> change);
}
