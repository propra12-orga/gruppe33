package propra2012.gruppe33.io.network.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;

public class NioServerApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		final SelectionManager sm = new SelectionManager();

		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);
		ssc.socket().bind(new InetSocketAddress(1337));
		ssc.register(sm.selector(), SelectionKey.OP_ACCEPT);

		Connection client = new Connection(sm);
		client.interest(SelectionKey.OP_CONNECT);
		client.channel().connect(new InetSocketAddress("localhost", 1337));

		sm.dispatchers().add(new Dispatcher() {

			@Override
			public void dispatch(SelectionKey key) throws IOException {

				if (key.isAcceptable()) {

					// Accept...
					Connection newClient = new Connection(
							((ServerSocketChannel) key.channel()).accept(), sm);

					System.out.println("Neuer client entgegengenommen: "
							+ newClient);

					newClient.interest(SelectionKey.OP_READ);

					ByteBuffer data = ByteBuffer.wrap("hello".getBytes());

					newClient.write(data);
				}

				if (key.isWritable()) {
					System.out.println("writable");
					Connection c = (Connection) key.attachment();

					ByteBuffer buffer = c.peekWriteBuffer();

					if (buffer.hasRemaining()) {
						c.channel().write(buffer);
					}

					if (!buffer.hasRemaining()) {
						c.removeWriteBuffer();
					}
				}

				if (key.isConnectable()) {

					Connection con = (Connection) key.attachment();

					if (!con.channel().finishConnect()) {
						System.out.println("Verbindung nicht aufgebaut");
						con.close();
					} else {
						System.out.println("Verbindung aufgebaut");

						// Change to read
						con.interest((con.interest() & ~SelectionKey.OP_CONNECT)
								| SelectionKey.OP_READ);
					}
				}

				if (key.isReadable()) {

					Connection con = (Connection) key.attachment();

					ByteBuffer buffer = ByteBuffer.allocate(8192);

					int a;
					System.out.println((a = con.channel().read(buffer))
							+ " bytes read.");

					con.close();
				}

			}
		});

		for (;;) {
			sm.select();
		}

	}
}
