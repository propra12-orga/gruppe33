package com.indyforge.twod.engine.io.network.tcp.rmi.impl;

import java.lang.reflect.UndeclaredThrowableException;
import java.nio.channels.ClosedChannelException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.util.internal.ConcurrentHashMap;

import com.foxnet.rmi.Invoker;
import com.foxnet.rmi.util.Future;
import com.foxnet.rmi.util.FutureCallback;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Change;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.io.network.tcp.rmi.Server;
import com.indyforge.twod.engine.io.network.tcp.rmi.Session;
import com.indyforge.twod.engine.io.network.tcp.rmi.SessionCallback;

public class ServerImpl implements Server {

	private final AtomicLong counter = new AtomicLong(0);
	final ConcurrentMap<Long, Session> sessions = new ConcurrentHashMap<Long, Session>();
	private final Map<Long, Session> readOnly = Collections
			.unmodifiableMap(sessions);

	@Override
	public Session openSession(SessionCallback sessionCallback, String name) {

		final long id = counter.getAndIncrement();

		// Create new session
		Session s = new SessionImpl(this, sessionCallback, id, name);

		sessions.put(id, s);

		Invoker.getInvokerOf(sessionCallback).manager().closeFuture()
				.add(new FutureCallback() {

					@Override
					public void completed(Future future) throws Exception {
						sessions.remove(id);
					}
				});
		return s;
	}

	public void broadcastChange(Change change) {
		for (Session session : sessions.values()) {
			try {
				session.callback().processChange(change);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void broadcastRoot(Scene root) {
		for (Session session : sessions.values()) {
			try {
				session.callback().root(root);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Map<Long, String> users() {
		Map<Long, String> names = new HashMap<Long, String>();
		for (Session session : sessions.values()) {
			names.put(session.sessionId(), session.name());
		}
		return names;
	}

	public void broadcastMessage(Session origin, Object message) {
		for (Session session : sessions.values()) {
			try {
				session.callback().onMessage(origin.name(), message);
			} catch (UndeclaredThrowableException e) {
				if (!(e.getCause() instanceof ClosedChannelException)) {
					e.printStackTrace();
				} else {
					System.out.println(session
							+ " disconnected while receiving");
				}
			}
		}
	}

	public Map<Long, Session> sessions() {
		return readOnly;
	}

	public void closeAll() {
		for (Session session : sessions.values()) {
			Invoker.getInvokerOf(session.callback()).manager().close();
		}
	}
}
