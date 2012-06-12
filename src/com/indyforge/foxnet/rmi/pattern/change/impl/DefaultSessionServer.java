package com.indyforge.foxnet.rmi.pattern.change.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.indyforge.foxnet.rmi.Invoker;
import com.indyforge.foxnet.rmi.RemoteInterfaces;
import com.indyforge.foxnet.rmi.pattern.change.AdminSessionServer;
import com.indyforge.foxnet.rmi.pattern.change.Change;
import com.indyforge.foxnet.rmi.pattern.change.Changeable;
import com.indyforge.foxnet.rmi.pattern.change.Session;
import com.indyforge.foxnet.rmi.pattern.change.SessionServer;
import com.indyforge.foxnet.rmi.util.Future;
import com.indyforge.foxnet.rmi.util.FutureCallback;

@RemoteInterfaces(SessionServer.class)
public final class DefaultSessionServer<T> implements AdminSessionServer<T> {

	// Used to count the ids
	private final AtomicLong counter = new AtomicLong(0);

	// The maps which store the sessions
	private final Map<Long, Session<T>> sessions = new HashMap<Long, Session<T>>();

	// The accepting sessions flag
	private boolean acceptingSessions = true;

	// The changeable local and broadcast
	private final Changeable<T> local, broadcast = new Changeable<T>() {
		@Override
		public void applyChange(Change<T> change) {
			for (Session<T> session : sessions.values()) {
				try {
					session.client().applyChange(change);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}, combined = new Changeable<T>() {
		@Override
		public void applyChange(Change<T> change) {
			local.applyChange(change);
			broadcast.applyChange(change);
		}
	};

	/**
	 * Creates a new session server using the changeable local.
	 * 
	 * @param local
	 *            The local.
	 */
	public DefaultSessionServer(Changeable<T> local) {
		if (local == null) {
			throw new NullPointerException("local");
		}
		this.local = local;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.foxnet.rmi.pattern.change.AdminSessionServer#
	 * isAcceptingSessions()
	 */
	@Override
	public synchronized boolean isAcceptingSessions() {
		return acceptingSessions;
	}

	/*
	 * 
	 */
	@Override
	public synchronized Session<T> session(long id) {
		return sessions.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.foxnet.rmi.pattern.change.AdminSessionServer#acceptingSessions
	 * (boolean)
	 */
	@Override
	public synchronized void acceptingSessions(boolean acceptingSessions) {
		this.acceptingSessions = acceptingSessions;
	}

	/*
	 * @Override(non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.foxnet.rmi.pattern.change.SessionServer#openSession(com
	 * .indyforge.foxnet.rmi .pattern.change.Changeable, java.lang.String)
	 */
	public synchronized Session<T> openSession(Changeable<T> changeable,
			String name) {

		if (changeable == null) {
			throw new NullPointerException("changeable");
		}

		/*
		 * If this server does not accept sessions, just return null.
		 */
		if (!acceptingSessions) {
			return null;
		}

		// Calc new id
		final long id = counter.getAndIncrement();

		// Create new session
		Session<T> s = new DefaultSession<T>(this, changeable, id, name);

		// Put into map
		sessions.put(id, s);
		Invoker.of(changeable).manager().closeFuture()
				.add(new FutureCallback() {

					@Override
					public void completed(Future future) throws Exception {
						// Remove after disconnect
						sessions.remove(id);
					}
				});

		return s;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.foxnet.rmi.pattern.change.AdminSessionServer#sessionCount()
	 */
	@Override
	public synchronized int sessionCount() {
		return sessions.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.foxnet.rmi.pattern.change.AdminSessionServer#local()
	 */
	@Override
	public Changeable<T> local() {
		return local;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.foxnet.rmi.pattern.change.AdminSessionServer#broadcast()
	 */
	@Override
	public Changeable<T> broadcast() {
		return broadcast;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.foxnet.rmi.pattern.change.AdminSessionServer#combined()
	 */
	@Override
	public Changeable<T> combined() {
		return combined;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.foxnet.rmi.pattern.change.AdminSessionServer#names()
	 */
	@Override
	public synchronized Map<Long, String> names() {
		Map<Long, String> ptr = new HashMap<Long, String>();
		for (Session<T> session : sessions.values()) {
			ptr.put(session.id(), session.name());
		}
		return ptr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.foxnet.rmi.pattern.change.AdminSessionServer#sessions()
	 */
	@Override
	public synchronized Map<Long, Session<T>> sessions() {
		return new HashMap<Long, Session<T>>(sessions);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.foxnet.rmi.pattern.change.AdminSessionServer#closeAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public synchronized void closeAll() {
		for (Object session : sessions.values().toArray()) {
			Invoker.of(((Session<T>) session).client()).manager().close();
		}
	}
}
