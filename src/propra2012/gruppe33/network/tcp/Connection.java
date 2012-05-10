package propra2012.gruppe33.network.tcp;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 * TODO: Doc.
 * 
 * @author Christopher Probst
 * @auther Matthias Hesse
 */
public class Connection implements Runnable, Closeable {

	public static final int MAX_QUEUED_OBJECTS = 256;

	public static final Object CLOSED = new Object();

	// Define input and output
	private final ObjectOutputStream output;
	private final ObjectInputStream input;

	// Here we store all message which we receive
	private final BlockingQueue<Object> messages = new LinkedBlockingQueue<Object>(
			MAX_QUEUED_OBJECTS);

	// The connection group
	private final ConnectionGroup connectionGroup;

	// The server flag
	private final boolean server;

	// The id of this connection
	private final long id;

	// The socket of the client
	private final Socket socket;

	public Connection(String host, int port, long id,
			ConnectionGroup connectionGroup) throws IOException {
		this(new Socket(host, port), false, id, connectionGroup);
	}

	public Connection(Socket socket, long id, ConnectionGroup connectionGroup)
			throws IOException {
		this(socket, true, id, connectionGroup);
	}

	private Connection(Socket socket, boolean server, long id,
			ConnectionGroup connectionGroup) throws IOException {

		// The socket cannot be null
		if (socket == null) {
			throw new NullPointerException("socket");
		}

		// Save the socket
		this.socket = socket;

		// Save the flag
		this.server = server;

		// Save the long
		this.id = id;

		// Save the connection group
		this.connectionGroup = connectionGroup;

		// Add if valid
		if (connectionGroup != null) {
			connectionGroup.put(id, this);
		}

		// Create the streams
		output = new ObjectOutputStream(socket.getOutputStream());
		input = new ObjectInputStream(socket.getInputStream());
	}

	public boolean isServer() {
		return server;
	}

	public boolean isClient() {
		return !server;
	}

	public long getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		socket.close();
	}

	/**
	 * @return the input.
	 */
	public ObjectInputStream getInput() {
		return input;
	}

	/**
	 * 
	 * @return the output.
	 */
	public ObjectOutputStream getOutput() {
		return output;
	}

	public BlockingQueue<Object> getMessages() {
		return messages;
	}

	public ConnectionGroup getConnectionGroup() {
		return connectionGroup;
	}

	/**
	 * 
	 * @return the socket.
	 */
	public Socket getSocket() {
		return socket;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public synchronized void run() {

		try {
			// Process as long as the thread is alive
			while (!Thread.interrupted()) {

				try {
					// Read next object
					messages.put(input.readObject());
					if (connectionGroup != null) {
						connectionGroup.notifyGroup();
					}
				} catch (Exception e) {
					// Failed to read from socket...
					System.err.println("Failed to read from socket. Reason: "
							+ e.getMessage());

					break;
				}

			}
		} finally {

			try {
				// Try to offer
				messages.put(CLOSED);
				if (connectionGroup != null) {
					connectionGroup.notifyGroup();
				}
			} catch (Exception e) {
				System.err.println("Failed to offer closed-signature. Reason: "
						+ e.getMessage());
			} finally {

				try {

					// Close the socket
					socket.close();
				} catch (Exception e2) {
					System.err.println("Failed to close socket. Reason: "
							+ e2.getMessage());
				} finally {
					if (connectionGroup != null) {
						// Remove from connection group
						connectionGroup.remove(id);
					}
				}
			}
		}
	}
}
