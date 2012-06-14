package com.indyforge.foxnet.rmi.pattern.change;

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
	 * @return the changeable broadcast queue.
	 */
	ChangeableQueue<T> broadcast();

	/**
	 * @return the changeable local queue.
	 */
	ChangeableQueue<T> local();

	/**
	 * @return the composite (local & broadcast) changeable queue.
	 */
	ChangeableQueue<T> composite();

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
	 * @param id
	 *            The session id.
	 * @return the session with the given id or null.
	 */
	Session<T> session(long id);

	/**
	 * Closes all sessions.
	 */
	void closeAll();
}
