package com.foxnet.rmi.pattern;

import com.foxnet.rmi.Remote;

public interface BroadcastServerService extends Remote {

	long register(BroadcastClientService client) throws Exception;
}
