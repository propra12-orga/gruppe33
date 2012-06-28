/*
 * Copyright (C) 2011 Christopher Probst
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of the 'FoxNet RMI' nor the names of its 
 *   contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.indyforge.foxnet.rmi.transport.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ServerChannel;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.compression.ZlibDecoder;
import org.jboss.netty.handler.codec.compression.ZlibEncoder;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;
import org.jboss.netty.util.internal.ExecutorUtil;

import com.indyforge.foxnet.rmi.InvokerManager;
import com.indyforge.foxnet.rmi.binding.registry.StaticRegistry;
import com.indyforge.foxnet.rmi.transport.network.handler.invocation.InvokerHandler;
import com.indyforge.foxnet.rmi.transport.network.handler.lookup.LookupHandler;
import com.indyforge.foxnet.rmi.transport.network.handler.reqres.ReqResHandler;
import com.indyforge.foxnet.rmi.transport.network.handler.setup.SetupHandler;

/**
 * @author Christopher Probst
 */
public final class ConnectionManager implements ChannelPipelineFactory {

	/**
	 * @param channel
	 *            The channel.
	 * @return the connection manager of the given channel.
	 */
	public static ConnectionManager of(Channel channel) {
		if (channel == null) {
			throw new NullPointerException("channel");
		}

		// Get the channel handler context
		ChannelHandlerContext ctx = channel.getPipeline().getContext(
				IdentificationHandler.class);

		return ctx != null ? (ConnectionManager) ctx.getAttachment() : null;
	}

	/*
	 * Used to identify channels.
	 */
	@Sharable
	private final class IdentificationHandler extends
			SimpleChannelUpstreamHandler {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.jboss.netty.channel.SimpleChannelUpstreamHandler#channelOpen(
		 * org.jboss.netty.channel.ChannelHandlerContext,
		 * org.jboss.netty.channel.ChannelStateEvent)
		 */
		@Override
		public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)
				throws Exception {

			// Set attachment
			ctx.setAttachment(ConnectionManager.this);

			// Add server channel!
			channels.add(e.getChannel());

			super.channelOpen(ctx, e);
		}
	}

	// The bootstraps of this connection manager
	private final ServerBootstrap serverBootstrap;
	private final ClientBootstrap clientBootstrap;

	// The identification handler of this connection manager
	private final IdentificationHandler identificationHandler = new IdentificationHandler();

	// Used to bind targets statically
	private final StaticRegistry staticRegistry = new StaticRegistry();

	// Cached network executor
	private final Executor networkExecutor = Executors.newCachedThreadPool();

	// The channel factories which are used to create channels
	private final ChannelFactory serverChannelFactory, clientChannelFactory;

	// Used to invoke methods
	private final Executor methodInvocator;

	// Used to limit thread usage
	private final ThreadUsage threadUsage;

	// Create a channel group to store connections
	private final ChannelGroup channels = new DefaultChannelGroup();

	// The disposed flag
	private final AtomicBoolean disposed = new AtomicBoolean(false);

	public ConnectionManager(boolean serversOnly) {
		this(null, serversOnly, !serversOnly);
	}

	public ConnectionManager(boolean supportServers, boolean supportClients) {
		this(null, supportServers, supportClients);
	}

	public ConnectionManager(ThreadUsage threadUsage, boolean supportServers,
			boolean supportClients) {

		if (!supportServers && !supportClients) {
			throw new IllegalArgumentException("You must provide "
					+ "at least one component");
		}

		// Check thread usage
		if (threadUsage == null) {
			threadUsage = ThreadUsage.DEFAULT;
		}

		// Save the thread usage
		this.threadUsage = threadUsage;

		// The default method executor
		methodInvocator = Executors
				.newFixedThreadPool(threadUsage.invocationThreads);

		// If this manager should handle servers
		if (supportServers) {

			// Create the channel factoriy
			serverChannelFactory = new NioServerSocketChannelFactory(
					networkExecutor, networkExecutor,
					threadUsage.networkThreads);

			// Create the bootstrap
			serverBootstrap = new ServerBootstrap(serverChannelFactory);

			// Setup the pipeline factory
			serverBootstrap.setPipelineFactory(this);

			// Add the parent handler
			serverBootstrap.setParentHandler(identificationHandler);
		} else {
			serverChannelFactory = null;
			serverBootstrap = null;
		}

		// If this manager should handle clients
		if (supportClients) {
			// Create the channel factoriy
			clientChannelFactory = new NioClientSocketChannelFactory(
					networkExecutor, networkExecutor,
					threadUsage.networkThreads);

			// Create the bootstrap
			clientBootstrap = new ClientBootstrap(clientChannelFactory);

			// Setup the pipeline factory
			clientBootstrap.setPipelineFactory(this);
		} else {
			clientChannelFactory = null;
			clientBootstrap = null;
		}
	}

	public InvokerManager openClient(SocketAddress socketAddress)
			throws IOException {
		// Get future
		ChannelFuture connectFuture = openClientAsync(socketAddress);

		// Await!
		connectFuture.awaitUninterruptibly();

		if (!connectFuture.isSuccess()) {
			throw new IOException("Connection failed", connectFuture.getCause());
		}

		// Extract handler
		return InvokerHandler.of(connectFuture.getChannel());
	}

	public InvokerManager openClient(String host, int port) throws IOException {
		return openClient(new InetSocketAddress(host, port));
	}

	public ChannelFuture openClientAsync(SocketAddress socketAddress) {
		if (!isSupportingClients()) {
			throw new IllegalStateException("This connection manager "
					+ "does not support clients");
		}

		// Connect to server
		return clientBootstrap.connect(socketAddress);
	}

	public ServerChannel openServer(int port) {
		return openServer(new InetSocketAddress(port));
	}

	public ServerChannel openServer(SocketAddress socketAddress) {
		if (!isSupportingServers()) {
			throw new IllegalStateException("This connection manager "
					+ "does not support servers");
		}
		return (ServerChannel) serverBootstrap.bind(socketAddress);
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		// Setup the pipeline
		ChannelPipeline channelPipeline = Channels.pipeline();

//		channelPipeline.addLast("t", new SimpleChannelHandler() {
//
//			long d = System.currentTimeMillis();
//			int count = 0;
//
//			@Override
//			public void writeRequested(ChannelHandlerContext ctx, MessageEvent e)
//					throws Exception {
//
//				synchronized (ctx) {
//					count += ((ChannelBuffer) e.getMessage()).readableBytes();
//
//					if (System.currentTimeMillis() - d > 1000) {
//						System.out.println(count + " Bytes/s");
//						count = 0;
//						d = System.currentTimeMillis();
//					}
//				}
//
//				super.writeRequested(ctx, e);
//			}
//		});

		// Used to identify the channel
		channelPipeline.addLast("id_handler", identificationHandler);

		// Add good compression
		channelPipeline.addLast("in_com", new ZlibDecoder());
		channelPipeline.addLast("out_com", new ZlibEncoder(1));

		// Add config manager
		channelPipeline.addLast("cfg", SetupHandler.INSTANCE);

		// Use the default decoder
		channelPipeline.addLast(
				"obj_decoder",
				new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers
						.weakCachingResolver(Thread.currentThread()
								.getContextClassLoader())));

		// Use the default encoder
		channelPipeline.addLast("obj_encoder", new ObjectEncoder(0xFFFF));

		// The request response handler
		channelPipeline.addLast("reqres", ReqResHandler.INSTANCE);

		// Support lookup
		channelPipeline.addLast("lookup", LookupHandler.INSTANCE);

		// Handle invocations
		channelPipeline.addLast("invocator", new InvokerHandler());

		return channelPipeline;
	}

	public boolean isSupportingServers() {
		return serverBootstrap != null && serverChannelFactory != null;
	}

	public boolean isSupportingClients() {
		return clientBootstrap != null && clientChannelFactory != null;
	}

	public ThreadUsage threadUsage() {
		return threadUsage;
	}

	public Executor networkExecutor() {
		return networkExecutor;
	}

	public Executor methodInvocator() {
		return methodInvocator;
	}

	public ChannelGroup channels() {
		return channels;
	}

	public StaticRegistry staticReg() {
		return staticRegistry;
	}

	public boolean isDisposed() {
		return disposed.get();
	}

	public ConnectionManager dispose() {
		/*
		 * Only dispose once.
		 */
		if (!disposed.getAndSet(true)) {

			// Close all channels and wait uninterruptibly
			channels.close().awaitUninterruptibly();

			// Release resources
			ExecutorUtil.terminate(methodInvocator);

			if (isSupportingServers()) {
				serverBootstrap.releaseExternalResources();
			}
			if (isSupportingClients()) {
				clientBootstrap.releaseExternalResources();
			}
		}
		return this;
	}
}
