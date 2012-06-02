package com.foxnet.rmi.transport.network.handler.invocation;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

import com.foxnet.rmi.Invocation;
import com.foxnet.rmi.Invoker;
import com.foxnet.rmi.InvokerManager;
import com.foxnet.rmi.binding.RemoteBinding;
import com.foxnet.rmi.util.Future;
import com.foxnet.rmi.util.FutureCallback;
import com.foxnet.rmi.util.Request;

/**
 * 
 * @author Christopher Probst
 * 
 */
final class NetworkInvoker extends Invoker {

	private final Channel channel;

	public NetworkInvoker(Channel channel, InvokerManager invokerFactory,
			RemoteBinding remoteBinding) {
		super(invokerFactory, remoteBinding);

		this.channel = channel;
	}

	public Channel channel() {
		return channel;
	}

	@Override
	protected void writeInvocation(final Invocation invocation) {

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
}
