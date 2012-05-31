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

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.foxnet.rmi.binding.RemoteBinding;
import com.foxnet.rmi.binding.registry.DynamicRegistry;
import com.foxnet.rmi.binding.registry.StaticRegistry;

/**
 * 
 * @author Christopher Probst
 * 
 */
public abstract class Invoker extends InvokerManager implements
		InvocationHandler {

	public static Invoker getInvokerOf(Object proxy) {
		// Check to be a proxy class
		if (proxy == null || !Proxy.isProxyClass(proxy.getClass())) {
			return null;
		} else {
			// Try to get the handler
			InvocationHandler handler = Proxy.getInvocationHandler(proxy);

			// Conditional return...
			return handler instanceof Invoker ? (Invoker) handler : null;
		}
	}

	protected Invocation newInvocation(int methodId, Object... arguments) {
		return new Invocation(this, methodId, arguments);
	}

	// The invoker manager which created this invoker
	private final InvokerManager invokerManager;

	// The remote binding of this invoker
	private final RemoteBinding remoteBinding;

	// The proxy invocation timeout
	private volatile long proxyTimeout;

	// The lazy proxy object which is created when needed
	private volatile Object lazyProxy;

	protected Invoker(InvokerManager invokerFactory, RemoteBinding remoteBinding) {
		if (invokerFactory == null) {
			throw new NullPointerException("invokerFactory");
		} else if (remoteBinding == null) {
			throw new NullPointerException("remoteBinding");
		}
		this.invokerManager = invokerFactory;
		this.remoteBinding = remoteBinding;
	}

	public long proxyTimeout() {
		return proxyTimeout;
	}

	public Invoker proxyTimeout(long proxyTimeout) {
		this.proxyTimeout = proxyTimeout;
		return this;
	}

	public InvokerManager manager() {
		return invokerManager;
	}

	public RemoteBinding binding() {
		return remoteBinding;
	}

	@Override
	public Invoker invoker(RemoteBinding remoteBinding) {
		return invokerManager.invoker(remoteBinding);
	}

	@Override
	public StaticRegistry statically() {
		return invokerManager.statically();
	}

	@Override
	public DynamicRegistry dynamically() {
		return invokerManager.dynamically();
	}

	@Override
	public Invoker lookupInvoker(String target) throws IOException {
		return invokerManager.lookupInvoker(target);
	}

	@Override
	public Object localToRemote(Object argument) {
		return invokerManager.localToRemote(argument);
	}

	@Override
	public Object remoteToLocal(Object argument) {
		return invokerManager.remoteToLocal(argument);
	}

	public abstract Invocation invoke(int methodId, Object... args);

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		// Invoke the method remotely
		Invocation invocation = invoke(method, args);

		// Simply synchronize and return or throw
		if (invocation.synchronize(proxyTimeout)) {
			return invocation.attachment();
		} else {
			throw invocation.cause();
		}
	}

	public Invocation invoke(Method method, Object... args) {
		Integer methodId = remoteBinding.methodIds().get(method);
		if (methodId == null) {
			throw new IllegalArgumentException("Unknown method");
		}
		return invoke(methodId, args);
	}

	public Invocation invoke(String method, Object... args) {
		Integer methodId = remoteBinding.nameIds().get(method);
		if (methodId == null) {
			throw new IllegalArgumentException("Unknown method name");
		}
		return invoke(methodId, args);
	}

	public Object proxy() {
		Object tmpProxy = lazyProxy;
		if (tmpProxy == null) {
			synchronized (this) {
				tmpProxy = lazyProxy;
				if (tmpProxy == null) {
					lazyProxy = tmpProxy = Proxy.newProxyInstance(Thread
							.currentThread().getContextClassLoader(),
							remoteBinding.interfaces(), this);
				}
			}
		}

		return tmpProxy;
	}
}
