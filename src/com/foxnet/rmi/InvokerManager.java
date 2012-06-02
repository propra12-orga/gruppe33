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
import java.util.concurrent.Executor;

import com.foxnet.rmi.binding.LocalBinding;
import com.foxnet.rmi.binding.LocalObject;
import com.foxnet.rmi.binding.RemoteBinding;
import com.foxnet.rmi.binding.RemoteObject;
import com.foxnet.rmi.binding.registry.DynamicRegistry;
import com.foxnet.rmi.binding.registry.StaticRegistry;
import com.foxnet.rmi.util.Future;

/**
 * 
 * @author Christopher Probst
 * 
 */
public abstract class InvokerManager {

	/*
	 * The close future of this invoker manager.
	 */
	private final Future closeFuture = new Future();

	/*
	 * The dynamic registry of this invoker manager.
	 */
	private final DynamicRegistry dynamicRegistry = new DynamicRegistry();

	/*
	 * The static registry of this invoker manager.
	 */
	private final StaticRegistry staticRegistry;

	/*
	 * The dynamical proxy timeout.
	 */
	private volatile long dynamicalProxyTimeout;

	private Object replaceRemoteObjectWithProxy(Object remoteObject) {

		if (!(remoteObject instanceof RemoteObject)) {
			return remoteObject;
		}

		// Create a new invoker
		return invoker(new RemoteBinding((RemoteObject) remoteObject, true))
				.proxyTimeout(dynamicalProxyTimeout).proxy();
	}

	private Object replaceProxyWithLocalObject(Object proxy) {

		// Try to get proxy invoker
		Invoker invoker = Invoker.getInvokerOf(proxy);

		// Obviously not a proxy which was created by us
		if (invoker == null || !equals(invoker.manager())) {
			return proxy;
		} else {
			// Get the remote binding
			RemoteBinding remoteBinding = invoker.binding();

			// Create a new local object based on the proxy
			return new LocalObject(remoteBinding.id(),
					remoteBinding.isDynamic());
		}
	}

	private Object replaceLocalObject(Object localObject) {

		// Not for us...
		if (!(localObject instanceof LocalObject)) {
			return localObject;
		}

		// Convert
		LocalObject tmp = (LocalObject) localObject;

		// Lookup the target
		LocalBinding target;
		if (tmp.isDynamic()) {
			target = dynamical().get(tmp.id());
		} else {
			target = statical().get(tmp.id());
		}

		// Check the target
		if (target == null) {
			throw new IllegalArgumentException("The local argument object "
					+ "is not part of the given registries");
		} else {
			// Otherwise replace with real target
			return target.target();
		}
	}

	public InvokerManager(StaticRegistry staticRegistry) {
		if (staticRegistry == null) {
			throw new NullPointerException("staticRegistry");
		}

		this.staticRegistry = staticRegistry;
	}

	public Object remoteToLocal(Object remoteArgument) {
		// Replace remote OR local object
		return replaceLocalObject(replaceRemoteObjectWithProxy(remoteArgument));
	}

	public long dynamicalProxyTimeout() {
		return dynamicalProxyTimeout;
	}

	public InvokerManager dynamicalProxyTimeout(long dynamicalProxyTimeout) {
		this.dynamicalProxyTimeout = dynamicalProxyTimeout;
		return this;
	}

	public void handleInvocation(final InvocationMessage message,
			Executor executor, final Future future) {

		if (message == null) {
			throw new NullPointerException("message");
		} else if (executor == null) {
			throw new NullPointerException("executor");
		}

		// Get the correct binding
		final LocalBinding binding = message.isDynamic() ? dynamicRegistry
				.get(message.bindingId()) : staticRegistry.get(message
				.bindingId());

		// Check the binding
		if (binding == null) {
			// Fail if future exists...
			if (future != null) {

				// Fail
				future.fail(new IllegalArgumentException(
						"Requested binding with id (" + message.bindingId()
								+ ") does not exist"));
			}
		} else if (!binding.containsMethodId(message.methodId())) {
			// Fail the future...
			if (future != null) {

				// Fail
				future.fail(new IllegalArgumentException("Method id ("
						+ message.methodId() + ") does not exist"));
			}
		} else {

			// Get final method from binding!
			final Method method = binding.methods().get(message.methodId());

			/*
			 * Check the return value and the future.
			 */
			if (method.getReturnType() != void.class && future == null) {
				throw new IllegalStateException("The method does return a "
						+ "non-void value but you have non specified a future");
			}

			/*
			 * Create a new invocation and execute it using the given method
			 * context.
			 */
			binding.executeInMethodContext(executor, message.methodId(),

			new Runnable() {

				@Override
				public void run() {
					try {
						/*
						 * Resolve all remote and local objects and invoke
						 * method.
						 */
						Object result = method.invoke(binding.target(),
								remotesToLocals(message.arguments()));

						if (future != null) {
							// Succeed the future with the filtered result
							future.succeed(localToRemote(result));
						}
					} catch (Throwable e) {
						if (future != null) {
							// Fail
							future.fail(e);
						}
					}
				}
			});
		}
	}

	public Object localToRemote(Object localArgument) {
		// Do we send back a remote OR a known proxy ?
		return dynamical().replaceRemote(
				replaceProxyWithLocalObject(localArgument));
	}

	public Future closeFuture() {
		return closeFuture;
	}

	public DynamicRegistry dynamical() {
		return dynamicRegistry;
	}

	public StaticRegistry statical() {
		return staticRegistry;
	}

	public Object[] localsToRemotes(Object... localArguments) {
		if (localArguments != null) {
			for (int i = 0; i < localArguments.length; i++) {
				localArguments[i] = localToRemote(localArguments[i]);
			}
		}
		return localArguments;
	}

	public Object[] remotesToLocals(Object... remoteArguments) {
		if (remoteArguments != null) {
			for (int i = 0; i < remoteArguments.length; i++) {
				remoteArguments[i] = remoteToLocal(remoteArguments[i]);
			}
		}
		return remoteArguments;
	}

	public Object lookupProxy(String target) throws LookupException {
		return lookupInvoker(target).proxy();
	}

	public abstract Invoker invoker(RemoteBinding remoteBinding);

	public abstract Invoker lookupInvoker(String target) throws LookupException;

	public abstract String[] lookupNames() throws LookupException;

	public abstract Future close();
}
