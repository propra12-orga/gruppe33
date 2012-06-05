package com.foxnet.rmi.pattern.change;

import com.foxnet.rmi.Remote;

public interface SessionServer<T> extends Remote {

	Session<T> openSession(Changeable<T> changeable, String name);
}
