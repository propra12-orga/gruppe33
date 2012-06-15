package com.indyforge.foxnet.rmi.pattern.change.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.indyforge.foxnet.rmi.Invoker;
import com.indyforge.foxnet.rmi.RemoteInterfaces;
import com.indyforge.foxnet.rmi.pattern.change.AdminSessionServer;
import com.indyforge.foxnet.rmi.pattern.change.Change;
import com.indyforge.foxnet.rmi.pattern.change.Changeable;
import com.indyforge.foxnet.rmi.pattern.change.ChangeableQueue;
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

	// The cached session map
	private Map<Long, Session<T>> cachedSessionMap;

	// The cached sessions
	private List<Session<T>> cachedSessions;

	// The accepting sessions flag
	private boolean acceptingSessions = true;

	// The changeable local, broadcast and composite queue
	private final ChangeableQueue<T> local,
			broadcast = new DefaultChangeableQueue<T>(new Changeable<T>() {
				@Override
				public void applyChange(Change<T> change) {
					for (Session<T> session : sessions()) {
						try {
							session.client().applyChange(change);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}), composite;

	/**
	 * Creates a new session server using the changeable local.
	 * 
	 * @param local
	 *            The local.
	 */
	@SuppressWarnings("unchecked")
	public DefaultSessionServer(Changeable<T> local) {
		if (local == null) {
			throw new NullPointerException("local");
		}
		// Create new local changeable queue
		this.local = new DefaultChangeableQueue<T>(local);

		// Create a new composite changeable
		composite = new CompositeChangeableQueue<T>(this.local, broadcast);
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

		// Clear caches
		cachedSessionMap = null;
		cachedSessions = null;

		// Register remover!
		Invoker.of(changeable).manager().closeFuture()
				.add(new FutureCallback() {

					@Override
					public void completed(Future future) throws Exception {

						/*
						 * Synchronize with this server!
						 */
						synchronized (DefaultSessionServer.this) {

							// Remove after disconnect
							sessions.remove(id);

							// Clear caches
							cachedSessionMap = null;
							cachedSessions = null;
						}
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
	public ChangeableQueue<T> local() {
		return local;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.foxnet.rmi.pattern.change.AdminSessionServer#broadcast()
	 */
	@Override
	public ChangeableQueue<T> broadcast() {
		return broadcast;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.foxnet.rmi.pattern.change.AdminSessionServer#composite()
	 */
	@Override
	public ChangeableQueue<T> composite() {
		return composite;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.foxnet.rmi.pattern.change.AdminSessionServer#names()
	 */
	@Override
	public Map<Long, String> names() {
		Map<Long, String> ptr = new HashMap<Long, String>();
		for (Session<T> session : sessions()) {
			ptr.put(session.id(), session.name());
		}
		return ptr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.foxnet.rmi.pattern.change.AdminSessionServer#sessionMap()
	 */
	@Override
	public synchronized Map<Long, Session<T>> sessionMap() {
		if (cachedSessionMap == null) {
			cachedSessionMap = Collections
					.unmodifiableMap(new HashMap<Long, Session<T>>(sessions));
		}
		return cachedSessionMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.foxnet.rmi.pattern.change.AdminSessionServer#sessions()
	 */
	@Override
	public synchronized List<Session<T>> sessions() {
		if (cachedSessions == null) {
			cachedSessions = Collections
					.unmodifiableList(new ArrayList<Session<T>>(sessions
							.values()));
		}
		return cachedSessions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.foxnet.rmi.pattern.change.AdminSessionServer#closeAll()
	 */
	@Override
	public void closeAll() {
		for (Session<T> session : sessions()) {
			Invoker.of(session.client()).manager().close();
		}
	}
}
