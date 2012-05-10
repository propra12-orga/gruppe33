package propra2012.gruppe33.network.tcp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class ConnectionGroup extends ConcurrentHashMap<Long, Connection> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Lock readLock = new ReentrantLock();
	private final Condition readable = readLock.newCondition();

	public ConnectionGroup() {
	}

	public ConnectionGroup(int initialCapacity, float loadFactor,
			int concurrencyLevel) {
		super(initialCapacity, loadFactor, concurrencyLevel);
	}

	public ConnectionGroup(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public ConnectionGroup(int initialCapacity) {
		super(initialCapacity);
	}

	public ConnectionGroup(Map<? extends Long, ? extends Connection> m) {
		super(m);
	}

	public Condition getReadable() {
		return readable;
	}

	public Lock getReadLock() {
		return readLock;
	}

	public void waitGroup() throws InterruptedException {
		getReadLock().lock();
		try {
			getReadable().await();
		} finally {
			getReadLock().unlock();
		}
	}

	public void notifyGroup() {
		getReadLock().lock();
		try {
			getReadable().signalAll();
		} finally {
			getReadLock().unlock();
		}
	}
}
