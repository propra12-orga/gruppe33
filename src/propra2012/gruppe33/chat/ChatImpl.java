package propra2012.gruppe33.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import propra2012.gruppe33.network.Client;

/**
 * 
 * @author Matthias Hesse
 *
 */
public class ChatImpl   {
	
	private Client test;
	private Thread thread;

	private BufferedReader messageReader = new BufferedReader(new InputStreamReader(System.in));	
	String message;
	
	

	
	public void connectToServer(String name, String host, int port) throws UnknownHostException, IOException{
		
		test = new Client(new Socket(host, port),name, false, null);

		thread = new Thread(test);
		thread.start();
		
	}
	
	public void sendMessage(){
		
	}
	
	public void process() throws IOException{
		while(!thread.isInterrupted()){
			message = messageReader.readLine();
			test.getOutput().writeUTF("SEND" + test.getName() +": " + message);
			
		}
		
	}
	
	
	
	public static void main(String args[]) throws NumberFormatException, IOException{
		ChatImpl chat = new ChatImpl();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));	
		
		System.out.println("Pls enter Port:");		
		int port = Integer.parseInt(reader.readLine());
		System.out.println("Pls enter Nickname:");
		String name = reader.readLine();
		System.out.println("Pls host:");
		String host = reader.readLine();
		
		
		chat.connectToServer(name, host, port);
		chat.process();
		
	}

}
