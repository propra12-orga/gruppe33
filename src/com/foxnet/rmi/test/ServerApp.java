package com.foxnet.rmi.test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import com.foxnet.rmi.InvokerManager;
import com.foxnet.rmi.Remote;
import com.foxnet.rmi.transport.network.ConnectionManager;

public class ServerApp implements Runnable, Remote {

	final AtomicInteger i = new AtomicInteger(0);

	@Override
	public void run() {
		i.getAndIncrement();
		System.out.println("void methode -> " + i.get());
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {

		// // SERVER

		ConnectionManager cm = new ConnectionManager(true);

		ServerApp s = new ServerApp();
		cm.openServer(1337);
		cm.staticReg().bind("void-stuff", s);

		// CLIENT
		ConnectionManager ccm = new ConnectionManager(false);

		InvokerManager client = ccm.openClient("localhost", 1337);

		// Lookup a proxy object
		Runnable run = (Runnable) client.lookupProxy("void-stuff");

		run.run();
	}
}
