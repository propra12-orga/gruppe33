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
package com.indyforge.foxnet.rmi.binding.registry;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import com.indyforge.foxnet.rmi.Remote;
import com.indyforge.foxnet.rmi.binding.DynamicBinding;
import com.indyforge.foxnet.rmi.binding.RemoteObject;

/**
 * A dynamic registry is used to store dynamically created bindings.
 * 
 * @author Christopher Probst
 * @see DynamicBinding
 */
public final class DynamicRegistry extends Registry<DynamicBinding> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Used to store the ids
	private final Map<Long, DynamicBinding> ids = new HashMap<Long, DynamicBinding>();

	// Used to store the dynamic objects
	private final Map<Remote, DynamicBinding> objects = new IdentityHashMap<Remote, DynamicBinding>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.foxnet.rmi.binding.registry.AbstractRegistry#bindingMap()
	 */
	@Override
	protected Map<Long, DynamicBinding> bindingMap() {
		return ids;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.foxnet.rmi.registry.Registry#unbindAll()
	 */
	@Override
	public synchronized DynamicRegistry unbindAll() {
		// Unbound all
		for (Remote target : objects.keySet()) {
			fireUnboundFrom(target);
		}

		// Clear the remaining maps
		objects.clear();
		ids.clear();

		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.foxnet.rmi.registry.Registry#unbind(long)
	 */
	@Override
	public synchronized DynamicBinding unbind(long id) {
		// Remove from id map
		DynamicBinding db = ids.remove(id);

		if (db != null) {
			// Remove from object map
			objects.remove(db.target());

			// Notify
			fireUnboundFrom(db.target());
		}
		return db;
	}

	/**
	 * Removes the binding with the given target.
	 * 
	 * @param target
	 *            The target of the binding.
	 * @return the old binding or null.
	 */
	public synchronized DynamicBinding unbind(Object target) {
		// Remove from object map
		DynamicBinding db = objects.remove(target);

		if (db != null) {
			// Remove from id map
			ids.remove(db.id());

			// Notify
			fireUnboundFrom(db.target());
		}
		return db;
	}

	/**
	 * @param target
	 *            The target of the binding.
	 * @return the binding with the given target or null.
	 */
	public synchronized DynamicBinding get(Remote target) {
		return objects.get(target);
	}

	/**
	 * Replaces {@link Remote} objects and stores them dynamically. If the given
	 * target is not a {@link Remote} object nothing happens.
	 * 
	 * @param target
	 *            The target object.
	 * @return the given target or a {@link RemoteObject} reference if the given
	 *         target was a {@link Remote} object.
	 */
	public Object replaceRemote(Object target) {
		if (!(target instanceof Remote)) {
			return target;
		}

		// Get remote binding
		return new RemoteObject(bindIfAbsent((Remote) target));
	}

	/**
	 * @see DynamicRegistry#replaceRemote(Object)
	 */
	public synchronized Object[] replaceRemotes(Object[] arguments) {
		if (arguments != null) {
			for (int i = 0; i < arguments.length; i++) {
				// Get the reference
				arguments[i] = replaceRemote(arguments[i]);
			}
		}

		return arguments;
	}

	/**
	 * Binds the given target to this registry if it is not already bound yet.
	 * 
	 * @param target
	 *            The target you want to bind.
	 * @return a new dynamic binding or the old one.
	 */
	public synchronized DynamicBinding bindIfAbsent(Remote target) {
		if (target == null) {
			throw new NullPointerException("target");
		}

		// Get old binding...
		DynamicBinding binding = objects.get(target);

		// Is there not an old binding ?
		if (binding == null) {

			// Create new temp object binding
			binding = new DynamicBinding(getNextId(), (Remote) target);

			// Put into maps
			objects.put(target, binding);
			ids.put(binding.id(), binding);

			// Notify the target
			fireBoundTo(target);
		}

		return binding;
	}
}
