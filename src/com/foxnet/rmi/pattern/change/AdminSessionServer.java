package com.foxnet.rmi.pattern.change;

import java.util.Map;

/**
 * 
 * @author Christopher Probst
 * 
 */
public interface AdminSessionServer<T> extends SessionServer<T>, Changeable<T> {

	void broadcastChange(Change<T> change);

	Map<Long, String> users();

	Map<Long, Session<T>> sessions();

	void closeAll();
}
