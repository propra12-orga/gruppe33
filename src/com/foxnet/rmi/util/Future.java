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
package com.foxnet.rmi.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class represents a future which can be basically any asynchronous
 * operation. You can add/remove {@link FutureCallback}s to react on the given
 * event. This class is thread-safe and optimized for concurrent usage.
 * 
 * @author Christopher Probst
 * @see FutureCallback
 */
public class Future {

	// The logger
	protected final Logger logger = Logger.getLogger(getClass().getName());

	// The lock object
	private final Object lock = new Object();

	// Used to store callbacks
	private List<FutureCallback> lazyCallbacks;

	// Used to store the cause
	private Throwable cause;

	// Used to store the attachment
	private Object attachment;

	// Used to store the state
	private volatile boolean completed;

	/**
	 * Used to modify the attachment when completed.
	 * 
	 * @param attachment
	 *            The attachment you can modify.
	 * @return the attachment which should be saved.
	 */
	protected Object modifyAttachment(Object attachment) {
		return attachment;
	}

	/**
	 * Used to modify the cause when completed.
	 * 
	 * @param cause
	 *            The cause you can modify.
	 * @return the cause which should be saved.
	 */
	protected Throwable modifyCause(Throwable cause) {
		return cause;
	}

	/**
	 * Notifies the given callback.
	 * 
	 * @param callback
	 *            The callback you want to notify.
	 */
	private void notifyCallback(FutureCallback callback) {
		try {
			callback.completed(this);
		} catch (Exception e) {
			logger.warning("Failed to notify callback. Reason: "
					+ e.getMessage());
		}
	}

	/**
	 * Adds a callback to this future. If the future is already completed it
	 * will be called immediately, otherwise it will be queued until completion.
	 * 
	 * @param callback
	 *            The callback you want to add.
	 * @return true if the callback is added to the future, otherwise false.
	 * 
	 */
	public boolean add(FutureCallback callback) {
		if (callback == null) {
			throw new NullPointerException("callback");
		}

		// The notify now flag
		boolean notifyNow = false;

		// Synchronize with lock
		synchronized (lock) {

			// Not completed yet ?
			if (!completed) {

				// Do lazy callback creation
				if (lazyCallbacks == null) {
					lazyCallbacks = new ArrayList<FutureCallback>();
				}

				// Add to list
				lazyCallbacks.add(callback);
			} else {
				notifyNow = true;
			}
		}

		// Notify the callback now ?
		if (notifyNow) {
			notifyCallback(callback);
		}

		return !notifyNow;
	}

	/**
	 * Removes a callback from this future. If the future is already completed
	 * nothing happens.
	 * 
	 * @param callback
	 *            The callback you want to remove.
	 * @return true if the callback is removed, otherwise false.
	 */
	public boolean remove(FutureCallback callback) {
		if (callback == null) {
			throw new NullPointerException("callback");
		}

		synchronized (lock) {

			if (completed || lazyCallbacks == null || lazyCallbacks.isEmpty()) {
				return false;
			}

			return lazyCallbacks.remove(callback);
		}
	}

	/**
	 * Synchronize the future with the calling thread. Basically this method
	 * blocks until the future completes. If this thread gets interrupted before
	 * the future is completed this method will fail this future. So after
	 * calling this method this future is definitely completed.
	 * 
	 * @return true if the future was successful, otherwise false.
	 */
	public boolean synchronize() {
		return synchronize(-1);
	}

	/**
	 * Synchronize the future with the calling thread. Basically this method
	 * blocks until the future completes OR the given timeout expires. If this
	 * thread gets interrupted before the future is completed or the timeout
	 * expires this method will fail this future. So after calling this method
	 * this future is definitely completed.
	 * 
	 * @param timeoutMillies
	 *            The timeout you want to wait in milliseconds.
	 * @return true if the future was successful, otherwise false.
	 */
	public boolean synchronize(long timeoutMillies) {

		synchronized (lock) {
			if (!completed) {
				try {
					// If the timeout makes sense use it!
					if (timeoutMillies > 0) {
						// Wait using the given timeout
						lock.wait(timeoutMillies);

						/*
						 * If the future is still not completed yet the timeout
						 * must have reached the limit. We fail this future in
						 * this case.
						 */
						if (!completed) {
							fail(new IllegalStateException("Future has timed "
									+ "out during synchronization"));
						}
					} else {
						// Default wait
						lock.wait();
					}
				} catch (InterruptedException e) {
					// Fail this future (Thread got interrupted...)
					fail(e);
				}
			}

			// True if success (cause == null), otherwise false!
			return isSuccessful();
		}
	}

	/**
	 * @return true if this future is completed and has a cause.
	 */
	public boolean isFailed() {
		if (!completed) {
			throw new IllegalStateException("Future is not completed yet");
		}

		return cause != null;
	}

	/**
	 * @return true if this future is completed and has not a cause.
	 */
	public boolean isSuccessful() {
		if (!completed) {
			throw new IllegalStateException("Future is not completed yet");
		}

		return !isFailed();
	}

	/**
	 * @return true if this future is completed.
	 */
	public boolean isCompleted() {
		return completed;
	}

	/**
	 * @return the cause of this future.
	 */
	public Throwable cause() {
		// Volatile refresh
		if (!completed) {
			throw new IllegalStateException("Future is not completed yet");
		}

		return cause;
	}

	/**
	 * @return the attachment of this future.
	 */
	public Object attachment() {
		// Volatile refresh
		if (!completed) {
			throw new IllegalStateException("Future is not completed yet");
		}

		return attachment;
	}

	/**
	 * Fails this future if it is not completed yet.
	 * 
	 * @param cause
	 *            The cause of this future.
	 * @return true if the future is completed now, otherwise false.
	 */
	public boolean fail(Throwable cause) {
		if (cause == null) {
			throw new NullPointerException("cause");
		}
		return complete(null, cause);
	}

	/**
	 * Succeeds this future if it is not completed yet.
	 * 
	 * @param attachment
	 *            The attachment you want to set.
	 * @return true if the future is completed now, otherwise false.
	 */
	public boolean succeed(Object attachment) {
		return complete(attachment, null);
	}

	/**
	 * Completes this future if it is not completed yet.
	 * 
	 * @param attachment
	 *            The attachment you want to set.
	 * @param cause
	 *            The cause of this future.
	 * @return true if the future is completed now, otherwise false.
	 */
	public boolean complete(Object attachment, Throwable cause) {
		List<FutureCallback> tmpCallbacks;

		synchronized (lock) {
			if (completed) {
				return false;
			}

			// Save attachment
			this.attachment = modifyAttachment(attachment);

			// Save cause
			this.cause = modifyCause(cause);

			// Set completed to true
			completed = true;

			// Signal all!
			lock.notifyAll();

			// Save old callbacks
			tmpCallbacks = lazyCallbacks;

			// GC
			lazyCallbacks = null;
		}
		// Notify the queued callbacks
		if (tmpCallbacks != null) {
			for (FutureCallback callback : tmpCallbacks) {
				notifyCallback(callback);
			}
		}

		return true;
	}
}
