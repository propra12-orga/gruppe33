package propra2012.gruppe33.network.udp.broadcast;

public class BroadcastTestApp {

	/**
	 * 
	 * YES YES I KNOW, i could have used JUnit.... But I really do not need this
	 * stuff... JDK only =).
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		Broadcaster server = new Broadcaster(1337, "hello client");
		server.start();

		System.out.println(Broadcaster.receiveMessageFrom(1337, 10000));

	}
}
