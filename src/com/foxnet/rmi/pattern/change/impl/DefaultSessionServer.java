package com.foxnet.rmi.pattern.change.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.foxnet.rmi.Invoker;
import com.foxnet.rmi.RemoteInterfaces;
import com.foxnet.rmi.pattern.change.AdminSessionServer;
import com.foxnet.rmi.pattern.change.Change;
import com.foxnet.rmi.pattern.change.Changeable;
import com.foxnet.rmi.pattern.change.Session;
import com.foxnet.rmi.pattern.change.SessionServer;
import com.foxnet.rmi.util.Future;
import com.foxnet.rmi.util.FutureCallback;

@RemoteInterfaces(SessionServer.class)
public final class DefaultSessionServer<T> implements AdminSessionServer<T> {

	// Used to count the ids
	private final AtomicLong counter = new AtomicLong(0);

	// The network lock
	private final Object netLock = new Object();

	// The maps which store the sessions
	private final Map<Long, Session<T>> sessions = new HashMap<Long, Session<T>>();

	// The changeable local and broadcast
	private final Changeable<T> local, broadcast = new Changeable<T>() {
		@Override
		public void applyChange(Change<T> change) {
			for (Session<T> session : sessions.values()) {
				try {
					((DefaultSession<T>) session).changeable
							.applyChange(change);
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
	 * @Override(non-Javadoc)
	 * 
	 * @see
	 * com.foxnet.rmi.pattern.change.SessionServer#openSession(com.foxnet.rmi
	 * .pattern.change.Changeable, java.lang.String)
	 */
	public Session<T> openSession(Changeable<T> changeable, String name) {

		if (changeable == null) {
			throw new NullPointerException("changeable");
		}
		// Calc new id
		final long id = counter.getAndIncrement();

		// Create new session
		Session<T> s = new DefaultSession<T>(this, changeable, id, name);

		// Put into map
		synchronized (netLock) {

			// Put into map
			sessions.put(id, s);
			Invoker.getInvokerOf(changeable).manager().closeFuture()
					.add(new FutureCallback() {

						@Override
						public void completed(Future future) throws Exception {
							// Remove after disconnect
							sessions.remove(id);
						}
					});
		}
		return s;
	}

	@Override
	public Changeable<T> local() {
		return local;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxnet.rmi.pattern.change.AdminSessionServer#broadcast()
	 */
	@Override
	public Changeable<T> broadcast() {
		return broadcast;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxnet.rmi.pattern.change.AdminSessionServer#combined()
	 */
	@Override
	public Changeable<T> combined() {
		return combined;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxnet.rmi.pattern.change.AdminSessionServer#names()
	 */
	@Override
	public Map<Long, String> names() {
		Map<Long, String> ptr = new HashMap<Long, String>();
		synchronized (netLock) {
			for (Session<T> session : sessions.values()) {
				ptr.put(session.id(), session.name());
			}
		}
		return ptr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxnet.rmi.pattern.change.AdminSessionServer#sessions()
	 */
	@Override
	public Map<Long, Session<T>> sessions() {
		Map<Long, Session<T>> ptr = new HashMap<Long, Session<T>>();
		synchronized (netLock) {
			ptr.putAll(sessions);
		}
		return ptr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxnet.rmi.pattern.change.AdminSessionServer#closeAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void closeAll() {
		synchronized (netLock) {
			for (Object session : sessions.values().toArray()) {
				Invoker.getInvokerOf(((DefaultSession<T>) session).changeable)
						.manager().close();
			}
		}
	}
}
