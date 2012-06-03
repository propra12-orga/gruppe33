package com.indyforge.twod.engine.io.network.tcp.rmi.impl;

import java.io.IOException;
import java.util.Map;

import com.indyforge.twod.engine.io.network.tcp.rmi.Session;
import com.indyforge.twod.engine.io.network.tcp.rmi.SessionCallback;

public class SessionImpl implements Session {

	private final ServerImpl impl;
	private final SessionCallback sessionCallback;
	private final long id;
	private volatile String name;

	public SessionImpl(ServerImpl impl, SessionCallback sessionCallback,
			long id, String name) {
		this.impl = impl;
		this.sessionCallback = sessionCallback;
		this.id = id;
		name(name);
	}

	@Override
	public void close() throws IOException {
		impl.sessions.remove(id);
	}

	@Override
	public long sessionId() {
		return id;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public void name(String name) {
		if (name == null || name.length() < 2) {
			name = "Undefined";
		}
		this.name = name;
	}

	@Override
	public Map<Long, String> users() {
		return impl.users();
	}

	@Override
	public SessionCallback callback() {
		return sessionCallback;
	}

	@Override
	public void sendToAll(Object message) {
		impl.broadcastMessage(this, message);
	}
}
