package propra2012.gruppe33.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

/**
 * 
 * @author Matthias Hesse
 *
 */

public class Client implements Runnable {

	private final Socket s;
	private final DataInputStream input;
	private final DataOutputStream output;
	private final boolean isServerSide;
	private final LinkedList<Client> clients;
	private String name;
	
	

	public Client(Socket s, String name, boolean ser, LinkedList<Client> clients)
			throws IOException {
		this.s = s;
		this.clients = clients;
		this.name = name;
		input = new DataInputStream(s.getInputStream());
		output = new DataOutputStream(s.getOutputStream());
		isServerSide = ser;
	}

	public DataOutputStream getOutput() {
		return output;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public void run() {
		while (!Thread.interrupted()) {
			try {

				// REad command
				String command = input.readUTF();

				if (isServerSide) {

					System.out.println("Server command received: " + command);

					if (command.startsWith("GET")) {
						output.writeUTF("OK <html stuff>");
					} else if (command.startsWith("SEND")) {
						command = command.replaceFirst("SEND", "");
						for (Client c : clients) {
							c.getOutput().writeUTF(command);
						}
					}
				} else {
					System.out.println( command);

					if (command.startsWith("OK")) {
						System.out.println("html site received: "
								+ command.substring(3));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}
}