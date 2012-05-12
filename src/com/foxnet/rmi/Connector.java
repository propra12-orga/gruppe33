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
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

import com.foxnet.rmi.kernel.Dispatcher;

/**
 * @author Christopher Probst
 */
public final class Connector extends ConnectionManager {

	public Connector() {
		this(null, null);
	}

	public Connector(ThreadUsage threadUsage, MemoryUsage memoryUsage) {
		super(false, threadUsage, memoryUsage);
	}

	public Connection open(String host, int port) throws IOException {
		return open(new InetSocketAddress(host, port));
	}

	@Override
	public Connection open(SocketAddress socketAddress) throws IOException {
		// Connect to server
		ChannelFuture cf = ((ClientBootstrap) bootstrap).connect(socketAddress);

		// Add listener
		cf.addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				// If successful...
				if (future.isSuccess()) {
					// Add to group
					getChannels().add(future.getChannel());
				}
			}
		});

		// Wait for connection
		try {
			// Await the channel future
			cf.await();

			// If attempt failed...
			if (!cf.isSuccess()) {
				if (cf.isCancelled()) {
					throw new IOException("Connection attempt was "
							+ "cancelled.");
				} else {
					throw new IOException("Connection attempt failed. "
							+ "Reason: " + cf.getCause().getMessage(),
							cf.getCause());
				}
			}
		} catch (InterruptedException e) {
			// Set interrupted exception
			cf.setFailure(e);

			// Throw
			throw new IOException("Connection attempt was "
					+ "interrupted. Reason: " + e.getMessage(), e);
		}

		// The dispatcher is the rmi connection
		return cf.getChannel().getPipeline().get(Dispatcher.class);
	}
}
