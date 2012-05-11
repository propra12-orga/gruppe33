package propra2012.gruppe33.io.network.tcp;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;

public class Connection implements Closeable {

	private final SelectionManager selectionManager;
	private final SocketChannel channel;
	private final Socket socket;
	private ByteBuffer readBuffer = ByteBuffer.allocate(8192);

	private final Queue<ByteBuffer> writeQueue = new LinkedList<ByteBuffer>();

	public Connection(SelectionManager selectionManager) throws IOException {
		this(SocketChannel.open(), selectionManager);
	}

	public Connection(SocketChannel channel, SelectionManager selectionManager)
			throws IOException {
		this.channel = channel;
		this.selectionManager = selectionManager;
		channel.configureBlocking(false);
		socket = channel.socket();
		socket.setReceiveBufferSize(0xFFFF);
		socket.setSendBufferSize(0xFFFF);
		socket.setTcpNoDelay(true);
	}

	public SelectionManager getSelectionManager() {
		return selectionManager;
	}

	synchronized void removeWriteBuffer() {
		writeQueue.poll();

		if (writeQueue.isEmpty()) {
			SelectionKey key = channel.keyFor(selectionManager.selector());
			if (key != null) {
				key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
			}
		}
	}

	public synchronized ByteBuffer peekWriteBuffer() {
		return writeQueue.peek();
	}

	public synchronized void write(ByteBuffer buffer)
			throws ClosedChannelException {
		if (writeQueue.isEmpty()) {
			SelectionKey key = channel.keyFor(selectionManager.selector());
			if (key == null) {
				channel.register(selectionManager.selector(),
						SelectionKey.OP_WRITE, this);
			} else {
				key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
			}
		}

		writeQueue.add(buffer);
	}

	public ByteBuffer getReadBuffer() {
		return readBuffer;
	}

	public int interest() {
		return interest(null);
	}

	public int interest(Selector selector) {
		if (selector == null) {
			selector = selectionManager.selector();
		}
		SelectionKey key = channel.keyFor(selector);
		return key != null ? key.interestOps() : 0;
	}

	public void interest(int flags) throws ClosedChannelException {
		interest(null, flags);
	}

	public void interest(Selector selector, int flags)
			throws ClosedChannelException {
		if (selector == null) {
			selector = selectionManager.selector();
		}

		SelectionKey key = channel.keyFor(selector);
		if (key == null) {
			channel.register(selector, flags, this);
		} else {
			key.interestOps(flags);
		}
	}

	public void setReadBuffer(ByteBuffer readBuffer) {
		this.readBuffer = readBuffer;
	}

	public SocketChannel channel() {
		return channel;
	}

	public Socket socket() {
		return socket;
	}

	@Override
	public void close() throws IOException {
		channel.close();
	}
}
