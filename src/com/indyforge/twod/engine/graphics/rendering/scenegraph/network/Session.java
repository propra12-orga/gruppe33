package com.indyforge.twod.engine.graphics.rendering.scenegraph.network;

import java.util.Map;

import com.foxnet.rmi.Remote;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.RemoteProcessor;

/**
 * 
 * @author Christopher Probst
 * 
 */
public interface Session extends Remote {

	long sessionId();

	String name();

	void name(String name);

	Map<Long, String> users();

	RemoteProcessor processor();
}
