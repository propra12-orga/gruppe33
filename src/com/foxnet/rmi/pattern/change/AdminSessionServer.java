package com.foxnet.rmi.pattern.change;

import java.util.Map;

/**
 * The admin session server adds some administrative features to the
 * {@link SessionServer}.
 * 
 * @author Christopher Probst
 * @param <T>
 *            The context type.
 */
public interface AdminSessionServer<T> extends SessionServer<T> {

	/**
	 * @return the changeable broadcast.
	 */
	Changeable<T> broadcast();

	/**
	 * @return the changeable local.
	 */
	Changeable<T> local();

	/**
	 * @return the combined (local & broadcast) changeable object.
	 */
	Changeable<T> combined();

	/**
	 * @return the accepting-sessions flag.
	 */
	boolean isAcceptingSessions();

	/**
	 * Sets the accepting-sessions flag.
	 * 
	 * @param acceptingSessions
	 *            If true, this server can accept sesssion.
	 */
	void acceptingSessions(boolean acceptingSessions);

	/**
	 * @return the names.
	 */
	Map<Long, String> names();

	/**
	 * @return the number of sessions.
	 */
	int sessionCount();

	/**
	 * @return the sessions.
	 */
	Map<Long, Session<T>> sessions();

	/**
	 * Closes all sessions.
	 */
	void closeAll();
}
