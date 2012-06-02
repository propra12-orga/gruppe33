package propra2012.gruppe33.test.multiplayer;

import java.lang.reflect.UndeclaredThrowableException;
import java.nio.channels.ClosedChannelException;
import java.util.Iterator;

import com.foxnet.rmi.InvokerManager;
import com.foxnet.rmi.pattern.BroadcastClientService;
import com.foxnet.rmi.pattern.BroadcastServerProvider;
import com.foxnet.rmi.pattern.BroadcastServerService;
import com.foxnet.rmi.transport.network.ConnectionManager;

public class ServerApp {

	public static interface ChatClient extends BroadcastClientService {

		void send(String message);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		// Servers
		ConnectionManager servers = new ConnectionManager(true);

		BroadcastServerProvider b = new BroadcastServerProvider();

		// Bind new impl
		servers.statical().bind("files", b);

		servers.openServer(1337);

		// Clients
		ConnectionManager clients = new ConnectionManager(false);

		InvokerManager im = clients.openClient("localhost", 1337);
		BroadcastServerService server = (BroadcastServerService) im
				.lookupProxy("files");

		server.register(new ChatClient() {
			@Override
			public void send(String message) {
				System.out.println(message);
			}
		});

		Iterator<ChatClient> chats = b.clientIterator(ChatClient.class);

		im.close().synchronize();

		while (chats.hasNext()) {
			try {
				chats.next().send("hello");
			} catch (UndeclaredThrowableException e) {
				if (e.getCause() instanceof ClosedChannelException) {
					System.err.println("Verbinding getrennt: " + e.getCause());
				} else {
					e.printStackTrace();
				}
			}
		}
	}
}
