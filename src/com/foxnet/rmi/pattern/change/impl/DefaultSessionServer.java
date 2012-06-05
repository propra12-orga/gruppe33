package com.foxnet.rmi.pattern.change.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.util.internal.ConcurrentHashMap;

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
public class DefaultSessionServer<T> implements AdminSessionServer<T> {

	private final AtomicLong counter = new AtomicLong(0);
	final ConcurrentMap<Long, Session<T>> sessions = new ConcurrentHashMap<Long, Session<T>>();
	private final Map<Long, Session<T>> readOnly = Collections
			.unmodifiableMap(sessions);

	private final Changeable<T> peer;

	public DefaultSessionServer(Changeable<T> peer) {
		this.peer = peer;
	}

	@Override
	public void applyChange(Change<T> change) {
		peer.applyChange(change);
	}

	@Override
	public Session<T> openSession(Changeable<T> changeable, String name) {

		final long id = counter.getAndIncrement();

		// Create new session
		Session<T> s = new DefaultSession<T>(this, changeable, id, name);

		sessions.put(id, s);

		Invoker.getInvokerOf(changeable).manager().closeFuture()
				.add(new FutureCallback() {

					@Override
					public void completed(Future future) throws Exception {
						sessions.remove(id);
					}
				});
		return s;
	}

	@Override
	public void broadcastChange(Change<T> change) {
		for (Session<T> session : sessions.values()) {
			try {
				session.changeable().applyChange(change);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Map<Long, String> users() {
		Map<Long, String> names = new HashMap<Long, String>();
		for (Session<T> session : sessions.values()) {
			names.put(session.sessionId(), session.name());
		}
		return names;
	}

	@Override
	public Map<Long, Session<T>> sessions() {
		return readOnly;
	}

	@Override
	public void closeAll() {
		for (Session<T> session : sessions.values()) {
			Invoker.getInvokerOf(session.changeable()).manager().close();
		}
	}
}
