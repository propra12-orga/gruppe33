package com.indyforge.twod.engine.graphics.rendering.scenegraph.network;

import java.util.Map;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Change;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.EntityFilter;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;

/**
 * 
 * @author Christopher Probst
 * 
 */
public interface AdminSessionServer extends SessionServer {

	void broadcastChange(Change change);

	void broadcastRoot(Scene root);

	Map<Long, String> users();

	void broadcastSceneEvent(EntityFilter entityFilter, Object event,
			Object... params);

	void broadcastSceneEvent(Object event, Object... params);

	Map<Long, Session> sessions();

	void closeAll();
}
