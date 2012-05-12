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
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

/**
 * @author Christopher Probst
 */
final class RequestManager {

	public static interface RequestCallback {

		void requestCompleted(Request request) throws Exception;
	}

	public final class Request {

		// Used to synchronize the request
		private final Lock requestLock = new ReentrantLock();
		private final Condition completion = requestLock.newCondition();

		// Used to store callbacks
		private List<RequestCallback> lazyCallbacks;

		// Used to identify the request
		private volatile int id;

		// Used to store the initial request
		private final Object request;

		// Used to store the cause
		private Throwable cause;

		// Used to store the response
		private Object response;

		// Used to store the state
		private volatile boolean completed;

		private void notifyCallback(RequestCallback callback) {
			try {
				callback.requestCompleted(this);
			} catch (Exception e) {
				Logger.getLogger(Request.class.getName()).warning(
						"Failed to notify callback. Reason: " + e.getMessage());
			}
		}

		private List<RequestCallback> complete() {

			// Set completed to true
			completed = true;

			// Signal all!
			completion.signalAll();

			// Remove this request
			requests.remove(id);

			// Save old callbacks
			List<RequestCallback> tmpCallbacks = lazyCallbacks;

			// GC
			lazyCallbacks = null;

			return tmpCallbacks;
		}

		private void setId(int id) {
			this.id = id;
		}

		private Request(Object request) {
			this.request = request;
		}

		public boolean queue(RequestCallback callback) {
			if (callback == null) {
				throw new NullPointerException("callback");
			}

			boolean notifyNow = false;

			try {
				requestLock.lock();

				if (!completed) {
					if (lazyCallbacks == null) {
						lazyCallbacks = new ArrayList<RequestCallback>();
					}

					lazyCallbacks.add(callback);
				} else {
					notifyNow = true;
				}
			} finally {
				requestLock.unlock();
			}

			if (notifyNow) {
				notifyCallback(callback);
				return false;
			}

			return true;
		}

		public boolean dequeue(RequestCallback callback) {
			if (callback == null) {
				throw new NullPointerException("callback");
			}

			try {
				requestLock.lock();

				if (completed || lazyCallbacks == null
						|| lazyCallbacks.isEmpty()) {
					return false;
				}

				return lazyCallbacks.remove(callback);
			} finally {
				requestLock.unlock();
			}
		}

		public boolean await(long timeout, TimeUnit timeUnit) {
			try {
				requestLock.lock();
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
				requestLock.unlock();
			}
		}

		public RequestManager getRequestStorage() {
			return RequestManager.this;
		}

		public boolean isFailed() {
			return isCompleted() ? cause != null : false;
		}

		public boolean isSuccessful() {
			return isCompleted() ? cause == null : false;
		}

		public Object getRequest() {
			return request;
		}

		public int getId() {
			return id;
		}

		public boolean isCompleted() {
			return completed;
		}

		public Throwable getCause() {
			// Volatile refresh
			if (!isCompleted()) {
				throw new IllegalStateException("Request is not "
						+ "completed yet.");
			}

			return cause;
		}

		public Object getResponse() {
			// Volatile refresh
			if (!isCompleted()) {
				throw new IllegalStateException("Request is not "
						+ "completed yet.");
			}

			return response;
		}

		public boolean setCause(Throwable cause) {
			List<RequestCallback> tmpCallbacks;

			try {
				requestLock.lock();

				if (completed) {
					return false;
				}

				// Save
				this.cause = cause;

				// Complete this request
				tmpCallbacks = complete();
			} finally {
				requestLock.unlock();
			}

			if (tmpCallbacks != null) {
				for (RequestCallback callback : tmpCallbacks) {
					notifyCallback(callback);
				}
			}

			return true;
		}

		public boolean setResponse(Object response) {
			List<RequestCallback> tmpCallbacks;

			try {
				requestLock.lock();

				if (completed) {
					return false;
				}

				// Save
				this.response = response;

				// Complete this request
				tmpCallbacks = complete();
			} finally {
				requestLock.unlock();
			}

			if (tmpCallbacks != null) {
				for (RequestCallback callback : tmpCallbacks) {
					notifyCallback(callback);
				}
			}

			return true;
		}
	}

	// Used to store the requests
	private final ConcurrentMap<Integer, Request> requests = new ConcurrentHashMap<Integer, Request>();

	public Collection<Request> getRequests() {
		return requests.values();
	}

	public Request getRequest(int id) {
		// Try to get request
		return requests.get(id);
	}

	public void setCauseToAll(Throwable cause) {
		// Copy requests
		List<Request> remainingRequests = new ArrayList<Request>(getRequests());
		requests.clear();

		for (Request r : remainingRequests) {
			r.setCause(cause);
		}
	}

	public Request addRequest() {

		// Create request object
		Request tmpRequest = new Request(null);

		// Get system hash code
		int hash = System.identityHashCode(tmpRequest);

		while (requests.putIfAbsent(hash, tmpRequest) != null) {
			// Increase hash
			hash++;
		}

		// Finally found a valid hash
		tmpRequest.setId(hash);

		return tmpRequest;
	}

	public Request addRequest(Object request) {

		// Get system hash code
		int hash = System.identityHashCode(request);

		// Create request object
		Request tmpRequest = new Request(request);

		while (requests.putIfAbsent(hash, tmpRequest) != null) {
			// Increase hash
			hash++;
		}

		// Finally found a valid hash
		tmpRequest.setId(hash);

		return tmpRequest;
	}
}
