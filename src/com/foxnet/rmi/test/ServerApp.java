package com.foxnet.rmi.test;

import java.io.IOException;

import com.foxnet.rmi.Invoker;
import com.foxnet.rmi.InvokerManager;
import com.foxnet.rmi.Remote;
import com.foxnet.rmi.transport.network.ConnectionManager;

public class ServerApp implements ServerInterface, Remote {

	@Override
	public void dieLiefertNichtsZurueck() {
		System.out.println("void  methode");
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		// SERVER
		ConnectionManager cm = new ConnectionManager(true);

		cm.openServer(1337);
		cm.getStaticRegistry().bind("void-stuff", new ServerApp());

		// CLIENT
		ConnectionManager ccm = new ConnectionManager(false);

		InvokerManager client = ccm.openClient("localhost", 1337);

		Invoker invoker = client.lookupInvoker("void-stuff");

		((ServerInterface) invoker.lookupProxy("void-stuff"))
				.dieLiefertNichtsZurueck();
	}
}
