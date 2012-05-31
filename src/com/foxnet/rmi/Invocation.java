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
 * * Neither the name of the 'FoxNet Codec' nor the names of its 
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

import java.lang.reflect.Method;

import com.foxnet.rmi.util.Future;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class Invocation extends Future {

	// The invoker of this invocation
	private final Invoker invoker;

	// The method id of the invoked method
	private final int methodId;

	// The arguments of the invocation
	private final Object[] arguments;

	@Override
	protected Object modifyAttachment(Object attachment) {
		return invoker.remoteToLocal(attachment);
	}

	Invocation(Invoker invoker, int methodId, Object... arguments) {
		if (invoker == null) {
			throw new NullPointerException("invoker");
		}
		this.methodId = methodId;
		this.arguments = arguments;
		this.invoker = invoker;
	}

	public Invoker invoker() {
		return invoker;
	}

	public Method method() {
		return invoker.binding().methods().get(methodId);
	}

	public String methodName() {
		return method().getName();
	}

	public int methodId() {
		return methodId;
	}

	public Object[] arguments() {
		return arguments;
	}
}
