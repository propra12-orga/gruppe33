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
package com.foxnet.rmi.transport.network.handler.lookup;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.ChannelHandler.Sharable;

import com.foxnet.rmi.binding.RemoteObject;
import com.foxnet.rmi.binding.StaticBinding;
import com.foxnet.rmi.transport.network.ConnectionManager;
import com.foxnet.rmi.util.Request;

/**
 * 
 * @author Christopher Probst
 * 
 */
@Sharable
public final class LookupHandler extends SimpleChannelUpstreamHandler {

	public static Request newLookup(String target) {
		return new Request(new LookupMessage(target));
	}

	public static Request newLookupAll() {
		return new Request(new LookupMessage(null));
	}

	public static final LookupHandler INSTANCE = new LookupHandler();

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {

		ConnectionManager cm = ConnectionManager.of(ctx.getChannel());

		if (cm == null) {
			throw new IllegalStateException("This channel was not "
					+ "opened by a connection manager");
		}

		if (e.getMessage() instanceof Request) {
			Request request = (Request) e.getMessage();

			if (request.getData() instanceof LookupMessage) {

				LookupMessage lm = (LookupMessage) request.getData();

				if (lm.getTarget() != null) {

					StaticBinding sb = cm.getStaticRegistry().get(
							lm.getTarget());

					if (sb == null) {

						request.fail(new IllegalArgumentException(
								"Unknown target argument"));

					} else {

						request.succeed(new RemoteObject(sb));
					}
				} else {
					request.succeed(cm.getStaticRegistry().getNames());
				}

				return;
			}
		}

		super.messageReceived(ctx, e);
	}
}
