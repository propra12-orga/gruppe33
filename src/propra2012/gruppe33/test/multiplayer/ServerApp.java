package propra2012.gruppe33.test.multiplayer;

import propra2012.gruppe33.bomberman.io.RFMImpl;
import propra2012.gruppe33.bomberman.io.RemoteFileManager;

import com.foxnet.rmi.InvokerManager;
import com.foxnet.rmi.transport.network.ConnectionManager;

public class ServerApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		// Servers
		ConnectionManager servers = new ConnectionManager(true);

		// Bind new impl
		servers.getStaticRegistry().bind("files", new RFMImpl());

		servers.openServer(1337);

		// Clients
		ConnectionManager clients = new ConnectionManager(false);
		InvokerManager im = clients.openClient("localhost", 1337);
		RemoteFileManager rfm = (RemoteFileManager) im.lookupProxy("files");

	}
}
