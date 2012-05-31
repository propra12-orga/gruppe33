package com.foxnet.rmi.test.simple;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Christopher Probst
 */
public class SimpleServer {

	public static void main(String[] args) throws IOException {

		// Neuen ServerSocket erstellen und an Port 1337 binden
		ServerSocket server = new ServerSocket(1337);
		try {
			// Solange wie der Thread nicht interrupted ist
			while (!Thread.interrupted()) {

				// Client akzeptieren (Blockiert)
				final Socket client = server.accept();

				/*
				 * WICHTIG: Hier wird ein neuer Thread erstellt, der den Request
				 * des Clienten einließt und dann das Ergebnis berechnet,
				 * welches anschließend wieder zurueck geschickt wird.
				 */
				new Thread() {

					@Override
					public void run() {

						try {
							// Input oeffnen
							DataInputStream in = new DataInputStream(
									client.getInputStream());

							// Output oeffnen
							DataOutputStream out = new DataOutputStream(
									client.getOutputStream());

							// Request-ints lesen
							int fak = in.readInt();

							// Fakultaet berechnen
							long ergebnis = 1;
							for (int i = 2; i <= fak; i++) {
								ergebnis *= i;
							}

							// Ergebnis senden
							out.writeLong(ergebnis);
						} catch (IOException e) {
							// Log
							System.err.println("Problem beim bearbeiten "
									+ "des Requests: " + e.getMessage());
						} finally {
							// Auf jeden Fall den Socket schließen!
							try {
								client.close();
							} catch (IOException e) {
								// Log
								System.err.println("Problem beim Schließen: "
										+ e.getMessage());
							}
						}
					}
				}.start();
			}
		} finally {
			// Auch der Server muss auf jeden Fall geschlossen werden
			server.close();
		}
	}
}
