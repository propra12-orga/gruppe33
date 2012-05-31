package com.foxnet.rmi.transport.network.handler.invocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

import com.foxnet.rmi.AsyncVoid;
import com.foxnet.rmi.Invocation;
import com.foxnet.rmi.Invoker;
import com.foxnet.rmi.InvokerManager;
import com.foxnet.rmi.binding.LocalBinding;
import com.foxnet.rmi.binding.LocalObject;
import com.foxnet.rmi.binding.RemoteBinding;
import com.foxnet.rmi.binding.RemoteObject;
import com.foxnet.rmi.binding.registry.Registry;
import com.foxnet.rmi.util.Future;

public class RmiRoutines {

	// One hour by default
	public static final long DEFAULT_INVOCATION_TIMEOUT_MILLIES = 1000 * 60 * 60;

	// Used to log infos, warnings or messages
	protected static final Logger STATIC_LOGGER = Logger
			.getLogger(Registry.class.getName());

	public static Object replaceRemoteObjectWithProxy(
			InvokerManager invokerFactory, Object remoteObject,
			long invocationTimeoutMillies) {

		if (!(remoteObject instanceof RemoteObject)) {
			return remoteObject;
		}

		// Create a new invoker
		Invoker newInvoker = invokerFactory.invoker(new RemoteBinding(
				(RemoteObject) remoteObject, true));

		newInvoker.proxyTimeout(invocationTimeoutMillies);

		return newInvoker.proxy();
	}

	public static Object replaceProxyWithLocalObject(InvokerManager owner,
			Object proxy) {

		// Try to get proxy invoker
		Invoker invoker = Invoker.getInvokerOf(proxy);

		// Obviously not a proxy which was created by us
		if (invoker == null || !owner.equals(invoker.manager())) {
			return proxy;
		} else {
			// Get the remote binding
			RemoteBinding remoteBinding = invoker.binding();

			// Create a new local object based on the proxy
			return new LocalObject(remoteBinding.id(),
					remoteBinding.isDynamic());
		}
	}

	public static Object remoteToLocal(InvokerManager invokerFactory,
			Object remoteArgument) {
		// Replace remote OR local object
		return replaceLocalObject(
				invokerFactory,
				replaceRemoteObjectWithProxy(invokerFactory, remoteArgument,
						DEFAULT_INVOCATION_TIMEOUT_MILLIES));
	}

	public static boolean isAsyncVoid(Invocation invocation) {
		// Annotation
		AsyncVoid av;

		// Get method...
		Method method = invocation.method();

		// Check for async void method
		return method.getReturnType() == void.class
				&& method.getExceptionTypes().length == 0
				&& ((av = method.getAnnotation(AsyncVoid.class)) != null && av
						.value());
	}

	public static Object localToRemote(InvokerManager owner,
			Object localArgument) {
		// Do we send back a remote OR a known proxy ?
		return owner.dynamically().replaceRemote(
				replaceProxyWithLocalObject(owner, localArgument));
	}

	public static Object replaceLocalObject(InvokerManager invokerFactory,
			Object localObject) {

		// Not for us...
		if (!(localObject instanceof LocalObject)) {
			return localObject;
		}

		// Convert
		LocalObject tmp = (LocalObject) localObject;

		// Lookup the target
		LocalBinding target;
		if (tmp.isDynamic()) {
			target = invokerFactory.dynamically().get(tmp.id());
		} else {
			target = invokerFactory.statically().get(tmp.id());
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

	public static void invoke(boolean dynamic, long bindingId,
			InvokerManager invokerFactory, Future future, int methodId,
			Object... args) throws IllegalStateException {
		invoke(dynamic, bindingId, null, invokerFactory, future, methodId, args);
	}

	public static void invoke(boolean dynamic, long bindingId,
			Executor executor, InvokerManager invokerFactory, Future future,
			int methodId, Object... args) throws IllegalStateException {

		// Helper var
		LocalBinding binding;
		if (dynamic) {
			binding = invokerFactory.dynamically().get(bindingId);
		} else {
			binding = invokerFactory.statically().get(bindingId);
		}

		// Check the binding
		if (binding == null) {
			// Create message
			String msg = "Requested binding with id (" + bindingId
					+ ") does not exist";

			// Log
			STATIC_LOGGER.warning(msg);

			// Fail if future exists...
			if (future != null) {

				// Fail
				future.fail(new IllegalArgumentException(msg));
			}
		} else {
			// Transfer the request to the binding
			invoke(executor, binding, invokerFactory, future, methodId, args);
		}
	}

	public static Runnable createInvocation(final LocalBinding localBinding,
			final InvokerManager invokerFactory, final Future future,
			int methodId, final Object... args) throws IllegalStateException {
		if (invokerFactory == null) {
			throw new NullPointerException("invokerFactory");
		}

		final Logger logger = Logger.getLogger("test");

		// Check if method id does not exist...
		if (!localBinding.containsMethodId(methodId)) {

			// Create message
			String msg = "Method id (" + methodId + ") does not exist";

			// Log
			logger.warning(msg);

			// Fail the future...
			if (future != null) {

				// Fail
				future.fail(new IllegalArgumentException(msg));
			}

			return null;
		} else {

			// Get final method from binding!
			final Method method = localBinding.methods().get(methodId);

			/*
			 * Check the return value and the future.
			 */
			if (method.getReturnType() != void.class && future == null) {
				throw new IllegalStateException("The method does return a "
						+ "non-void value but you have non specified a future");
			}

			// Create a new invocation runnable
			return new Runnable() {

				@Override
				public void run() {
					try {
						/*
						 * Resolve all remote and local objects.
						 */
						invokerFactory.remotesToLocals(args);

						// Invoke the method
						Object result = method.invoke(localBinding.target(),
								args);

						if (future != null) {
							// Succeed the future with the filtered result
							future.succeed(localToRemote(invokerFactory, result));
						}
					} catch (InvocationTargetException e) {
						// Short log...
						logger.warning("Declared throwable catched: "
								+ e.getCause());

						if (future != null) {
							// Fail
							future.fail(e);
						}
					} catch (Throwable e) {
						// Short log...
						logger.warning("Failed to invoke method. Reason: "
								+ e.getMessage());

						if (future != null) {
							// Fail
							future.fail(e);
						}
					}
				}
			};
		}
	}

	public static void invoke(Executor executor, LocalBinding localBinding,
			InvokerManager invokerFactory, Future future, int methodId,
			Object... args) throws IllegalStateException {

		/*
		 * Create a new invocation and execute it using the given method
		 * context.
		 */
		localBinding.executeInMethodContext(
				executor,
				methodId,
				createInvocation(localBinding, invokerFactory, future,
						methodId, args));
	}

}
