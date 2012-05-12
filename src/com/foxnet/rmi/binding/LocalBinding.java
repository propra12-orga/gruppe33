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
package com.foxnet.rmi.binding;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.foxnet.rmi.LifeCycle;
import com.foxnet.rmi.OrderedExecution;
import com.foxnet.rmi.Remote;
import com.foxnet.rmi.RemoteInterfaces;

/**
 * @author Christopher Probst
 */
public abstract class LocalBinding extends Binding {

	private static boolean isValidRemoteInterface(Class<?> interfaceClass) {
		return interfaceClass != Remote.class
				&& interfaceClass != LifeCycle.class;
	}

	private static void collectRemoteInterfaces(Class<?> startClass,
			Class<?> endClass, Set<Class<?>> interfaces) {

		// Start with the end class
		Class<?> pointer = endClass;

		// Used to return when finished
		boolean finished = false;

		do {

			// The finished parameter
			finished = pointer == startClass;

			// For all interfaces
			for (Class<?> interfaceClass : pointer.getInterfaces()) {
				// Check
				if (isValidRemoteInterface(interfaceClass)) {
					// Add to set
					interfaces.add(interfaceClass);
				}
			}

			// Save
			pointer = pointer.getSuperclass();

		} while (!finished);
	}

	private static Class<?>[] getRemoteInterfaces(Remote target) {
		if (target == null) {
			throw new NullPointerException("target");
		}

		// Get annotation if present
		RemoteInterfaces interfaces = target.getClass().getAnnotation(
				RemoteInterfaces.class);

		// Used to copy the interfaces
		Set<Class<?>> uniqueInterfaces = new HashSet<Class<?>>();

		/*
		 * Use the classes from the annotation.
		 */
		if (interfaces != null && interfaces.value().length > 0) {
			// Check if the interfaces are valid
			for (Class<?> interfaceClass : interfaces.value()) {
				// Check
				if (interfaceClass.isInterface()
						&& isValidRemoteInterface(interfaceClass)
						&& interfaceClass.isAssignableFrom(target.getClass())) {
					// Add to interfaces
					uniqueInterfaces.add(interfaceClass);
				}
			}
		} else {
			// Collect all interfaces
			collectRemoteInterfaces(Object.class, target.getClass(),
					uniqueInterfaces);
		}

		// Check
		if (uniqueInterfaces.isEmpty()) {
			throw new IllegalArgumentException("The target (" + target
					+ ") does not implement any interfaces.");
		}

		// Copy to array
		return uniqueInterfaces.toArray(new Class<?>[uniqueInterfaces.size()]);
	}

	public static final class OrderedExecutionQueue implements Runnable {

		/*
		 * Used to queue the runnables.
		 */
		private final Queue<Runnable> queue = new LinkedList<Runnable>();

		/*
		 * Used to synchronize the queue.
		 */
		private final Lock lock = new ReentrantLock();

		public boolean addOrderedExecution(Runnable runnable)
				throws InterruptedException {
			try {
				lock.lock();

				// Does this queue need to be executed ?
				boolean needsExecution = queue.isEmpty();

				// Offer command!
				queue.offer(runnable);

				// Return the state
				return needsExecution;
			} finally {
				lock.unlock();
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			Runnable runnable = null;

			for (;;) {
				try {
					lock.lock();

					// Get command
					runnable = queue.element();
				} finally {
					lock.unlock();
				}

				// Run!
				runnable.run();

				try {
					lock.lock();

					// Remove command
					queue.remove();

					// Leave the loop if empty!
					if (queue.isEmpty()) {
						return;
					}
				} finally {
					lock.unlock();
				}
			}
		}
	}

	private final OrderedExecutionQueue[] orderedExecutionQueues;
	private final Remote target;

	public LocalBinding(int id, Remote target) {
		this(id, target, getRemoteInterfaces(target));
	}

	public LocalBinding(int id, Remote target, Class<?>[] interfaces) {
		super(id, interfaces);

		// Save target
		this.target = target;

		// Create new ordered execution queues
		orderedExecutionQueues = new OrderedExecutionQueue[getMethodCount()];

		// The method count
		int methodCount = getMethodCount();

		// Annotation
		OrderedExecution oe;

		for (int i = 0; i < methodCount; i++) {
			// Check if OrderedExecution is present...
			if ((oe = getMethods()[i].getAnnotation(OrderedExecution.class)) != null
					&& oe.value()) {

				// Create new queue
				orderedExecutionQueues[i] = new OrderedExecutionQueue();
			} else {
				orderedExecutionQueues[i] = null;
			}
		}
	}

	public boolean isOrderedExecution(int methodId) {
		// Index out of bounds
		if (!containsMethodId(methodId)) {
			throw new IllegalArgumentException("Method id (" + methodId
					+ ") does not exist");
		}

		return orderedExecutionQueues[methodId] != null;
	}

	public OrderedExecutionQueue getQueue(int methodId) {
		if (!isOrderedExecution(methodId)) {
			throw new IllegalArgumentException("Method id (" + methodId
					+ ") does not represent a method with an ordered "
					+ "execution queue.");
		}

		// Get the queue
		return orderedExecutionQueues[methodId];
	}

	public Remote getTarget() {
		return target;
	}

	@Override
	public boolean isStatic() {
		return !isDynamic();
	}

	@Override
	public boolean isDynamic() {
		return this instanceof DynamicBinding;
	}
}
