package propra2012.gruppe33.network.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class Acceptor implements Runnable {

	private ExecutorService clientExecutors;
	private long ids = 0;
	private final ServerSocket serverSocket;
	private final Map<Long, Connection> clients = new ConcurrentHashMap<Long, Connection>();

	public Acceptor(int port) throws IOException {
		this(new ServerSocket(port));
	}

	public Acceptor(ServerSocket serverSocket) {
		if (serverSocket == null) {
			throw new NullPointerException("serverSocket");
		}
		this.serverSocket = serverSocket;
	}

	public Map<Long, Connection> getClients() {
		return clients;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	@Override
	public synchronized void run() {
		try {
			while (!Thread.interrupted()) {
				try {

					// Create a new connection
					Connection client = new Connection(serverSocket.accept(), ids++, clients);
				} catch (Exception e) {
					System.err.println("Failed to accept connection. Reason: "
							+ e.getMessage());
				}
			}
		} finally {
			try {

				// Close the server socket
				serverSocket.close();
			} catch (Exception e2) {
				System.err.println("Failed to close server socket. Reason: "
						+ e2.getMessage());
			}
		}
	}
}
