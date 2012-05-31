package com.foxnet.rmi.test.simple;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class SimpleClient {

	public static void main(String[] args) throws IOException {

		// Verbindung zum Server herstellen (Blockiert)
		Socket client = new Socket("localhost", 1337);

		// Input oeffnen
		DataInputStream in = new DataInputStream(client.getInputStream());

		// Output oeffnen
		DataOutputStream out = new DataOutputStream(client.getOutputStream());

		// Unser Request (Koennte auch von Args eingelesen werden etc.)
		int fak = 5;

		// Berechne Fakultaet 5
		out.writeInt(5);

		// Ergebnis senden
		System.out.println("Fakultaet von " + fak + " ist " + in.readLong());
	}
}
