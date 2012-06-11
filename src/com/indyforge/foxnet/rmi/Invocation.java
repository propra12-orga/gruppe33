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
package com.indyforge.foxnet.rmi;

import java.lang.reflect.Method;

import com.indyforge.foxnet.rmi.util.Future;

/**
 * An invocation is basically a future with some further information.
 * 
 * @author Christopher Probst
 */
public final class Invocation extends Future {

	// The invoker of this invocation
	private final Invoker invoker;

	// The invocation message of this invocation
	private final InvocationMessage invocationMessage;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.foxnet.rmi.util.Future#modifyAttachment(java.lang.Object)
	 */
	@Override
	protected Object modifyAttachment(Object attachment) {
		return invoker.manager().remoteToLocal(attachment);
	}

	/**
	 * Creates a new invocation with the given arguments.
	 * 
	 * @param invoker
	 *            The invoker.
	 * @param methodId
	 *            The method id.
	 * @param arguments
	 *            The arguments.
	 */
	Invocation(Invoker invoker, int methodId, Object... arguments) {
		if (invoker == null) {
			throw new NullPointerException("invoker");
		}

		// Create the new invocation message
		invocationMessage = new InvocationMessage(
				invoker.binding().isDynamic(), invoker.binding().id(),
				methodId, arguments);

		// Save the invoker
		this.invoker = invoker;
	}

	/**
	 * @return true if and only if the invoked method is marked as asynchronous
	 *         and return void.
	 */
	public boolean isAsyncVoid() {
		// Annotation
		AsyncVoid av;

		// Get method...
		Method method = method();

		// Check for async void method
		return method.getReturnType() == void.class
				&& method.getExceptionTypes().length == 0
				&& ((av = method.getAnnotation(AsyncVoid.class)) != null && av
						.value());
	}

	/**
	 * @return the invoker.
	 */
	public Invoker invoker() {
		return invoker;
	}

	/**
	 * @return the method.
	 */
	public Method method() {
		return invoker.binding().methods().get(invocationMessage.methodId());
	}

	/**
	 * @return the method name.
	 */
	public String methodName() {
		return method().getName();
	}

	/**
	 * @return the message of this invocation which can be transferred.
	 */
	public InvocationMessage message() {
		return invocationMessage;
	}
}
