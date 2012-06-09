package com.foxnet.rmi.pattern.change.impl;

import java.util.Map;

import com.foxnet.rmi.pattern.change.AdminSessionServer;
import com.foxnet.rmi.pattern.change.Changeable;
import com.foxnet.rmi.pattern.change.Session;

/**
 * The default implementation of {@link Session}.
 * 
 * @author Christopher Probst
 * 
 * @param <T>
 *            The context type.
 */
final class DefaultSession<T> implements Session<T> {

	// The server which manages this session
	private final AdminSessionServer<T> server;

	// The changeable implementation of the session
	private final Changeable<T> changeable;

	// The session id
	private final long id;

	// The name of the session
	private volatile String name;

	public DefaultSession(AdminSessionServer<T> server,
			Changeable<T> changeable, long id, String name) {
		if (server == null) {
			throw new NullPointerException("server");
		} else if (changeable == null) {
			throw new NullPointerException("changeable");
		}

		// Save
		this.server = server;
		this.changeable = changeable;
		this.id = id;

		// Set name
		name(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxnet.rmi.pattern.change.Session#id()
	 */
	@Override
	public long id() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxnet.rmi.pattern.change.Session#name()
	 */
	@Override
	public String name() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxnet.rmi.pattern.change.Session#name(java.lang.String)
	 */
	@Override
	public void name(String name) {
		if (name == null || name.length() < 2) {
			name = "Undefined";
		}
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxnet.rmi.pattern.change.Session#names()
	 */
	@Override
	public Map<Long, String> names() {
		return server.names();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxnet.rmi.pattern.change.Session#client()
	 */
	@Override
	public Changeable<T> client() {
		return changeable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxnet.rmi.pattern.change.Session#server()
	 */
	@Override
	public Changeable<T> server() {
		// Proceed to server
		return server.local();
	}
}
