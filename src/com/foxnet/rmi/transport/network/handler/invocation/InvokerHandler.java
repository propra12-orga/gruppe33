package com.foxnet.rmi.transport.network.handler.invocation;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import com.foxnet.rmi.InvocationMessage;
import com.foxnet.rmi.InvokerManager;
import com.foxnet.rmi.transport.network.ConnectionManager;
import com.foxnet.rmi.util.Future;
import com.foxnet.rmi.util.Request;

@Sharable
public class InvokerHandler extends SimpleChannelHandler {

	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		final Channel c = ctx.getChannel();
		ctx.setAttachment(new NetworkInvokerManager(ConnectionManager.of(c)
				.staticReg(), c));

		super.channelOpen(ctx, e);
	}

	public static InvokerManager of(Channel channel) {
		return (InvokerManager) channel.getPipeline()
				.getContext(InvokerHandler.class).getAttachment();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {

		ctx.sendUpstream(e);
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {

		Future fut = null;
		InvocationMessage im = null;

		if (e.getMessage() instanceof Request) {
			// Convert to request
			Request request = (Request) e.getMessage();

			if (request.data() instanceof InvocationMessage) {
				im = (InvocationMessage) request.data();
				fut = request;
			}
		} else if (e.getMessage() instanceof InvocationMessage) {
			// Async invocation
			im = (InvocationMessage) e.getMessage();
		}

		if (im != null) {
			ConnectionManager cm = ConnectionManager.of(ctx.getChannel());
			NetworkInvokerManager fac = (NetworkInvokerManager) ctx
					.getAttachment();
			if (cm != null) {

				// Do invocation
				fac.handleInvocation(im, cm.methodInvocator(), fut);
			}
		} else {

			super.messageReceived(ctx, e);
		}
	}
}
