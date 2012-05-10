package propra2012.gruppe33.network.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;

/**
 * 
 * @author Christopher Probst
 * @auther Matthias Hesse
 * 
 */
public class Acceptor implements Runnable {

	private ExecutorService clientExecutors;
	private long ids = 0;
	private final ServerSocket serverSocket;
	private final ConnectionGroup clients = new ConnectionGroup();

	public Acceptor(int port, ExecutorService clientExecutors)
			throws IOException {
		this(new ServerSocket(port), clientExecutors);
	}

	public Acceptor(ServerSocket serverSocket, ExecutorService clientExecutors) {
		if (serverSocket == null) {
			throw new NullPointerException("serverSocket");
		}
		if (clientExecutors == null) {
			throw new NullPointerException("clientExecutors");
		}
		this.serverSocket = serverSocket;
		this.clientExecutors = clientExecutors;
	}

	public ConnectionGroup getClients() {
		return clients;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public ExecutorService getClientExecutors() {
		return clientExecutors;
	}

	@Override
	public synchronized void run() {
		try {
			while (!Thread.interrupted()) {
				try {

					// Create a new connection
					clientExecutors.execute(new Connection(serverSocket
							.accept(), ids++, clients));
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
