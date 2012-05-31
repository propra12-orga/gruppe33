package com.foxnet.rmi.test.simple;

import com.foxnet.rmi.Remote;
import com.foxnet.rmi.transport.network.ConnectionManager;

public class SimpleRMIServer {

	public static interface FakultaetService extends Remote {

		/**
		 * Der Fakultaet Dienst bereitgestellt durch dieses Interface.
		 */
		long calc(int fak);
	}

	public static class FakultaetProvider implements FakultaetService {

		@Override
		public long calc(int fak) {
			// Fakultaet berechnen
			long ergebnis = 1;
			for (int i = 2; i <= fak; i++) {
				ergebnis *= i;
			}
			return ergebnis;
		}
	}

	public static void main(String[] args) {

		// Initialisieren fuer Server (true) -gebrauch
		ConnectionManager conMgr = new ConnectionManager(true);

		// Server oeffnen
		conMgr.openServer(1337);

		/*
		 * Sehr wichtig: Hier registrieren wir den Provider, der die Impl. des
		 * Fakultaetdienstes bereitstellt.
		 */
		conMgr.getStaticRegistry().bind("fak_calc", new FakultaetProvider());
	}
}
