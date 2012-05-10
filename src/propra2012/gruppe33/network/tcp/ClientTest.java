package propra2012.gruppe33.network.tcp;

public class ClientTest {

	public static void main(String[] args) throws Exception {

		Connection c = new Connection("localhost", 1337, 2, null);

		c.getOutput().writeObject("hello server");
	}
}
