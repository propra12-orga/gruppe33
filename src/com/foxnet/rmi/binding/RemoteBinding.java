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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a remote binding.
 * 
 * @author Christopher Probst
 */
public final class RemoteBinding extends Binding {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The dynamic flag
	private final boolean dynamic;

	// Maps methods to their ids
	private final Map<Method, Integer> methodIds;

	// Maps method names to their ids
	private final Map<String, Integer> nameIds;

	/**
	 * Creates a new remote binding.
	 * 
	 * @param remoteObject
	 *            The base remote object.
	 * @param dynamic
	 *            The dynamic flag.
	 */
	public RemoteBinding(RemoteObject remoteObject, boolean dynamic) {
		this(remoteObject.id(), remoteObject.interfaces(), dynamic);
	}

	/**
	 * Creates a new remote binding.
	 * 
	 * @param id
	 *            The id of this remote binding.
	 * @param interfaces
	 *            The interface classes of this remote binding.
	 * @param dynamic
	 *            The dynamic flag.
	 */
	public RemoteBinding(long id, Class<?>[] interfaces, boolean dynamic) {
		super(id, interfaces);

		// Save
		this.dynamic = dynamic;

		// Create maps
		int index = 0;
		Map<Method, Integer> tmpMethodIds = new HashMap<Method, Integer>();
		Map<String, Integer> tmpNameIds = new HashMap<String, Integer>();
		for (Method method : methods()) {
			tmpMethodIds.put(method, index);
			tmpNameIds.put(method.getName(), index++);
		}

		// Save unmodifiable maps
		methodIds = Collections.unmodifiableMap(tmpMethodIds);
		nameIds = Collections.unmodifiableMap(tmpNameIds);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxnet.rmi.binding.Binding#isDynamic()
	 */
	@Override
	public boolean isDynamic() {
		return dynamic;
	}

	/**
	 * @return the method-to-id map.
	 */
	public Map<Method, Integer> methodIds() {
		return methodIds;
	}

	/**
	 * @return the method-name-to-id map.
	 */
	public Map<String, Integer> nameIds() {
		return nameIds;
	}
}
