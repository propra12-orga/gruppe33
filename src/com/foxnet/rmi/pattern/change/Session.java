package com.foxnet.rmi.pattern.change;

import java.util.Map;

import com.foxnet.rmi.Remote;

/**
 * A session is the basic interface between client and server.
 * 
 * @author Christopher Probst
 * @param <T>
 *            The context type.
 */
public interface Session<T> extends Remote {

	/**
	 * @return the session id.
	 */
	long id();

	/**
	 * @return the name of this session.
	 */
	String name();

	/**
	 * Sets the name of this session.
	 * 
	 * @param name
	 *            The new name of this session.
	 */
	void name(String name);

	/**
	 * @return all active session ids mapped to the names.
	 */
	Map<Long, String> names();

	/**
	 * @return the {@link Changeable} client.
	 */
	Changeable<T> client();

	/**
	 * @return the {@link Changeable} server.
	 */
	Changeable<T> server();
}
