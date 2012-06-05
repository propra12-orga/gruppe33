package com.foxnet.rmi.pattern.change.impl;

import java.util.Map;

import com.foxnet.rmi.pattern.change.AdminSessionServer;
import com.foxnet.rmi.pattern.change.Change;
import com.foxnet.rmi.pattern.change.Changeable;
import com.foxnet.rmi.pattern.change.Session;

public class DefaultSession<T> implements Session<T> {

	private final AdminSessionServer<T> server;
	private final Changeable<T> changeable;
	private final long id;
	private volatile String name;

	public DefaultSession(AdminSessionServer<T> server,
			Changeable<T> changeable, long id, String name) {
		this.server = server;
		this.changeable = changeable;
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
		return server.users();
	}

	@Override
	public Changeable<T> changeable() {
		return changeable;
	}

	@Override
	public void applyChange(Change<T> change) {
		// Proceed to server
		server.applyChange(change);
	}
}
