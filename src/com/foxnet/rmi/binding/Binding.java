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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * This class represents an abstract binding.
 * 
 * @author Christopher Probst
 */
public abstract class Binding extends RemoteObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// All interface methods (ordered by name)
	private final List<Method> methods;

	/**
	 * Creates a new binding.
	 * 
	 * @param id
	 *            The id of this binding.
	 * @param interfaces
	 *            The interface classes of this binding.
	 */
	public Binding(long id, Class<?>[] interfaces) {
		super(id, interfaces);

		// This class does not allow null!
		if (interfaces == null) {
			throw new NullPointerException("interfaces");
		}

		// Tmp hash map for storing information about the methods
		SortedMap<String, Method> methodMap = new TreeMap<String, Method>();

		// Collect all methods
		for (Class<?> interfaceClass : interfaces) {

			// For all methods of the interface
			for (Method method : interfaceClass.getMethods()) {

				// Get the string
				String signature = method.getName();

				// Build the string signature
				for (Class<?> parameterType : method.getParameterTypes()) {
					signature += parameterType.getName();
				}

				// Add method if different
				if (!methodMap.containsKey(signature)) {

					// Put into map
					methodMap.put(signature, method);
				}
			}
		}

		// Stop here... We need methods!!
		if (methodMap.isEmpty()) {
			throw new IllegalArgumentException("The given interfaces "
					+ "do not have any methods");
		}

		// Get array
		methods = Collections.unmodifiableList(new ArrayList<Method>(methodMap
				.values()));
	}

	/**
	 * @return the list which contains all methods.
	 */
	public List<Method> methods() {
		return methods;
	}

	/**
	 * Checks a given method id.
	 * 
	 * @param methodId
	 *            The method id you want to check.
	 * @return true if the id is valid, otherwise false.
	 */
	public boolean containsMethodId(int methodId) {
		return methodId >= 0 && methodId < methods.size();
	}

	/**
	 * @return true if this is a static binding, otherwise false.
	 */
	public boolean isStatic() {
		return !isDynamic();
	}

	/**
	 * @return true if this is a dynamic binding, otherwise false.
	 */
	public abstract boolean isDynamic();
}
