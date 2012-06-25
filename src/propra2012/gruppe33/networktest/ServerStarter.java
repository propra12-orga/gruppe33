package propra2012.gruppe33.networktest;

public class ServerStarter {

	public static void main(String[] args) {
		try {
			ServerApp.newServer();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
