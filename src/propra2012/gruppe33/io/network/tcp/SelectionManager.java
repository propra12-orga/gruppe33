package propra2012.gruppe33.io.network.tcp;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class SelectionManager {

	private final Selector selector = Selector.open();
	private final Set<Dispatcher> dispatchers = new CopyOnWriteArraySet<Dispatcher>();

	private void dispatch() throws IOException {

		// Get iterator for all keys
		Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

		while (keys.hasNext()) {
			// Get next key in line
			SelectionKey key = keys.next();

			// Check the key
			if (!key.isValid()) {
				continue;
			}

			try {

				// Dispatch all keys
				for (Object dispatcher : dispatchers) {
					((Dispatcher) dispatcher).dispatch(key);
				}
			} catch (Exception e) {
				System.err.println("Failed to dispatch " + key + ". Reason: "
						+ e);
			} finally {
				keys.remove();
			}
		}
	}

	public SelectionManager() throws IOException {
	}

	public Set<Dispatcher> dispatchers() {
		return dispatchers;
	}

	public Selector selector() {
		return selector;
	}

	public void selectNow() throws IOException {
		if (selector.selectNow() > 0) {
			dispatch();
		}
	}

	public void select(long timeout) throws IOException {
		if (selector.select(timeout) > 0) {
			dispatch();
		}
	}

	public void select() throws IOException {
		if (selector.select() > 0) {
			dispatch();
		}
	}
}
