package propra2012.gruppe33.network.udp.broadcast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

/**
 * Simple broadcaster class. Basically you initialize this class with a given
 * address and then all requests on this address are answered with your message.
 * 
 * 
 * TODO: Doc.
 * 
 * @author Christopher Probst
 */
public final class Broadcaster extends Thread {

	/**
	 * Protocol constant for sending the message.
	 */
	public static final short SEND_ME_YOUR_MESSAGE = 1337;

	/**
	 * This is maximum size. UDP should handle 65536 but due to router
	 * fragmentation the half is more safe.
	 */
	public static final int MAX_MESSAGE_SIZE = 32768;

	/**
	 * Even this value is quite huge. Its a default value.
	 */
	public static final int MAX_READ_SIZE = 128;

	public static Object receiveMessageFrom(int port, int timeoutmillies)
			throws IOException, ClassNotFoundException {
		return receiveMessageFrom(
				new InetSocketAddress(InetAddress.getByName("255.255.255.255"),
						port), timeoutmillies);
	}

	/**
	 * This method is the client which sends and receives packets.
	 * 
	 * @param target
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object receiveMessageFrom(SocketAddress target,
			int timeoutmillies) throws IOException, ClassNotFoundException {

		// Just open, does not matter which port
		DatagramSocket receiver = new DatagramSocket();

		// Use timeout
		receiver.setSoTimeout(timeoutmillies);

		// Build request
		ByteBuffer buffer = ByteBuffer.allocate(2);
		buffer.putShort(SEND_ME_YOUR_MESSAGE);
		byte[] request = buffer.array();

		// Create packet
		DatagramPacket packet = new DatagramPacket(request, request.length,
				target);

		// Send the request
		receiver.send(packet);

		// Assume max size...
		byte[] response = new byte[MAX_MESSAGE_SIZE];

		// Define answer
		DatagramPacket answer = new DatagramPacket(response, response.length);

		// Try to read answer
		receiver.receive(answer);

		// Create a new parser
		ObjectInputStream parser = new ObjectInputStream(
				new ByteArrayInputStream(answer.getData(), answer.getOffset(),
						answer.getLength()));
		try {
			// Read the message and return
			return parser.readObject();
		} finally {
			receiver.close();
			parser.close();
		}
	}

	/*
	 * This is the udp broadcaster.
	 */
	private final DatagramSocket udpServer;

	/*
	 * This message will be broadcasted to all listeners.
	 */
	private final Object message;

	/*
	 * The serialized message and the read buffer.
	 */
	private final byte[] binary, readBuffer;

	public Broadcaster(int port, Object message) throws IOException {
		this(new InetSocketAddress(port), message);
	}

	public Broadcaster(SocketAddress address, Object message)
			throws IOException {
		if (address == null) {
			throw new NullPointerException("address");
		} else if (message == null) {
			throw new NullPointerException("message");
		}

		// Create a new udp socket
		udpServer = new DatagramSocket(address);

		// Save the message
		this.message = message;

		/*
		 * *******************************
		 * 
		 * **** Serialize the message ****
		 * 
		 * *******************************
		 */

		// Used for serializing
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		// Create a new object output on top of the array
		ObjectOutputStream objectOutput = new ObjectOutputStream(output);

		// Write message
		objectOutput.writeObject(message);

		// Flush to output
		objectOutput.flush();

		// Close the stream
		objectOutput.close();

		// Copy and store as final var
		binary = output.toByteArray();

		if (binary.length > MAX_MESSAGE_SIZE) {
			// Well... owned...
			throw new IllegalArgumentException("Your message is "
					+ "too big in binary format.");
		} else {
			// Read should be pretty small...
			readBuffer = new byte[MAX_READ_SIZE];
		}
	}

	public Object getMessage() {
		return message;
	}

	public byte[] getBinary() {
		return binary;
	}

	public DatagramSocket getUdpServer() {
		return udpServer;
	}

	@Override
	public void run() {
		// As long as the thread is not interrupted
		while (!Thread.interrupted()) {

			// Create new read packet
			DatagramPacket packet = new DatagramPacket(readBuffer,
					readBuffer.length);

			try {
				// Try to receive
				udpServer.receive(packet);

				// Log
				System.out.println("Received broadcast request");

				// Create a parser on top of the received packet
				DataInputStream packetParser = new DataInputStream(
						new ByteArrayInputStream(packet.getData(),
								packet.getOffset(), packet.getLength()));

				/*
				 * Was this package meant to be answered by us.
				 */
				if (packetParser.readShort() == SEND_ME_YOUR_MESSAGE) {

					// Create new message packet
					DatagramPacket msgPacket = new DatagramPacket(binary,
							binary.length, packet.getSocketAddress());

					// Now send the packet to its destination
					udpServer.send(msgPacket);
				}
			} catch (IOException e) {
				System.out
						.println("UDP Broadcaster was unable to process IO. Reason: "
								+ e.getMessage());

				// Enough...
				break;
			}

		}

		// Close the socket...
		udpServer.close();
	}
}
