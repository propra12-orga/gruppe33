package com.indyforge.twod.engine.graphics.rendering.scenegraph.network;

import java.lang.reflect.UndeclaredThrowableException;
import java.nio.channels.ClosedChannelException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.util.internal.ConcurrentHashMap;

import com.foxnet.rmi.Invoker;
import com.foxnet.rmi.RemoteInterfaces;
import com.foxnet.rmi.util.Future;
import com.foxnet.rmi.util.FutureCallback;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Change;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.EntityFilter;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.RemoteProcessor;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;

@RemoteInterfaces(SessionServer.class)
public class DefaultSessionServer implements AdminSessionServer {

	private final AtomicLong counter = new AtomicLong(0);
	final ConcurrentMap<Long, Session> sessions = new ConcurrentHashMap<Long, Session>();
	private final Map<Long, Session> readOnly = Collections
			.unmodifiableMap(sessions);

	@Override
	public Session openSession(RemoteProcessor remoteProcessor, String name) {

		final long id = counter.getAndIncrement();

		// Create new session
		Session s = new DefaultSession(this, remoteProcessor, id, name);

		sessions.put(id, s);

		Invoker.getInvokerOf(remoteProcessor).manager().closeFuture()
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
				session.processor().addChange(change);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void broadcastRoot(Scene root) {
		for (Session session : sessions.values()) {
			try {
				session.processor().root(root);
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

	@Override
	public void broadcastSceneEvent(EntityFilter entityFilter, Object event,
			Object... params) {

		for (Session session : sessions.values()) {
			try {
				session.processor().fireSceneEvent(entityFilter, event, params);
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

	@Override
	public void broadcastSceneEvent(Object event, Object... params) {
		broadcastSceneEvent(null, event, params);
	}

	public Map<Long, Session> sessions() {
		return readOnly;
	}

	public void closeAll() {
		for (Session session : sessions.values()) {
			Invoker.getInvokerOf(session.processor()).manager().close();
		}
	}
}
