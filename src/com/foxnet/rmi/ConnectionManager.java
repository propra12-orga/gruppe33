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
package com.foxnet.rmi;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.util.internal.ExecutorUtil;

import com.foxnet.rmi.impl.Dispatcher;

/**
 * @author Christopher Probst
 */
public abstract class ConnectionManager {

	// Cached network executor
	private final ExecutorService networkExecutor = Executors
			.newCachedThreadPool();

	// The channel factory which is used to create channels
	private final ChannelFactory channelFactory;

	// Used to invoke methods
	private final ExecutorService methodInvocator;

	// Used to limit channel memory localally
	private final int maxLocalChannelMemory;

	// Create a channel group to store connections
	private final ChannelGroup channels = new DefaultChannelGroup();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();

		// Shutdown manager if collected
		shudown();
	}

	public ConnectionManager(boolean server, int maxNetworkThreads,
			int methodInvocationThreads, int maxLocalChannelMemory) {

		// Create the channel factory
		channelFactory = server ? new NioServerSocketChannelFactory(
				networkExecutor, networkExecutor, maxNetworkThreads)
				: new NioClientSocketChannelFactory(networkExecutor,
						networkExecutor, maxNetworkThreads);

		// Create method invocator
		methodInvocator = Executors.newFixedThreadPool(methodInvocationThreads);

		// Save the max local channel memory
		this.maxLocalChannelMemory = maxLocalChannelMemory;

	}

	public abstract Object open(SocketAddress socketAddress) throws IOException;

	public int getMaxLocalChannelMemory() {
		return maxLocalChannelMemory;
	}

	public ExecutorService getMethodInvocator() {
		return methodInvocator;
	}

	public ExecutorService getNetworkExecutor() {
		return networkExecutor;
	}

	public ChannelGroup getChannels() {
		return channels;
	}

	public ChannelFactory getChannelFactory() {
		return channelFactory;
	}

	public Connection get(Channel channel) {
		if (channel == null) {
			throw new NullPointerException("channel");
		}

		// Return the dispatcher
		return channel.getPipeline().get(Dispatcher.class);
	}

	public void shudown() {
		// Close all channels and wait uninterruptibly
		getChannels().close().awaitUninterruptibly();

		// Terminate the remaining executors
		ExecutorUtil.terminate(methodInvocator);

		// Release resources
		getChannelFactory().releaseExternalResources();
	}
}
