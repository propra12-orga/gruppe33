package com.indyforge.foxnet.rmi.transport.network.handler.invocation;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

import com.indyforge.foxnet.rmi.Invocation;
import com.indyforge.foxnet.rmi.Invoker;
import com.indyforge.foxnet.rmi.InvokerManager;
import com.indyforge.foxnet.rmi.LookupException;
import com.indyforge.foxnet.rmi.binding.RemoteBinding;
import com.indyforge.foxnet.rmi.binding.RemoteObject;
import com.indyforge.foxnet.rmi.binding.registry.StaticRegistry;
import com.indyforge.foxnet.rmi.transport.network.handler.lookup.LookupHandler;
import com.indyforge.foxnet.rmi.util.Future;
import com.indyforge.foxnet.rmi.util.FutureCallback;
import com.indyforge.foxnet.rmi.util.Request;

final class NetworkInvokerManager extends InvokerManager {

	private final Channel channel;

	public NetworkInvokerManager(StaticRegistry staticRegistry, Channel channel) {
		super(staticRegistry);
		this.channel = channel;

		channel.getCloseFuture().addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				closeFuture().complete(null, future.getCause());
			}
		});
	}

	@Override
	protected void sendInvocation(final Invocation invocation) {

		if (!invocation.isAsyncVoid()) {
			// Create request
			Request req = new Request(invocation.message());

			// Write the request
			channel.write(req);

			req.add(new FutureCallback() {

				@Override
				public void completed(Future future) throws Exception {
					invocation.complete(future.attachment(), future.cause());
				}
			});
		} else {
			channel.write(invocation.message()).addListener(
					new ChannelFutureListener() {

						@Override
						public void operationComplete(ChannelFuture future)
								throws Exception {
							invocation.complete(null, future.getCause());
						}
					});
		}
	}

	@Override
	public String[] lookupNames() throws LookupException {
		Request req = LookupHandler.newLookupAll();
		channel.write(req);
		if (req.synchronize()) {
			return (String[]) req.attachment();
		} else {
			throw new LookupException(req.cause());
		}
	}

	@Override
	public Invoker lookupInvoker(String target) throws LookupException {

		Request req = LookupHandler.newLookup(target);
		channel.write(req);
		if (req.synchronize()) {
			return new Invoker(this, new RemoteBinding(
					(RemoteObject) req.attachment(), false));
		} else {
			throw new LookupException(req.cause());
		}
	}

	@Override
	public Future close() {
		channel.close();
		return closeFuture();
	}
}
