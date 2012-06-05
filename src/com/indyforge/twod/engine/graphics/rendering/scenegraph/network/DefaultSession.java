package com.indyforge.twod.engine.graphics.rendering.scenegraph.network;

import java.util.Map;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.RemoteProcessor;

public class DefaultSession implements Session {

	private final DefaultSessionServer impl;
	private final RemoteProcessor remoteProcessor;
	private final long id;
	private volatile String name;

	public DefaultSession(DefaultSessionServer impl,
			RemoteProcessor remoteProcessor, long id, String name) {
		this.impl = impl;
		this.remoteProcessor = remoteProcessor;
		this.id = id;
		name(name);
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
	public RemoteProcessor processor() {
		return remoteProcessor;
	}
}
