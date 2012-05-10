package propra2012.gruppe33.network.tcp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerTest {

	public static void main(String[] args) throws Exception {

		ExecutorService threadPool = Executors.newCachedThreadPool();

		// Open the acceptor
		Acceptor acceptor = new Acceptor(1337, threadPool);

		threadPool.execute(acceptor);
		
		while (true) {
			acceptor.getClients().waitGroup();

			for (Connection c : acceptor.getClients().values()) {
				Object message = c.getMessages().poll();
				if (message != null) {
					System.out.println("Ser: Received: " + message);
				}
			}
		}

	}
}
