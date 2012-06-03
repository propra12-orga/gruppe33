package com.indyforge.twod.engine.io.network.tcp.rmi;

import java.io.Closeable;
import java.util.Map;

import com.foxnet.rmi.Remote;

public interface Session extends Remote, Closeable {

	long sessionId();

	String name();

	void name(String name);

	Map<Long, String> users();

	SessionCallback callback();

	void sendToAll(Object message);
}
