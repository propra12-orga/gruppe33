package com.foxnet.rmi.transport.network.handler.reqres;

import java.nio.channels.ClosedChannelException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import com.foxnet.rmi.util.Future;
import com.foxnet.rmi.util.FutureCallback;
import com.foxnet.rmi.util.Request;

@Sharable
public final class ReqResHandler extends SimpleChannelHandler {

	public static final ReqResHandler INSTANCE = new ReqResHandler();

	// The logger
	protected final Logger logger = Logger.getLogger(getClass().getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jboss.netty.channel.SimpleChannelHandler#channelOpen(org.jboss.netty
	 * .channel.ChannelHandlerContext,
	 * org.jboss.netty.channel.ChannelStateEvent)
	 */
	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		// Create a new queue at beginning
		ctx.setAttachment(new ConcurrentHashMap<Long, Request>());
		super.channelOpen(ctx, e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jboss.netty.channel.SimpleChannelHandler#channelClosed(org.jboss.
	 * netty.channel.ChannelHandlerContext,
	 * org.jboss.netty.channel.ChannelStateEvent)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {

		// Lookup the queue
		ConcurrentMap<Long, Request> requests = (ConcurrentMap<Long, Request>) ctx
				.getAttachment();

		// Clear the requests
		ctx.setAttachment(null);

		// Finish the remaining requests
		for (Object request : requests.values().toArray()) {
			((Request) request).fail(new ClosedChannelException());
		}

		super.channelClosed(ctx, e);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {

		// If reqres message...
		if (e.getMessage() instanceof ReqResMessage) {
			ReqResMessage reqResMessage = (ReqResMessage) e.getMessage();

			if (reqResMessage.isRequest()) {

				// Create new request
				Request request = new Request(reqResMessage.getData(),
						reqResMessage.getId());

				// Get final channel
				final Channel channel = ctx.getChannel();

				// Send result back when completed
				request.add(new FutureCallback() {

					@Override
					public void completed(Future future) throws Exception {
						// Convert
						Request request = (Request) future;

						// Just write the response
						channel.write(new ReqResMessage(
								request.attachment(), request.cause(),
								request.getId(), false));
					}
				});

				// Send upstream
				Channels.fireMessageReceived(ctx, request);
			} else {
				// Lookup requests
				ConcurrentMap<Long, Request> requests = (ConcurrentMap<Long, Request>) ctx
						.getAttachment();

				// Try to find the correct request
				Request request = requests.remove(reqResMessage.getId());

				if (request == null) {
					logger.warning("Response message received but "
							+ "the id=\"" + reqResMessage.getId()
							+ "\" does not represent a request");
				} else {
					// Complete the request
					request.complete(reqResMessage.getData(),
							reqResMessage.getCause());
				}
			}
		} else {
			// Not for us...
			super.messageReceived(ctx, e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void writeRequested(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {

		// Are we trying to send a request ?
		if (e.getMessage() instanceof Request) {
			// Convert
			final Request request = (Request) e.getMessage();

			// Lookup requests
			final ConcurrentMap<Long, Request> requests = (ConcurrentMap<Long, Request>) ctx
					.getAttachment();

			// Try to save the request or fail it
			if (requests.putIfAbsent(request.getId(), request) != null) {
				request.fail(new IllegalStateException("The request "
						+ "id is already used"));
			} else {
				// Remove the request when finished
				request.add(new FutureCallback() {

					@Override
					public void completed(Future future) throws Exception {
						requests.remove(((Request) future).getId());
					}
				});

				// Hook
				e.getFuture().addListener(new ChannelFutureListener() {

					@Override
					public void operationComplete(ChannelFuture future)
							throws Exception {

						/*
						 * If the write process was not successful there will
						 * never be a response.
						 */
						if (!future.isSuccess()) {
							request.fail(future.getCause());
						}
					}
				});

				// Proceed the write request
				Channels.write(ctx, e.getFuture(), new ReqResMessage(request));
			}
		} else {
			// Not for us...
			super.writeRequested(ctx, e);
		}
	}
}
