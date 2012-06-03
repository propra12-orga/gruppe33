package com.indyforge.twod.engine.io.network.tcp.rmi;

import com.foxnet.rmi.Remote;

public interface Server extends Remote {

	Session openSession(SessionCallback sessionCallback, String name);

}
