package propra2012.gruppe33.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {

	private final ServerSocket s;
	private final ExecutorService clientExecutor;
	private final LinkedList<Client> clients = new LinkedList<Client>();

	public Server(int port, ExecutorService clientExecutor) throws IOException {
		s = new ServerSocket(port, 5);
		this.clientExecutor = clientExecutor;
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			try {
				// Verbindung zulassen
				Socket clientSocket = s.accept();

				// Short info
				System.out.println("New client has connect: " + clientSocket);

				// Create new client
				Client client = new Client(clientSocket, "Host", true, clients);

				clients.add(client);

				// Starte den client
				clientExecutor.execute(client);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		// Der threadpool
		ExecutorService executor = Executors.newCachedThreadPool();

		// Neuer server
		Server server = new Server(1337, executor);

		// Start accepting
		executor.execute(server);
//
//		// Connect to server
//		Client client = new Client(new Socket("localhost", 1337), false, null);
//
//		// Starte den clienten
//		executor.execute(client);
//
//		// Connect to server
//		Client client2 = new Client(new Socket("localhost", 1337), false, null);
//
//		// Starte den clienten
//		executor.execute(client2);
//
//		client.getOutput().writeUTF("SEND Hello this is a broadcastet message");

	}

}
