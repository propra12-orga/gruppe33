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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.foxnet.rmi.LifeCycle;
import com.foxnet.rmi.OrderedExecution;
import com.foxnet.rmi.Remote;
import com.foxnet.rmi.RemoteInterfaces;

/**
 * This represents an abstract local binding.
 * 
 * @author Christopher Probst
 */
public abstract class LocalBinding extends Binding {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;

	/**
	 * Simply checks whether or not the given interface class is valid.
	 * 
	 * @param interfaceClass
	 *            The interface class you want to check.
	 * @return true if the interface class is valid, otherwise false.
	 */
	private static boolean isValidRemoteInterface(Class<?> interfaceClass) {
		return interfaceClass != Remote.class
				&& interfaceClass != LifeCycle.class;
	}

	/**
	 * This method collects all valid remote interfaces.
	 * 
	 * @param startClass
	 *            The start class.
	 * @param endClass
	 *            The end class.
	 * @param interfaces
	 *            The set with all remote interface classes.
	 */
	private static void collectAllRemoteInterfaces(Class<?> startClass,
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

	/**
	 * This method collects all remote interfaces or uses the
	 * {@link RemoteInterfaces} annotation if present to use only the specified
	 * intefaces.
	 * 
	 * @param target
	 *            The remote target.
	 * @return an array with all valid remote interfaces.
	 */
	private static Class<?>[] getRemoteInterfaces(Remote target) {
		if (target == null) {
			throw new NullPointerException("target");
		}

		// Get annotation if present
		RemoteInterfaces interfaces = target.getClass().getAnnotation(
				RemoteInterfaces.class);

		// Used to collect the interfaces
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
			collectAllRemoteInterfaces(Object.class, target.getClass(),
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

	/**
	 * This class implements the concept of an ordered execution queue.
	 * 
	 * @author Christopher Probst
	 */
	public static final class OrderedExecutionQueue implements Runnable,
			Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/*
		 * Used to queue the runnables.
		 */
		private final Queue<Runnable> queue = new LinkedList<Runnable>();

		/**
		 * This method offers a runnable object.
		 * 
		 * @param runnable
		 *            The runnable you want to add.
		 * @return true if this queue needs to be executed.
		 */
		public synchronized boolean addOrderedExecution(Runnable runnable) {

			// Does this queue need to be executed ?
			boolean needsExecution = queue.isEmpty();

			// Offer command!
			queue.offer(runnable);

			// Return the state
			return needsExecution;
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
				synchronized (this) {
					// Get command
					runnable = queue.element();
				}

				// Run!
				runnable.run();

				synchronized (this) {

					// Remove command
					queue.remove();

					// Leave the loop if empty!
					if (queue.isEmpty()) {
						return;
					}
				}
			}
		}
	}

	// Here we store all ordered execution queues mapped to their method ids
	private final Map<Integer, OrderedExecutionQueue> orderedExecutionQueues;

	// Here we store the remote target
	private final Remote target;

	/**
	 * Creates a new local binding using the default
	 * interface-collect-algorithm.
	 * 
	 * @param id
	 *            The id of this binding.
	 * @param target
	 *            The target of this binding.
	 */
	public LocalBinding(long id, Remote target) {
		this(id, target, getRemoteInterfaces(target));
	}

	/**
	 * Creates a new local binding.
	 * 
	 * @param id
	 *            The id of this binding.
	 * @param target
	 *            The target of this binding.
	 * @param interfaces
	 *            The interface classes of the target.
	 */
	public LocalBinding(long id, Remote target, Class<?>[] interfaces) {
		super(id, interfaces);

		// Check all given interface classes
		for (Class<?> interfaceClass : interfaces) {
			if (!interfaceClass.isAssignableFrom(target.getClass())) {
				throw new IllegalArgumentException(interfaceClass
						+ " is not part of the hierarchy of "
						+ target.getClass());
			}
		}

		// Save target
		this.target = target;

		// The method count
		int methodCount = getMethodCount();

		// Annotation
		OrderedExecution oe;

		// Create tmp
		Map<Integer, OrderedExecutionQueue> tmpOrderedExecutionQueues = null;

		// Add all methods which
		for (int i = 0; i < methodCount; i++) {
			// Check if OrderedExecution is present...
			if ((oe = getMethod(i).getAnnotation(OrderedExecution.class)) != null
					&& oe.value()) {

				// Lazy setup
				if (tmpOrderedExecutionQueues == null) {
					tmpOrderedExecutionQueues = new HashMap<Integer, OrderedExecutionQueue>();
				}

				// Create new queue
				tmpOrderedExecutionQueues.put(i, new OrderedExecutionQueue());
			}
		}

		// Set the map
		orderedExecutionQueues = tmpOrderedExecutionQueues != null ? Collections
				.unmodifiableMap(tmpOrderedExecutionQueues) : null;
	}

	/**
	 * Checks a method for ordered execution.
	 * 
	 * @param methodId
	 *            The method id of the method you want to check.
	 * @return true if the given method uses ordered execution, otherwise false.
	 */
	public boolean isOrderedExecution(int methodId) {
		// Index out of bounds
		if (!containsMethodId(methodId)) {
			throw new IllegalArgumentException("Method id (" + methodId
					+ ") does not exist");
		}

		return orderedExecutionQueues != null ? orderedExecutionQueues
				.get(methodId) != null : false;
	}

	/**
	 * Returns the ordered execution queue of the given method.
	 * 
	 * @param methodId
	 *            The method id.
	 * @return the queue or null if there is no such queue.
	 */
	public OrderedExecutionQueue getQueue(int methodId) {
		// Index out of bounds
		if (!containsMethodId(methodId)) {
			throw new IllegalArgumentException("Method id (" + methodId
					+ ") does not exist");
		}

		// Try to get the queue
		return orderedExecutionQueues != null ? orderedExecutionQueues
				.get(methodId) : null;
	}

	/**
	 * @return the remote target.
	 */
	public Remote getTarget() {
		return target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxnet.rmi.binding.Binding#isDynamic()
	 */
	@Override
	public boolean isDynamic() {
		return this instanceof DynamicBinding;
	}
}
