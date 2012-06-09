package com.foxnet.rmi.pattern.change;

import com.foxnet.rmi.Remote;

/**
 * The session server which is able to retrieve and manage session.
 * 
 * @author Christopher Probst
 * 
 * @param <T>
 *            The context type.
 */
public interface SessionServer<T> extends Remote {

	/**
	 * Opens a new session using the given {@link Changeable} implementation and
	 * the name or null if the server does not allow more sessions.
	 * 
	 * @param changeable
	 *            The {@link Changeable} implementation of the session.
	 * @param name
	 *            The name of the session.
	 * @return a session implementation or null.
	 */
	Session<T> openSession(Changeable<T> changeable, String name);
}
