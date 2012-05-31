package com.foxnet.rmi.transport.network.handler.invocation;

import java.io.IOException;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import com.foxnet.rmi.Invocation;
import com.foxnet.rmi.Invoker;
import com.foxnet.rmi.InvokerManager;
import com.foxnet.rmi.binding.RemoteBinding;
import com.foxnet.rmi.binding.RemoteObject;
import com.foxnet.rmi.binding.registry.DynamicRegistry;
import com.foxnet.rmi.binding.registry.StaticRegistry;
import com.foxnet.rmi.transport.network.ConnectionManager;
import com.foxnet.rmi.transport.network.handler.lookup.LookupHandler;
import com.foxnet.rmi.util.Future;
import com.foxnet.rmi.util.FutureCallback;
import com.foxnet.rmi.util.Request;

@Sharable
public class InvokerHandler extends SimpleChannelHandler {

	static class NetworkInvoker extends Invoker {

		private final Channel channel;

		public NetworkInvoker(Channel channel, InvokerManager invokerFactory,
				RemoteBinding remoteBinding) {
			super(invokerFactory, remoteBinding);

			this.channel = channel;
		}

		public Channel getChannel() {
			return channel;
		}

		@Override
		public Invocation invoke(int methodId, Object... args) {

			// Convert
			localsToRemotes(args);

			// Create invocation
			final Invocation in = newInvocation(methodId, args);

			// Create the message now
			InvocationMessage invMsg = new InvocationMessage(binding()
					.isDynamic(), binding().id(), methodId, args);

			if (!RmiRoutines.isAsyncVoid(in)) {
				// Create request
				Request req = new Request(invMsg);

				// Write the request
				channel.write(req);

				req.add(new FutureCallback() {

					@Override
					public void completed(Future future) throws Exception {
						in.complete(future.attachment(), future.cause());
					}
				});
			} else {
				channel.write(invMsg).addListener(new ChannelFutureListener() {

					@Override
					public void operationComplete(ChannelFuture future)
							throws Exception {
						in.complete(null, future.getCause());
					}
				});
			}

			return in;
		}
	}

	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		final Channel c = ctx.getChannel();
		ctx.setAttachment(new InvokerManager() {

			private final DynamicRegistry dr = new DynamicRegistry();

			@Override
			public Object remoteToLocal(Object argument) {
				return RmiRoutines.remoteToLocal(this, argument);
			}

			@Override
			public Object localToRemote(Object argument) {
				return RmiRoutines.localToRemote(this, argument);
			}

			@Override
			public Invoker invoker(RemoteBinding remoteBinding) {
				return new NetworkInvoker(c, this, remoteBinding);
			}

			@Override
			public StaticRegistry statically() {
				return ConnectionManager.of(c).getStaticRegistry();
			}

			@Override
			public DynamicRegistry dynamically() {
				return dr;
			}

			@Override
			public Invoker lookupInvoker(String target) throws IOException {

				Request req = LookupHandler.newLookup(target);
				c.write(req);
				if (req.synchronize()) {

					final RemoteBinding rb = new RemoteBinding(
							(RemoteObject) req.attachment(), false);

					return invoker(rb);

				} else {
					throw new IOException(req.cause());
				}

			}

		});

		super.channelOpen(ctx, e);
	}

	public static InvokerManager of(Channel channel) {
		return (InvokerManager) channel.getPipeline()
				.getContext(InvokerHandler.class).getAttachment();
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {

		Future fut = null;
		InvocationMessage im = null;

		if (e.getMessage() instanceof Request) {
			// Convert to request
			Request request = (Request) e.getMessage();

			if (request.getData() instanceof InvocationMessage) {
				im = (InvocationMessage) request.getData();
				fut = request;
			}
		} else if (e.getMessage() instanceof InvocationMessage) {
			// Async invocation
			im = (InvocationMessage) e.getMessage();
		}

		if (im != null) {
			ConnectionManager cm = ConnectionManager.of(ctx.getChannel());
			InvokerManager fac = (InvokerManager) ctx.getAttachment();
			if (cm != null) {

				// Do invocation
				RmiRoutines.invoke(im.isDynamic(), im.getBindingId(),
						cm.getMethodInvocator(), fac, fut, im.getMethodId(),
						im.getArguments());

			}
		} else {

			super.messageReceived(ctx, e);
		}
	}
}
