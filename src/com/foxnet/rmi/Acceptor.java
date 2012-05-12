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

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChildChannelStateEvent;
import org.jboss.netty.channel.ServerChannel;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * @author Christopher Probst
 */
public final class Acceptor extends ConnectionManager {

	public Acceptor() {
		this(null, null);
	}

	public Acceptor(ThreadUsage threadUsage, MemoryUsage memoryUsage) {
		super(true, threadUsage, memoryUsage);

		// Used to add channels
		((ServerBootstrap) bootstrap)
				.setParentHandler(new SimpleChannelUpstreamHandler() {
					/*
					 * (non-Javadoc)
					 * 
					 * @see
					 * org.jboss.netty.channel.SimpleChannelUpstreamHandler#
					 * channelOpen
					 * (org.jboss.netty.channel.ChannelHandlerContext,
					 * org.jboss.netty.channel.ChannelStateEvent)
					 */
					@Override
					public void channelOpen(ChannelHandlerContext ctx,
							ChannelStateEvent e) throws Exception {
						// Add server channel!
						getChannels().add(e.getChannel());

						super.channelOpen(ctx, e);
					}

					/*
					 * (non-Javadoc)
					 * 
					 * @see
					 * org.jboss.netty.channel.SimpleChannelUpstreamHandler#
					 * childChannelOpen
					 * (org.jboss.netty.channel.ChannelHandlerContext,
					 * org.jboss.netty.channel.ChildChannelStateEvent)
					 */
					@Override
					public void childChannelOpen(ChannelHandlerContext ctx,
							ChildChannelStateEvent e) throws Exception {
						// Add client channel!
						getChannels().add(e.getChildChannel());

						super.childChannelOpen(ctx, e);
					}
				});
	}

	public ServerChannel open(int port) throws IOException {
		return open(new InetSocketAddress(port));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxnet.rmi.ConnectionManager#open(java.net.SocketAddress)
	 */
	@Override
	public ServerChannel open(SocketAddress socketAddress) throws IOException {
		return (ServerChannel) ((ServerBootstrap) bootstrap)
				.bind(socketAddress);
	}
}
