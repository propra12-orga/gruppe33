package propra2012.gruppe33.io.network.tcp;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioServerApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		Selector selector = Selector.open();

		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);
		ssc.socket().bind(new InetSocketAddress(1337));
		ssc.register(selector, SelectionKey.OP_ACCEPT);

		SocketChannel sc = SocketChannel.open();
		sc.configureBlocking(false);
		sc.register(selector, SelectionKey.OP_CONNECT);

		sc.connect(new InetSocketAddress("localhost", 1337));

		for (;;) {

			if (selector.select(1000) >= 0) {

				System.out.println(selector.keys());

				// Get the iterator
				Iterator<SelectionKey> keys = selector.selectedKeys()
						.iterator();

				while (keys.hasNext()) {
					// Get key
					SelectionKey key = keys.next();

					// Remove the key directly
					keys.remove();

					if (key.isValid()) {
						if (key.isAcceptable()) {

							// Accept...
							SocketChannel client = ((ServerSocketChannel) key
									.channel()).accept();
							client.configureBlocking(false);
							System.out
									.println("Neuer client entgegengenommen: "
											+ client);

							client.register(selector, SelectionKey.OP_READ);

							ByteBuffer data = ByteBuffer.wrap("hello"
									.getBytes());
							client.write(data);
						}

						if (key.isConnectable()) {

							if (!sc.finishConnect()) {
								System.out
										.println("Verbindung nicht aufgebaut");
								sc.close();
							} else {
								System.out.println("Verbindung aufgebaut");

								// Change to read
								key.interestOps(SelectionKey.OP_READ);
							}
						}

						if (key.isReadable()) {

							SocketChannel channel = (SocketChannel) key
									.channel();

							ByteBuffer buffer = ByteBuffer.allocate(8192);

							int a;
							System.out.println((a = channel.read(buffer))
									+ " bytes read.");

							if (a == -1) {
								channel.close();
							} else {
								channel.close();
							}
						}

					}
				}
			}
		}

	}
}
