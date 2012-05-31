package com.foxnet.rmi.test.simple;

import java.io.IOException;

import com.foxnet.rmi.InvokerManager;
import com.foxnet.rmi.test.simple.SimpleRMIServer.FakultaetService;
import com.foxnet.rmi.transport.network.ConnectionManager;

public class SimpleRMIClient {

	public static void main(String[] args) throws IOException {

		// Initialisieren fuer Client (false) -gebrauch
		ConnectionManager conMgr = new ConnectionManager(false);

		// Verbindung herstellen
		InvokerManager client = conMgr.openClient("localhost", 1337);

		/*
		 * Hier lassen wir uns ein Proxy erstellen mit Hilfe des dazugehoerigen
		 * Namens. Natuerlich muss der Server fuer diesen Namen eine
		 * Registriereung haben.
		 */
		FakultaetService proxy = (FakultaetService) client
				.lookupProxy("fak_calc");

		// Unser Request (Koennte auch von Args eingelesen werden etc.)
		int fak = 5;

		// Ergebnis senden
		System.out.println("Fakultaet von " + fak + " ist " + proxy.calc(fak));
	}
}
