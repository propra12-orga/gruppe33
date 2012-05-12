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
package com.foxnet.rmi.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import com.foxnet.rmi.Invocation;
import com.foxnet.rmi.InvocationCallback;
import com.foxnet.rmi.Invoker;
import com.foxnet.rmi.impl.RequestManager.Request;

/**
 * @author Christopher Probst
 */
final class Invocations {

	private Invocations() {
	}

	private static abstract class AbstractInvocation implements Invocation {

		// Used to store the invoker
		private final Invoker invoker;

		// Used to store the method name
		private final String methodName;

		// Used to store the args
		private final Object[] args;

		protected void notifyCallback(InvocationCallback callback) {
			try {
				callback.completed(this);
			} catch (Exception e) {
				Logger.getLogger(Request.class.getName()).warning(
						"Failed to notify callback. Reason: " + e.getMessage());
			}
		}

		public AbstractInvocation(Invoker invoker, String methodName,
				Object[] args) {
			this.invoker = invoker;
			this.methodName = methodName;
			this.args = args;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#getInvoker()
		 */
		@Override
		public Invoker getInvoker() {
			return invoker;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#getMethodName()
		 */
		@Override
		public String getMethodName() {
			return methodName;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#getArguments()
		 */
		@Override
		public Object[] getArguments() {
			return args;
		}
	}

	private static final class DefaultInvocation extends AbstractInvocation {

		// Used to synchronize the invocation
		private final Lock lock = new ReentrantLock();
		private final Condition completion = lock.newCondition();

		// Used to store callbacks
		private List<InvocationCallback> lazyCallbacks;

		// Used to store the cause
		private Throwable cause;

		// Used to store the response
		private Object response;

		// Used to store the state
		private volatile boolean completed;

		private List<InvocationCallback> complete() {

			// Set completed to true
			completed = true;

			// Signal all!
			completion.signalAll();

			// Save old callbacks
			List<InvocationCallback> tmpCallbacks = lazyCallbacks;

			// GC
			lazyCallbacks = null;

			return tmpCallbacks;
		}

		public DefaultInvocation(Invoker invoker, String methodName,
				Object[] args) {
			super(invoker, methodName, args);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#add(com.foxnet.rmi.InvocationCallback)
		 */
		@Override
		public boolean add(InvocationCallback callback) {
			if (callback == null) {
				throw new NullPointerException("callback");
			}

			boolean notifyNow = false;

			try {
				lock.lock();

				if (!completed) {
					if (lazyCallbacks == null) {
						lazyCallbacks = new ArrayList<InvocationCallback>();
					}

					lazyCallbacks.add(callback);
				} else {
					notifyNow = true;
				}
			} finally {
				lock.unlock();
			}

			if (notifyNow) {
				notifyCallback(callback);
				return false;
			}

			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.foxnet.rmi.Invocation#remove(com.foxnet.rmi.InvocationCallback)
		 */
		@Override
		public boolean remove(InvocationCallback callback) {
			if (callback == null) {
				throw new NullPointerException("callback");
			}

			try {
				lock.lock();

				if (completed || lazyCallbacks == null
						|| lazyCallbacks.isEmpty()) {
					return false;
				}

				return lazyCallbacks.remove(callback);
			} finally {
				lock.unlock();
			}
		}

		public boolean await() {
			return await(-1, null);
		}

		public boolean await(long timeout, TimeUnit timeUnit) {
			try {
				lock.lock();
				if (!completed) {
					try {
						if (timeout < 0) {
							// Await the completion!
							completion.await();
						} else {
							// Await the completion!
							if (!completion.await(timeout, timeUnit)) {
								// Set new timeout exception.
								setCause(new IllegalStateException(
										"Command has been timed out."));
							}
						}
					} catch (InterruptedException e) {
						// Set exception.
						setCause(e);
					}
				}

				// True if success (cause == null), otherwise false!
				return cause == null;
			} finally {
				lock.unlock();
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#isCompleted()
		 */
		@Override
		public boolean isCompleted() {
			return completed;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#getCause()
		 */
		@Override
		public Throwable getCause() {
			// Volatile refresh
			if (!isCompleted()) {
				throw new IllegalStateException("Request is not "
						+ "completed yet.");
			}

			return cause;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#getResponse()
		 */
		@Override
		public Object getResponse() {
			// Volatile refresh
			if (!isCompleted()) {
				throw new IllegalStateException("Request is not "
						+ "completed yet.");
			}

			return response;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#isFailed()
		 */
		@Override
		public boolean isFailed() {
			return isCompleted() ? cause != null : false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#isSuccessful()
		 */
		@Override
		public boolean isSuccessful() {
			return isCompleted() ? cause == null : false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#setCause(java.lang.Throwable)
		 */
		@Override
		public boolean setCause(Throwable cause) {
			List<InvocationCallback> tmpCallbacks;

			try {
				lock.lock();

				if (completed) {
					return false;
				}

				// Save
				this.cause = cause;

				// Complete this request
				tmpCallbacks = complete();
			} finally {
				lock.unlock();
			}

			if (tmpCallbacks != null) {
				for (InvocationCallback callback : tmpCallbacks) {
					notifyCallback(callback);
				}
			}

			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#setResponse(java.lang.Object)
		 */
		@Override
		public boolean setResponse(Object response) {
			List<InvocationCallback> tmpCallbacks;

			try {
				lock.lock();

				if (completed) {
					return false;
				}

				// Save
				this.response = response;

				// Complete this request
				tmpCallbacks = complete();
			} finally {
				lock.unlock();
			}

			if (tmpCallbacks != null) {
				for (InvocationCallback callback : tmpCallbacks) {
					notifyCallback(callback);
				}
			}

			return true;
		}
	}

	private static final class SucceededInvocation extends AbstractInvocation {

		private final Object response;

		public SucceededInvocation(Invoker invoker, String methodName,
				Object[] args, Object response) {
			super(invoker, methodName, args);
			this.response = response;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#add(com.foxnet.rmi.InvocationCallback)
		 */
		@Override
		public boolean add(InvocationCallback callback) {
			notifyCallback(callback);
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.foxnet.rmi.Invocation#remove(com.foxnet.rmi.InvocationCallback)
		 */
		@Override
		public boolean remove(InvocationCallback callback) {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#await()
		 */
		@Override
		public boolean await() {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#await(long,
		 * java.util.concurrent.TimeUnit)
		 */
		@Override
		public boolean await(long timeout, TimeUnit timeUnit) {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#isCompleted()
		 */
		@Override
		public boolean isCompleted() {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#getCause()
		 */
		@Override
		public Object getCause() {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#getResponse()
		 */
		@Override
		public Object getResponse() {
			return response;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#isSuccessful()
		 */
		@Override
		public boolean isSuccessful() {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#isFailed()
		 */
		@Override
		public boolean isFailed() {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#setCause(java.lang.Throwable)
		 */
		@Override
		public boolean setCause(Throwable cause) {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#setResponse(java.lang.Object)
		 */
		@Override
		public boolean setResponse(Object response) {
			return false;
		}
	}

	private static final class FailedInvocation extends AbstractInvocation {

		private final Throwable cause;

		public FailedInvocation(Invoker invoker, String methodName,
				Object[] args, Throwable cause) {
			super(invoker, methodName, args);
			this.cause = cause;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#add(com.foxnet.rmi.InvocationCallback)
		 */
		@Override
		public boolean add(InvocationCallback callback) {
			notifyCallback(callback);
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.foxnet.rmi.Invocation#remove(com.foxnet.rmi.InvocationCallback)
		 */
		@Override
		public boolean remove(InvocationCallback callback) {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#await()
		 */
		@Override
		public boolean await() {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#await(long,
		 * java.util.concurrent.TimeUnit)
		 */
		@Override
		public boolean await(long timeout, TimeUnit timeUnit) {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#isCompleted()
		 */
		@Override
		public boolean isCompleted() {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#getCause()
		 */
		@Override
		public Object getCause() {
			return cause;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#getResponse()
		 */
		@Override
		public Object getResponse() {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#isSuccessful()
		 */
		@Override
		public boolean isSuccessful() {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#isFailed()
		 */
		@Override
		public boolean isFailed() {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#setCause(java.lang.Throwable)
		 */
		@Override
		public boolean setCause(Throwable cause) {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.foxnet.rmi.Invocation#setResponse(java.lang.Object)
		 */
		@Override
		public boolean setResponse(Object response) {
			return false;
		}
	}

	public static Invocation defaultInvocation(Invoker invoker,
			String methodName, Object[] args) {
		return new DefaultInvocation(invoker, methodName, args);
	}

	public static Invocation succeededInvocation(Invoker invoker,
			String methodName, Object[] args, Object response) {
		return new SucceededInvocation(invoker, methodName, args, response);
	}

	public static Invocation failedInvocation(Invoker invoker,
			String methodName, Object[] args, Throwable cause) {
		return new FailedInvocation(invoker, methodName, args, cause);
	}
}
