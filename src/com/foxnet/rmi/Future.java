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
package com.foxnet.rmi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class Future {

	// Used to synchronize the future
	private final Lock futureLock = new ReentrantLock();
	private final Condition completion = futureLock.newCondition();

	// Used to store callbacks
	private List<FutureCallback> lazyCallbacks;

	// Used to store the cause
	private Throwable cause;

	// Used to store the attachment
	private Object attachment;

	// Used to store the state
	private volatile boolean completed;

	private void notifyCallback(FutureCallback callback) {
		try {
			callback.completed(this);
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).warning(
					"Failed to notify callback. Reason: " + e.getMessage());
		}
	}

	private List<FutureCallback> complete() {

		// Set completed to true
		completed = true;

		// Signal all!
		completion.signalAll();

		// Save old callbacks
		List<FutureCallback> tmpCallbacks = lazyCallbacks;

		// GC
		lazyCallbacks = null;

		return tmpCallbacks;
	}

	public boolean add(FutureCallback callback) {
		if (callback == null) {
			throw new NullPointerException("callback");
		}

		boolean notifyNow = false;

		try {
			futureLock.lock();

			if (!completed) {
				if (lazyCallbacks == null) {
					lazyCallbacks = new ArrayList<FutureCallback>();
				}

				lazyCallbacks.add(callback);
			} else {
				notifyNow = true;
			}
		} finally {
			futureLock.unlock();
		}

		if (notifyNow) {
			notifyCallback(callback);
			return false;
		}

		return true;
	}

	public boolean remove(FutureCallback callback) {
		if (callback == null) {
			throw new NullPointerException("callback");
		}

		try {
			futureLock.lock();

			if (completed || lazyCallbacks == null || lazyCallbacks.isEmpty()) {
				return false;
			}

			return lazyCallbacks.remove(callback);
		} finally {
			futureLock.unlock();
		}
	}

	public boolean await(long timeout, TimeUnit timeUnit) {
		try {
			futureLock.lock();
			if (!completed) {
				try {
					if (timeout < 0) {
						// Await the completion!
						completion.await();
					} else {
						// Await the completion!
						if (!completion.await(timeout, timeUnit)) {
							// Set new timeout exception.
							failed(new IllegalStateException(
									"Command has been timed out."));
						}
					}
				} catch (InterruptedException e) {
					// Set exception.
					failed(e);
				}
			}

			// True if success (cause == null), otherwise false!
			return cause == null;
		} finally {
			futureLock.unlock();
		}
	}

	public boolean isFailed() {
		return isCompleted() ? cause != null : false;
	}

	public boolean isSuccessful() {
		return isCompleted() ? cause == null : false;
	}

	public boolean isCompleted() {
		return completed;
	}

	public Throwable getCause() {
		// Volatile refresh
		if (!isCompleted()) {
			throw new IllegalStateException("Future is not completed yet.");
		}

		return cause;
	}

	public Object getAttachment() {
		// Volatile refresh
		if (!isCompleted()) {
			throw new IllegalStateException("Future is not completed yet.");
		}

		return attachment;
	}

	public boolean failed(Throwable cause) {
		List<FutureCallback> tmpCallbacks;

		try {
			futureLock.lock();

			if (completed) {
				return false;
			}

			// Save
			this.cause = cause;

			// Complete this future
			tmpCallbacks = complete();
		} finally {
			futureLock.unlock();
		}

		if (tmpCallbacks != null) {
			for (FutureCallback callback : tmpCallbacks) {
				notifyCallback(callback);
			}
		}

		return true;
	}

	public boolean completed(Object attachment) {
		List<FutureCallback> tmpCallbacks;

		try {
			futureLock.lock();

			if (completed) {
				return false;
			}

			// Save
			this.attachment = attachment;

			// Complete this future
			tmpCallbacks = complete();
		} finally {
			futureLock.unlock();
		}

		if (tmpCallbacks != null) {
			for (FutureCallback callback : tmpCallbacks) {
				notifyCallback(callback);
			}
		}

		return true;
	}
}
