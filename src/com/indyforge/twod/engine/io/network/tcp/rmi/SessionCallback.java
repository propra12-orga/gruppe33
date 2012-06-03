package com.indyforge.twod.engine.io.network.tcp.rmi;

import com.foxnet.rmi.Remote;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Change;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;

public interface SessionCallback extends Remote {

	void onMessage(String origin, Object message);

	void processChange(Change change);

	void root(Scene root) throws Exception;
}
