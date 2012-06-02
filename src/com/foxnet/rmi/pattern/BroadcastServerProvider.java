package com.foxnet.rmi.pattern;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.util.internal.ConcurrentHashMap;

import com.foxnet.rmi.Invoker;
import com.foxnet.rmi.util.Future;
import com.foxnet.rmi.util.FutureCallback;

public class BroadcastServerProvider implements BroadcastServerService {

	private final AtomicLong counter = new AtomicLong(0);
	private final ConcurrentMap<Long, BroadcastClientService> clients = new ConcurrentHashMap<Long, BroadcastClientService>();
	private final Map<Long, BroadcastClientService> readOnly = Collections
			.unmodifiableMap(clients);

	@Override
	public long register(BroadcastClientService client) throws Exception {
		final long id = counter.getAndIncrement();
		clients.put(id, client);

		Invoker.getInvokerOf(client).manager().closeFuture()
				.add(new FutureCallback() {

					@Override
					public void completed(Future future) throws Exception {
						clients.remove(id);
					}
				});
		return id;
	}

	public Map<Long, BroadcastClientService> clients() {
		return readOnly;
	}

	public <T> Iterator<T> clientIterator(Class<T> type) {
		return new Iterator<T>() {
			private final Iterator<BroadcastClientService> peer = clients
					.values().iterator();

			@Override
			public boolean hasNext() {
				return peer.hasNext();
			}

			@SuppressWarnings("unchecked")
			@Override
			public T next() {
				return (T) peer.next();
			}

			@Override
			public void remove() {
				peer.remove();
			}
		};
	}

	public void closeAll() {
		for (BroadcastClientService client : clients.values()) {
			Invoker.getInvokerOf(client).manager().close();
		}
	}
}
