package com.indyforge.twod.engine.graphics.rendering.scenegraph.network;

import com.foxnet.rmi.Remote;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.RemoteProcessor;

public interface SessionServer extends Remote {

	Session openSession(RemoteProcessor remoteProcessor, String name);

}
