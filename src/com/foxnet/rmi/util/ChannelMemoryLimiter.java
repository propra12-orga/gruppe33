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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.channel.Channel;

/**
 * @author Christopher Probst
 */
public final class ChannelMemoryLimiter {

	public static final class ChannelMemory {

		// The channel which owns the memory
		private final Channel channel;

		// Used to suspend & resume this memory user
		private boolean suspended;

		private synchronized void suspend(
				Queue<ChannelMemory> channelMemoryQueue) {
			if (!suspended) {
				channel.setReadable(false);
				suspended = true;
				channelMemoryQueue.offer(this);
			}
		}

		private synchronized void resume() {
			if (suspended) {
				channel.setReadable(true);
				suspended = false;
			}
		}

		public ChannelMemory(Channel channel) {
			if (channel == null) {
				throw new NullPointerException("channel");
			}

			this.channel = channel;
		}
	}

	private final int maxMemory;
	private final AtomicInteger remainingMemory;
	private final Queue<ChannelMemory> channelMemoryQueue = new ConcurrentLinkedQueue<ChannelMemory>();

	public ChannelMemoryLimiter(int remainingMemory) {
		if (remainingMemory < 1) {
			throw new IllegalArgumentException("remainingMemory cannot "
					+ "be smaller than one.");
		}

		this.remainingMemory = new AtomicInteger(maxMemory = remainingMemory);
	}

	public int getMaxMemory() {
		return maxMemory;
	}

	public int getRemainingMemory() {
		return remainingMemory.get();
	}

	public boolean hasRemainingMemory() {
		return remainingMemory.get() > 0;
	}

	public void acquire(ChannelMemory channelMemory, int size) {
		// Reduce the remaining memory
		if (remainingMemory.addAndGet(-size) < 0) {
			channelMemory.suspend(channelMemoryQueue);
		}
	}

	public void release(int size) {
		// Increase the remaining memory
		remainingMemory.addAndGet(size);

		// Tmp memory user
		ChannelMemory channelMemory;

		// While there is remaining memory and waiting memory users
		while (hasRemainingMemory()
				&& (channelMemory = channelMemoryQueue.poll()) != null) {
			channelMemory.resume();
		}
	}
}
