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
import java.util.concurrent.Executor;

import com.indyforge.foxnet.rmi.binding.LocalBinding;
import com.indyforge.foxnet.rmi.binding.LocalObject;
import com.indyforge.foxnet.rmi.binding.RemoteBinding;
import com.indyforge.foxnet.rmi.binding.RemoteObject;
import com.indyforge.foxnet.rmi.binding.registry.DynamicRegistry;
import com.indyforge.foxnet.rmi.binding.registry.StaticRegistry;
import com.indyforge.foxnet.rmi.util.Future;

/**
 * An invoker manager represents a connection. It can lookup invokers and
 * manages bindings.
 * 
 * @author Christopher Probst
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
	 * The dynamic proxy timeout.
	 */
	private volatile long dynamicProxyTimeout;

	/**
	 * Replaces the given remote object with a proxy if necessary.
	 * 
	 * @param remoteObject
	 *            The remote object you want to check.
	 * @return the given object or a proxy.
	 */
	private Object replaceRemoteObjectWithProxy(Object remoteObject) {

		if (!(remoteObject instanceof RemoteObject)) {
			return remoteObject;
		}

		// Create a new invoker
		return new Invoker(this, new RemoteBinding((RemoteObject) remoteObject,
				true)).proxyTimeout(dynamicProxyTimeout).proxy();
	}

	/**
	 * Replaces the given proxy with a local object. The remote side will know
	 * which "real"-object belongs to the given local object.
	 * 
	 * @param proxy
	 *            The proxy you want to check.
	 * @return the given object or a local object.
	 */
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

	/**
	 * Replaces the given local object with the "real"-object.
	 * 
	 * @param localObject
	 *            The local object you want to check.
	 * @return the given object or the "real"-object.
	 */
	private Object replaceLocalObject(Object localObject) {

		// Not for us...
		if (!(localObject instanceof LocalObject)) {
			return localObject;
		}

		// Convert
		LocalObject local = (LocalObject) localObject;

		// Lookup the binding
		LocalBinding binding = local.isDynamic() ? dynamicRegistry.get(local
				.id()) : staticRegistry.get(local.id());

		// Check the binding
		if (binding == null) {
			throw new IllegalArgumentException("The local argument object "
					+ "is not part of the given registries");
		} else {
			// Otherwise replace with real target
			return binding.target();
		}
	}

	/**
	 * Sends the invocation to the remote side.
	 * 
	 * @param invocation
	 *            The invocation you want to send.
	 */
	protected abstract void sendInvocation(Invocation invocation);

	/**
	 * Creates a new invoker manager using the given argument.
	 * 
	 * @param staticRegistry
	 *            The static registry of this invoker manager.
	 */
	public InvokerManager(StaticRegistry staticRegistry) {
		if (staticRegistry == null) {
			throw new NullPointerException("staticRegistry");
		}

		this.staticRegistry = staticRegistry;
	}

	/**
	 * Converts a remote argument to a local argument.
	 * 
	 * @param remoteArgument
	 *            The remote argument.
	 * @return the local argument.
	 */
	public Object remoteToLocal(Object remoteArgument) {
		// Replace remote OR local object
		return replaceLocalObject(replaceRemoteObjectWithProxy(remoteArgument));
	}

	/**
	 * Converts a local argument to a remote argument.
	 * 
	 * @param localArgument
	 *            The local argument.
	 * @return the remote argument.
	 */
	public Object localToRemote(Object localArgument) {
		// Do we send back a remote OR a known proxy ?
		return dynamicRegistry
				.replaceRemote(replaceProxyWithLocalObject(localArgument));
	}

	/**
	 * @see InvokerManager#localToRemote(Object)
	 */
	public Object[] localsToRemotes(Object... localArguments) {
		if (localArguments != null) {
			for (int i = 0; i < localArguments.length; i++) {
				localArguments[i] = localToRemote(localArguments[i]);
			}
		}
		return localArguments;
	}

	/**
	 * @see InvokerManager#remoteToLocal(Object)
	 */
	public Object[] remotesToLocals(Object... remoteArguments) {
		if (remoteArguments != null) {
			for (int i = 0; i < remoteArguments.length; i++) {
				remoteArguments[i] = remoteToLocal(remoteArguments[i]);
			}
		}
		return remoteArguments;
	}

	/**
	 * @return the dynamic proxy timeout which is used when creating dynamic
	 *         proxy objects.
	 */
	public long dynamicProxyTimeout() {
		return dynamicProxyTimeout;
	}

	/**
	 * Sets the dynamic proxy timeout which is used when creating dynamic proxy
	 * objects.
	 * 
	 * @param dynamicProxyTimeout
	 *            The new dynamic proxy timeout. A value <= 0 means waiting
	 *            without a limit.
	 * @return this for chaining.
	 */
	public InvokerManager dynamicProxyTimeout(long dynamicProxyTimeout) {
		this.dynamicProxyTimeout = dynamicProxyTimeout;
		return this;
	}

	/**
	 * This method will handle the invocation request using the given executor.
	 * If the invocation is finished the future will be notified (If the future
	 * was not null).
	 * 
	 * @param message
	 *            The invocation message.
	 * @param executor
	 *            The executor.
	 * @param future
	 *            The future or null.
	 */
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
								 * Resolve all remote and local objects and
								 * invoke method.
								 */
								Object result = method.invoke(binding.target(),
										remotesToLocals(message.arguments()));

								if (future != null) {
									// Succeed the future with the filtered
									// result
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

	/**
	 * @return the close future which is notifies when the invoker manager is
	 *         closed.
	 */
	public Future closeFuture() {
		return closeFuture;
	}

	/**
	 * @return the dynamic registry.
	 */
	public DynamicRegistry dynamicReg() {
		return dynamicRegistry;
	}

	/**
	 * @return the static registry.
	 */
	public StaticRegistry staticReg() {
		return staticRegistry;
	}

	/**
	 * Gets the proxy with the given name.
	 * 
	 * @param name
	 *            The name of the target you want to lookup.
	 * @return the proxy.
	 * @throws LookupException
	 *             If the lookup failed.
	 */
	public Object lookupProxy(String name) throws LookupException {
		return lookupInvoker(name).proxy();
	}

	/**
	 * Gets the invoker with the given name.
	 * 
	 * @param name
	 *            The name of the target you want to lookup.
	 * @return the invoker.
	 * @throws LookupException
	 *             If the lookup failed.
	 */
	public abstract Invoker lookupInvoker(String name) throws LookupException;

	/**
	 * Gets the names of all remote bindings.
	 * 
	 * @return an array which contains the names.
	 * @throws LookupException
	 *             If the lookup failed.
	 */
	public abstract String[] lookupNames() throws LookupException;

	/**
	 * Closes this invoker manager.
	 * 
	 * @return the close future.
	 */
	public abstract Future close();
}
