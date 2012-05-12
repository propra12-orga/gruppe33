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
package com.foxnet.rmi.kernel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.SocketChannelConfig;

import com.foxnet.rmi.AsyncVoid;
import com.foxnet.rmi.Connection;
import com.foxnet.rmi.ConnectionManager;
import com.foxnet.rmi.Future;
import com.foxnet.rmi.FutureCallback;
import com.foxnet.rmi.Invocation;
import com.foxnet.rmi.Invoker;
import com.foxnet.rmi.binding.LocalBinding;
import com.foxnet.rmi.binding.LocalBinding.OrderedExecutionQueue;
import com.foxnet.rmi.binding.RemoteBinding;
import com.foxnet.rmi.binding.RemoteObject;
import com.foxnet.rmi.binding.StaticBinding;
import com.foxnet.rmi.registry.DynamicRegistry;
import com.foxnet.rmi.registry.StaticRegistry;
import com.foxnet.rmi.util.ChannelMemoryLimiter;
import com.foxnet.rmi.util.ChannelMemoryLimiter.ChannelMemory;
import com.foxnet.rmi.util.Request;
import com.foxnet.rmi.util.RequestManager;
import com.foxnet.rmi.util.WrapperInputStream;
import com.foxnet.rmi.util.WrapperOutputStream;

/**
 * @author Christopher Probst
 */
public final class Dispatcher extends SimpleChannelHandler implements
		Connection {

	/*
	 * Request carrier flag: 0 - 126
	 */
	private static final int REQUEST = 0x0;

	/*
	 * Message carrier flag: 127 - 253
	 */
	private static final int MESSAGE = 0x7F;

	/*
	 * 254
	 */
	private static final int REQUEST_SUCCEEDED = 0xFE;

	/*
	 * 255
	 */
	private static final int REQUEST_FAILED = 0xFF;

	/*
	 * 0
	 */
	private static final int DYNAMIC_INVOCATION = 0x0;

	/*
	 * 1
	 */
	private static final int STATIC_INVOCATION = 0x1;

	/*
	 * 2
	 */
	private static final int LOOKUP = 0x2;

	/*
	 * 3
	 */
	private static final int LIST = 0x3;

	private static final PooledOutputStreams POOLED_OUTPUT_STREAMS = new PooledOutputStreams();

	/**
	 * The timeout of an invocation. -1 means no timeout.
	 */
	public static final long INVOCATION_TIMEOUT = 1;

	/**
	 * The time unit of the invocation timeout.
	 */
	public static final TimeUnit INVOCATION_TIME_UNIT = TimeUnit.HOURS;

	/**
	 * The timeout of a lookup request. -1 means no timeout.
	 */
	public static final long LOOKUP_REQUEST_TIMEOUT = 1;

	/**
	 * The time unit of the lookup request timeout.
	 */
	public static final TimeUnit LOOKUP_REQUEST_TIME_UNIT = TimeUnit.MINUTES;

	/**
	 * The timeout of a list request. -1 means no timeout.
	 */
	public static final long LIST_REQUEST_TIMEOUT = 1;

	/**
	 * The time unit of the list request timeout.
	 */
	public static final TimeUnit LIST_REQUEST_TIME_UNIT = TimeUnit.MINUTES;

	/**
	 * The socket send buffer capacity.
	 */
	public static final int SOCKET_SEND_BUFFER_CAPACITY = 0xFFFF;

	/**
	 * The socket receive buffer capacity.
	 */
	public static final int SOCKET_RECV_BUFFER_CAPACITY = 0xFFFF;

	/**
	 * The initial capacity of the input buffer.
	 */
	public static final int INITIAL_INPUT_BUFFER_CAPACITY = 0xFFFF;

	/**
	 * The initial capacity of the output buffer.
	 */
	public static final int INITIAL_OUTPUT_BUFFER_CAPACITY = 0xFFFF;

	/*
	 * The default invoker implementation which invokes the given method
	 * asynchronously.
	 */
	private final class DefaultInvoker implements Invoker {

		private final String target;
		private final RemoteBinding remoteBinding;
		private final Map<String, Method> stringMethods;

		public DefaultInvoker(String target, RemoteObject remoteObject,
				boolean dynamic) {
			this.target = target;

			// Create new remote binding
			remoteBinding = new RemoteBinding(remoteObject.getId(),
					remoteObject.getInterfaces(), dynamic);

			// Create string methods
			Map<String, Method> tmpStringMethods = new HashMap<String, Method>();
			for (Method method : remoteBinding) {
				tmpStringMethods.put(method.getName(), method);
			}

			// Save unmodifiable map
			stringMethods = Collections.unmodifiableMap(tmpStringMethods);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invoker#getMethod(java.lang.String)
		 */
		@Override
		public Method getMethod(String methodName) {
			return stringMethods.get(methodName);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invoker#getConnection()
		 */
		@Override
		public Connection getConnection() {
			return Dispatcher.this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invoker#getTarget()
		 */
		@Override
		public String getTarget() {
			return target;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invoker#getRemoteBinding()
		 */
		@Override
		public RemoteBinding getRemoteBinding() {
			return remoteBinding;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invoker#invoke(java.lang.String,
		 * java.lang.Object[])
		 */
		@Override
		public Invocation invoke(String methodName, Object... args) {
			return invokeAsynchronously(this, methodName, args);
		}
	}

	/*
	 * The proxy handler implementation which redirects the proxy invocations to
	 * the remote and waits for the result if necessary.
	 */
	private final class ProxyHandler implements InvocationHandler {

		/*
		 * The remote binding of this handler.
		 */
		private final RemoteBinding remoteBinding;

		public ProxyHandler(RemoteObject remoteObject, boolean dynamic) {
			if (remoteObject == null) {
				throw new NullPointerException("remoteObject");
			}

			// Create new remote binding
			remoteBinding = new RemoteBinding(remoteObject.getId(),
					remoteObject.getInterfaces(), dynamic);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
		 * java.lang.reflect.Method, java.lang.Object[])
		 */
		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {

			// Invoke and wait
			return invokeSynchronously(remoteBinding, method, args);
		}
	}

	// Used to log
	protected final Logger logger = Logger.getLogger(getClass().getName());

	// The channel of this dispatcher
	private volatile Channel channel;

	// Used to store packets before they are read
	private ChannelBuffer remainingPackets;

	// The manager which manages this dispatcher
	private final ConnectionManager manager;

	// Used to limit the memory globally
	private final ChannelMemoryLimiter globalChannelMemoryLimiter;

	// Used to limit the memory locally
	private final ChannelMemoryLimiter localChannelMemoryLimiter;

	// Used to store and identify requests
	private final RequestManager requestManager = new RequestManager();

	// Used to manage dynamic remote objects
	private final DynamicRegistry dynamicRegistry = new DynamicRegistry();

	// Used to manage static remote objects if necessary
	private final StaticRegistry staticRegistry;

	// Used to suspend & resume this dispatcher
	private volatile ChannelMemory channelMemory;

	private final WrapperInputStream<ChannelBufferInputStream> wrappedInput = new WrapperInputStream<ChannelBufferInputStream>();

	// Used to read data
	private final ObjectInputStream input = new ObjectInputStream(wrappedInput) {
		protected void readStreamHeader() throws IOException,
				StreamCorruptedException {
			// Ignore header...
		}
	};

	private final WrapperOutputStream<ChannelBufferOutputStream> wrappedOutput = new WrapperOutputStream<ChannelBufferOutputStream>(
			POOLED_OUTPUT_STREAMS
					.acquireOutputStream(INITIAL_OUTPUT_BUFFER_CAPACITY));

	// Used to write data (Acquire the output stream directly)
	private final ObjectOutputStream output = new ObjectOutputStream(
			wrappedOutput) {
		protected void writeStreamHeader() throws IOException {
			// Ignore header...
		}
	};

	private final Lock outputLock = new ReentrantLock();
	private final Condition writable = outputLock.newCondition();

	private Request lookupAndWait(String target) throws IOException {
		// Add a new request
		Request request = requestManager.addRequest(target);

		try {
			outputLock.lock();

			// At first write request infos
			output.writeByte(REQUEST + LOOKUP);
			output.writeLong(request.getId());
			output.writeObject(target);

			// Always flush when ready!
			flushAndWrite();
		} catch (Exception e) {
			// Set cause
			request.failed(e);
		} finally {
			outputLock.unlock();
		}

		// Wait for response
		if (request.await(LOOKUP_REQUEST_TIMEOUT, LOOKUP_REQUEST_TIME_UNIT)) {
			// Return the request
			return request;
		} else {
			throw new IOException("Lookup failed. Reason: "
					+ request.getCause().getMessage(), request.getCause());
		}
	}

	private Invocation invokeAsynchronously(Invoker invoker, String methodName,
			Object... args) {
		// Get method
		Method method = invoker.getMethod(methodName);

		// If method name is wrong...
		if (method == null) {
			// Create and return failed invocation
			Invocation invocation = new Invocation(invoker, methodName, args);
			invocation.failed(new IllegalArgumentException("Method with "
					+ "name \"" + methodName + "\" does " + "not exist."));
			return invocation;
		}

		/*
		 * At first replace all objects which implement the Remote interface
		 * with instances of RemoteObject so the receiver can easily create
		 * appropriate proxy classes.
		 */
		dynamicRegistry.replaceRemoteObjects(args);

		// Annotation
		AsyncVoid av;

		// Get the remote binding
		RemoteBinding remoteBinding = invoker.getRemoteBinding();

		// Check for async void method
		if (method.getReturnType() == void.class
				&& method.getExceptionTypes().length == 0
				&& ((av = method.getAnnotation(AsyncVoid.class)) != null && av
						.value())) {

			/*
			 * Now send the request asynchronously to the remote side.
			 */
			try {
				invokeVoidMethod(remoteBinding.isDynamic(),
						remoteBinding.getId(),
						remoteBinding.getMethodId(method), args);
			} catch (Exception e) {
				// Create and return failed invocation
				Invocation invocation = new Invocation(invoker, methodName,
						args);
				invocation.failed(e);
				return invocation;
			}

			// Return a new completed invocation
			Invocation invocation = new Invocation(invoker, methodName, args);
			invocation.completed(null);
			return invocation;
		} else {
			// Create a default invocation
			final Invocation invocation = new Invocation(invoker, methodName,
					args);

			/*
			 * Now send the request synchronously to the remote side.
			 */
			Request request;
			try {
				request = invokeMethod(remoteBinding.isDynamic(),
						remoteBinding.getId(),
						remoteBinding.getMethodId(method), args);
			} catch (Exception e) {
				invocation.failed(e);
				return invocation;
			}

			// Wait for response asynchronously
			request.add(new FutureCallback() {

				@Override
				public void completed(final Future future) throws Exception {
					// Execute the notification in an executor
					getMethodInvocator().execute(new Runnable() {

						@Override
						public void run() {
							if (future.isSuccessful()) {
								invocation.completed(future.getAttachment());
							} else {
								invocation.failed(future.getCause());
							}
						}
					});
				}
			});

			return invocation;
		}
	}

	private Object invokeSynchronously(RemoteBinding remoteBinding,
			Method method, Object[] args) throws Throwable {

		/*
		 * At first replace all objects which implement the Remote interface
		 * with instances of RemoteObject so the receiver can easily create
		 * appropriate proxy classes.
		 */
		dynamicRegistry.replaceRemoteObjects(args);

		// Annotation
		AsyncVoid av;

		// Check for async void method
		if (method.getReturnType() == void.class
				&& method.getExceptionTypes().length == 0
				&& ((av = method.getAnnotation(AsyncVoid.class)) != null && av
						.value())) {

			/*
			 * Now send the request asynchronously to the remote side.
			 */
			invokeVoidMethod(remoteBinding.isDynamic(), remoteBinding.getId(),
					remoteBinding.getMethodId(method), args);

			return null;
		} else {
			/*
			 * Now send the request synchronously to the remote side.
			 */
			Request request = invokeMethod(remoteBinding.isDynamic(),
					remoteBinding.getId(), remoteBinding.getMethodId(method),
					args);

			/*
			 * Now await that the request completes.
			 */
			if (request.await(INVOCATION_TIMEOUT, INVOCATION_TIME_UNIT)) {

				/*
				 * Replace reference and/or return response
				 */
				return replaceReference(request.getAttachment());
			} else {

				/*
				 * Throw the exception which was caught on the remote side.
				 */
				throw request.getCause();
			}
		}
	}

	private Channel verifyChannel() {
		// Get channel
		Channel tmpChannel = channel;

		if (tmpChannel == null) {
			throw new IllegalStateException("The internal channel "
					+ "does not exist yet.");
		}

		return tmpChannel;
	}

	private void awaitWritable() throws InterruptedException {
		try {
			outputLock.lock();

			// Await the writable-condition uninterruptibly
			writable.awaitUninterruptibly();
		} finally {
			outputLock.unlock();
		}
	}

	private void signalAllWritable() {
		try {
			outputLock.lock();
			writable.signalAll();
		} finally {
			outputLock.unlock();
		}
	}

	private void offerPackets(ChannelBuffer newPackets) throws Exception {
		// If there is nothing to read just return
		if (!newPackets.readable() && !remainingPackets.readable()) {
			return;
		}

		// If there are remaining bytes we have to process them first
		if (remainingPackets.readable()) {

			// Discard the read bytes
			remainingPackets.discardReadBytes();

			// Write bytes
			remainingPackets.writeBytes(newPackets);

			// Get and process packets
			processPackets(remainingPackets);
		} else {
			// Just process the received packet
			processPackets(newPackets);

			// If there are remaining new packets just put into buffer
			if (newPackets.readable()) {

				// Write bytes
				remainingPackets.writeBytes(newPackets);
			}
		}
	}

	private void processPackets(ChannelBuffer packets) throws Exception {

		// While there are at least 4 bytes to read
		while (packets.readableBytes() > 3) {

			// Mark here
			packets.markReaderIndex();

			// Read an int
			int packetSize = packets.readInt();

			// If there are not enough bytes
			if (packets.readableBytes() < packetSize) {

				// Reset
				packets.resetReaderIndex();

				// Wait for more
				return;
			}

			// Get reader index
			int readerIndex = packets.readerIndex();

			// Slice the packets
			ChannelBuffer sliced = packets.slice(readerIndex, packetSize);

			// Increase
			packets.readerIndex(readerIndex + packetSize);

			// Process the sliced packet
			processPacket(sliced);
		}
	}

	private void processPacket(ChannelBuffer cb) throws Exception {
		// The readable amount
		int frameSize = cb.readableBytes();

		/*
		 * If the buffer is valid we try to process the input.
		 */
		try {
			// Set source
			wrappedInput.setInput(new ChannelBufferInputStream(cb, frameSize));

			// Read byte!
			int type = input.readUnsignedByte();

			switch (type) {
			case REQUEST_FAILED:
				requestManager.getRequest(input.readLong()).failed(
						(Throwable) input.readObject());
				break;
			case REQUEST_SUCCEEDED:
				requestManager.getRequest(input.readLong()).completed(
						input.readObject());
				break;
			default:
				Long requestId;

				if (type < MESSAGE) {
					requestId = input.readLong();
				} else {
					requestId = null;
					type -= MESSAGE;
				}

				switch (type) {
				case DYNAMIC_INVOCATION:
				case STATIC_INVOCATION:
					handleInvocation(type, requestId, frameSize);
					break;
				case LIST:
					handleList(requestId);
					break;
				case LOOKUP:
					handleLookup(requestId);
					break;
				}
				break;
			}
		} catch (Exception e) {
			logger.severe("Failed to decode the input. "
					+ "The connection will be closed now. Reason: "
					+ e.getMessage());

			// Try to close
			close();
		} finally {
			// Help GC!
			wrappedInput.setInput(null);
		}
	}

	private void acquireMemory(int size) {
		// Acquire local memory
		localChannelMemoryLimiter.acquire(channelMemory, size);

		// Acquire global memory if necessary
		if (globalChannelMemoryLimiter != null) {
			globalChannelMemoryLimiter.acquire(channelMemory, size);
		}
	}

	private void releaseMemory(int size) {
		// Finally release the local memory
		localChannelMemoryLimiter.release(size);

		// Release global memory if necessary
		if (globalChannelMemoryLimiter != null) {
			globalChannelMemoryLimiter.release(size);
		}
	}

	private void handleInvocation(int type, final Long requestId,
			final int messageSize) throws Exception {

		long id = input.readLong();
		int methodId = input.readUnsignedShort();
		final Object[] args = (Object[]) input.readObject();
		boolean dynamic = type == DYNAMIC_INVOCATION;

		// Resolve the arguments
		replaceReferences(args);

		// Helper var
		LocalBinding binding;
		if (dynamic) {
			binding = dynamicRegistry.get(id);
		} else {
			binding = getStaticRegistry().get(id);
		}

		// Check the binding
		if (binding == null) {
			// Send cause
			if (requestId != null) {

				// Create message
				String msg = "Requested binding with id (" + id
						+ ") does not exist.";

				// Log
				logger.warning(msg);

				// Send cause
				throwCause(requestId, new IllegalArgumentException(msg));
			}

			// Stop here!
			return;
		}

		// Check if method id does not exist...
		if (!binding.containsMethodId(methodId)) {
			// Send cause
			if (requestId != null) {

				// Create message
				String msg = "Method id (" + methodId + ") does not exist";

				// Log
				logger.warning(msg);

				// Send cause
				throwCause(requestId, new IllegalArgumentException(msg));
			}

			// Stop here!
			return;
		}

		// Get final method from binding!
		final Method method = binding.getMethod(methodId);

		// Get final target from binding
		final Object target = binding.getTarget();

		// Create a new invocation runnable
		Runnable invocation = new Runnable() {

			@Override
			public void run() {
				try {
					try {
						// Invoke method!
						Object res = method.invoke(target, args);

						if (requestId != null) {
							// Send value
							returnValue(requestId,
									dynamicRegistry.replaceRemoteObject(res));
						}
					} catch (InvocationTargetException e) {
						// Short log...
						logger.warning("Declared throwable catched: "
								+ e.getCause());

						if (requestId != null) {
							// Send cause
							throwCause(requestId, e.getCause());
						}
					} catch (Throwable e) {
						// Short log...
						logger.warning("Failed to invoke method. Reason: "
								+ e.getMessage());

						if (requestId != null) {
							// Send cause
							throwCause(requestId, e);
						}
					}
				} catch (Exception e) {
					logger.severe("Failed to execute throwCause(...). "
							+ "The connection will be closed now. "
							+ "Reason: " + e.getMessage());

					// Close the channel
					close();
				} finally {
					// Finally release the memory
					releaseMemory(messageSize);
				}
			}
		};

		// Acquire memory
		acquireMemory(messageSize);

		// Try to get the queue
		OrderedExecutionQueue queue = binding.getQueue(methodId);

		/*
		 * Now execute the invocation. Either directly or the ordered execution
		 * queue of the method if necessary.
		 */
		if (queue != null) {

			// Execute queue if necessary
			if (queue.addOrderedExecution(invocation)) {

				// Execute the queue now
				getMethodInvocator().execute(queue);
			}
		} else {
			// Execute the invocation now
			getMethodInvocator().execute(invocation);
		}
	}

	private void handleLookup(long requestId) throws Exception {
		try {
			String name;

			// Get name binding
			StaticBinding sb = getStaticRegistry().get(
					name = (String) input.readObject());

			if (sb == null) {
				// Create warning text
				String warning = "Requested binding with name (" + name
						+ ") does not exist.";

				// Log...
				logger.warning(warning);

				// Throw new exception
				throw new Exception(warning);
			}

			// Send response
			try {
				outputLock.lock();
				// At first write request infos
				output.writeByte(REQUEST_SUCCEEDED);
				output.writeLong(requestId);

				// Write invocation infos
				output.writeObject(new RemoteObject(sb));

				// Always flush when ready!
				flushAndWrite();
			} finally {
				outputLock.unlock();
			}
		} catch (Throwable t) {
			try {
				outputLock.lock();
				// At first write request infos
				output.writeByte(REQUEST_FAILED);
				output.writeLong(requestId);

				// Write invocation infos
				output.writeObject(t);

				// Always flush when ready!
				flushAndWrite();
			} finally {
				outputLock.unlock();
			}
		}
	}

	private void handleList(long requestId) throws Exception {
		// Send response
		try {
			outputLock.lock();
			// At first write request infos
			output.writeByte(REQUEST_SUCCEEDED);
			output.writeLong(requestId);

			// Write invocation infos
			output.writeObject(getStaticRegistry().getNames());

			// Always flush when ready!
			flushAndWrite();
		} finally {
			outputLock.unlock();
		}
	}

	private void replaceReferences(Object[] arguments) {
		if (arguments != null) {
			for (int i = 0; i < arguments.length; i++) {
				// Get the reference
				arguments[i] = replaceReference(arguments[i]);
			}
		}
	}

	private Object replaceReference(Object object) {
		if (!(object instanceof RemoteObject)) {
			return object;
		}

		return createProxy((RemoteObject) object, true);
	}

	private Object createProxy(RemoteObject remoteObject, boolean dynamic) {
		return Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(),
				remoteObject.getInterfaces(), new ProxyHandler(remoteObject,
						dynamic));
	}

	private void invokeVoidMethod(boolean dynamic, long id, int methodId,
			Object[] args) throws Exception {

		try {
			outputLock.lock();
			// At first write request infos
			output.writeByte(MESSAGE
					+ (dynamic ? DYNAMIC_INVOCATION : STATIC_INVOCATION));

			// Write invocation infos
			output.writeLong(id);
			output.writeShort(methodId);
			output.writeObject(args);

			// Always flush when ready!
			flushAndWrite();
		} finally {
			outputLock.unlock();
		}
	}

	private Request invokeMethod(boolean dynamic, long id, int methodId,
			Object[] args) throws Exception {

		// Add a new request
		Request request = requestManager.addRequest();

		try {
			outputLock.lock();
			// At first write request infos
			output.writeByte(REQUEST
					+ (dynamic ? DYNAMIC_INVOCATION : STATIC_INVOCATION));
			output.writeLong(request.getId());

			// Write invocation infos
			output.writeLong(id);
			output.writeShort(methodId);
			output.writeObject(args);

			// Always flush when ready!
			flushAndWrite();
		} catch (Exception e) {
			// Set cause
			request.failed(e);
		} finally {
			outputLock.unlock();
		}

		return request;
	}

	private void throwCause(long requestId, Throwable cause) throws Exception {
		try {
			outputLock.lock();

			// At first write request infos
			output.writeByte(REQUEST_FAILED);
			output.writeLong(requestId);

			// Write invocation infos
			output.writeObject(cause);

			// Always flush when ready!
			flushAndWrite();
		} finally {
			outputLock.unlock();
		}
	}

	private void returnValue(long requestId, Object value) throws Exception {
		try {
			outputLock.lock();

			// At first write request infos
			output.writeByte(REQUEST_SUCCEEDED);
			output.writeLong(requestId);

			// Write invocation infos
			output.writeObject(value);

			// Always flush when ready!
			flushAndWrite();
		} finally {
			outputLock.unlock();
		}
	}

	volatile int k = 0;

	/*
	 * Must be called inside the output lock.
	 */
	private void flushAndWrite() throws IOException {
		// Flush the internal buffer
		output.flush();

		// Get and verify
		Channel tmpChannel = verifyChannel();

		// Check if still open
		if (!tmpChannel.isOpen()) {
			throw new IOException("Connection is already closed.");
		}

		// Get and set output stream
		final ChannelBufferOutputStream cbos = wrappedOutput
				.setOutput(POOLED_OUTPUT_STREAMS
						.acquireOutputStream(INITIAL_OUTPUT_BUFFER_CAPACITY));

		// Write length
		cbos.buffer().setInt(0, cbos.buffer().readableBytes() - 4);

		// Write a wrapped buffer
		tmpChannel.write(cbos.buffer()).addListener(
				new ChannelFutureListener() {

					@Override
					public void operationComplete(ChannelFuture future)
							throws Exception {
						// Release the output stream
						POOLED_OUTPUT_STREAMS.releaseOutputStream(cbos);
					}
				});
	}

	public Dispatcher(ConnectionManager manager) throws IOException {
		this(manager, null, null);
	}

	public Dispatcher(ConnectionManager manager,
			ChannelMemoryLimiter globalChannelMemoryLimiter,
			StaticRegistry staticRegistry) throws IOException {

		// Save the manager
		this.manager = manager;

		// Save the global channel memory limiter
		this.globalChannelMemoryLimiter = globalChannelMemoryLimiter;

		// Used to limit the channel memory usage locally
		localChannelMemoryLimiter = new ChannelMemoryLimiter(
				manager.getMemoryUsage().localMemory);

		// Save static registry
		this.staticRegistry = staticRegistry;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jboss.netty.channel.SimpleChannelHandler#exceptionCaught(org.jboss
	 * .netty.channel.ChannelHandlerContext,
	 * org.jboss.netty.channel.ExceptionEvent)
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {

		// Log
		logger.severe("Network exception caught. Connection will be "
				+ "closed now. Reason: " + e.getCause().getMessage());

		// Close connection
		close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jboss.netty.channel.SimpleChannelUpstreamHandler#channelInterestChanged
	 * (org.jboss.netty.channel.ChannelHandlerContext,
	 * org.jboss.netty.channel.ChannelStateEvent)
	 */
	@Override
	public void channelInterestChanged(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {

		// Convert
		int ops = (Integer) e.getValue();

		// Signal if writable
		if ((ops & Channel.OP_WRITE) == 0) {
			signalAllWritable();
		}

		// Super
		super.channelInterestChanged(ctx, e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jboss.netty.channel.SimpleChannelHandler#messageReceived(org.jboss
	 * .netty.channel.ChannelHandlerContext,
	 * org.jboss.netty.channel.MessageEvent)
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {

		// Offer new packets if necessary
		offerPackets(e.getMessage() instanceof ChannelBuffer ? (ChannelBuffer) e
				.getMessage() : ChannelBuffers.EMPTY_BUFFER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jboss.netty.handler.codec.frame.FrameDecoder#channelClosed(org.jboss
	 * .netty.channel.ChannelHandlerContext,
	 * org.jboss.netty.channel.ChannelStateEvent)
	 */
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {

		// Signal all writable
		signalAllWritable();

		// Shutdown the remaining requests
		requestManager.setCauseToAll(new IOException(
				"Connection is closed now."));

		// Unbind all dynamic remote objects
		dynamicRegistry.unbindAll();

		// Super
		super.channelClosed(ctx, e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jboss.netty.channel.SimpleChannelUpstreamHandler#channelOpen(org.
	 * jboss.netty.channel.ChannelHandlerContext,
	 * org.jboss.netty.channel.ChannelStateEvent)
	 */
	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {

		// Save the channel
		channel = ctx.getChannel();

		// Get the socket config
		SocketChannelConfig cfg = (SocketChannelConfig) channel.getConfig();

		// Set both the send and recv buffer
		cfg.setSendBufferSize(SOCKET_SEND_BUFFER_CAPACITY);
		cfg.setReceiveBufferSize(SOCKET_RECV_BUFFER_CAPACITY);

		// Deactivate the tcp delay
		cfg.setTcpNoDelay(true);

		// Create dynamic buffer
		remainingPackets = ChannelBuffers.dynamicBuffer(
				INITIAL_INPUT_BUFFER_CAPACITY, ctx.getChannel().getConfig()
						.getBufferFactory());

		// Create and save the channel memory
		channelMemory = new ChannelMemory(channel);

		// Super
		super.channelOpen(ctx, e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jboss.netty.channel.SimpleChannelHandler#writeRequested(org.jboss
	 * .netty.channel.ChannelHandlerContext,
	 * org.jboss.netty.channel.MessageEvent)
	 */
	@Override
	public void writeRequested(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {

		// Verify the channel
		Channel tmpChannel = verifyChannel();

		// If the channel is not writable but connected wait here!
		while (!tmpChannel.isWritable() && tmpChannel.isOpen()) {
			// Await
			awaitWritable();
		}

		super.writeRequested(ctx, e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxnet.rmi.Connection#close()
	 */
	@Override
	public ChannelFuture close() {
		return verifyChannel().close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxnet.rmi.Connection#getCloseFuture()
	 */
	@Override
	public ChannelFuture getCloseFuture() {
		return verifyChannel().getCloseFuture();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxnet.rmi.Connection#getDynamicRegistry()
	 */
	@Override
	public DynamicRegistry getDynamicRegistry() {
		return dynamicRegistry;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxnet.rmi.Connection#getChannel()
	 */
	@Override
	public Channel getChannel() {
		return channel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxnet.rmi.Connection#getMethodInvocator()
	 */
	@Override
	public Executor getMethodInvocator() {
		return manager.getMethodInvocator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxnet.rmi.Connection#list()
	 */
	@Override
	public String[] list() throws IOException {

		// Add a new request
		Request request = requestManager.addRequest();

		try {
			outputLock.lock();
			// At first write request infos
			output.writeByte(REQUEST + LIST);
			output.writeLong(request.getId());
			// Always flush when ready!
			flushAndWrite();
		} catch (Exception e) {
			// Set cause
			request.failed(e);
		} finally {
			outputLock.unlock();
		}

		// Wait for response
		if (request.await(LIST_REQUEST_TIMEOUT, LIST_REQUEST_TIME_UNIT)) {

			// Get response!
			return (String[]) request.getAttachment();
		} else {
			throw new IOException("List failed. Reason: "
					+ request.getCause().getMessage(), request.getCause());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxnet.rmi.Connection#getManager()
	 */
	@Override
	public ConnectionManager getManager() {
		return manager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxnet.rmi.Connection#invoker(java.lang.String)
	 */
	@Override
	public Invoker invoker(String target) throws IOException {
		// Create new static invoker
		return new DefaultInvoker(target, (RemoteObject) lookupAndWait(target)
				.getAttachment(), false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxnet.rmi.Connection#lookup(java.lang.String)
	 */
	@Override
	public Object lookup(String target) throws IOException {
		// Create new static proxy
		return createProxy(
				(RemoteObject) lookupAndWait(target).getAttachment(), false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxnet.rmi.Connection#getStaticRegistry()
	 */
	@Override
	public StaticRegistry getStaticRegistry() {
		if (staticRegistry == null) {
			throw new IllegalStateException("This dispatcher was created "
					+ "without a static registry.");
		}

		return staticRegistry;
	}
}
