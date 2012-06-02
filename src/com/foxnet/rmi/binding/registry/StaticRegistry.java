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
package com.foxnet.rmi.binding.registry;

import java.util.HashMap;
import java.util.Map;

import com.foxnet.rmi.Remote;
import com.foxnet.rmi.binding.StaticBinding;

/**
 * A static registry is used to store statically created bindings.
 * 
 * @author Christopher Probst
 * @see StaticBinding
 */
public final class StaticRegistry extends Registry<StaticBinding> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Used to store the ids linked with their static binding
	private final Map<Long, StaticBinding> ids = new HashMap<Long, StaticBinding>();

	// Used to store the names linked with their static bindings
	private final Map<String, StaticBinding> names = new HashMap<String, StaticBinding>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxnet.rmi.binding.registry.AbstractRegistry#bindingMap()
	 */
	@Override
	protected Map<Long, StaticBinding> bindingMap() {
		return ids;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxnet.rmi.registry.Registry#unbind(long)
	 */
	@Override
	public synchronized StaticBinding unbind(long id) {

		// Remove from id map
		StaticBinding sb = ids.remove(id);

		if (sb != null) {
			// Remove from name map
			names.remove(sb.name());

			// Notify
			fireUnboundFrom(sb.target());
		}

		return sb;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxnet.rmi.registry.Registry#unbindAll()
	 */
	@Override
	public synchronized StaticRegistry unbindAll() {

		// Clear id map
		ids.clear();

		// Check life cycle objects
		for (StaticBinding sb : names.values()) {
			// Notify
			fireUnboundFrom(sb.target());
		}

		// Clear the names
		names.clear();

		return this;
	}

	/**
	 * @param name
	 *            The name of the static binding.
	 * @return the static binding with the given name or null.
	 */
	public synchronized StaticBinding get(String name) {
		return names.get(name);
	}

	/**
	 * Removes the binding with the given name.
	 * 
	 * @param name
	 *            The name of the binding.
	 * @return the old binding or null.
	 */
	public synchronized StaticBinding unbind(String name) {
		// Remove from name map
		StaticBinding sb = names.remove(name);

		if (sb != null) {
			// Remove from id map
			ids.remove(sb.id());

			// Notify
			fireUnboundFrom(sb.target());
		}

		return sb;
	}

	/**
	 * @return an array which contains all names.
	 */
	public synchronized String[] names() {

		// Create array
		String[] names = new String[ids.size()];

		// Copy to array
		int i = 0;
		for (StaticBinding sb : ids.values()) {
			names[i++] = sb.name();
		}

		return names;
	}

	/**
	 * Binds the given target to this registry using the given name if it is not
	 * already bound yet.
	 * 
	 * @param name
	 *            The name of the binding.
	 * @param target
	 *            The target you want to bind.
	 * @return a new static binding or the old one.
	 */
	public synchronized StaticBinding bindIfAbsent(String name, Remote target) {
		if (name == null) {
			throw new NullPointerException("name");
		}
		if (target == null) {
			throw new NullPointerException("target");
		}

		// Get old binding...
		StaticBinding binding = names.get(name);

		// Is there not an old binding ?
		if (binding == null) {

			// Create new binding
			binding = new StaticBinding(getNextId(), name, (Remote) target);

			// Put into maps
			ids.put(binding.id(), binding);
			names.put(binding.name(), binding);
			fireBoundTo(binding.target());
		}

		return binding;
	}

	/**
	 * Binds the given target to this registry using the given name. If there is
	 * already a binding with the same name the old binding will be removed.
	 * 
	 * @param name
	 *            The name of the binding.
	 * @param target
	 *            The target you want to bind.
	 * @return the old binding or null.
	 */
	public synchronized StaticBinding bind(String name, Object target) {
		if (name == null) {
			throw new NullPointerException("name");
		}
		if (target == null) {
			throw new NullPointerException("target");
		}

		// Verify target
		if (!(target instanceof Remote)) {
			throw new IllegalArgumentException("The target does not "
					+ "implement the Remote interface");
		}

		// Get old binding...
		StaticBinding binding = names.get(name);

		// Is there an old binding ?
		if (binding != null) {

			// Rebind would not make sense...
			if (binding.target() == target) {
				return binding;
			}

			// Unbind the old binding
			unbind(binding.id());
		}

		// Create new binding
		StaticBinding newBinding = new StaticBinding(getNextId(), name,
				(Remote) target);

		// Put into maps
		ids.put(newBinding.id(), newBinding);
		names.put(newBinding.name(), newBinding);
		fireBoundTo(newBinding.target());

		return binding;
	}
}
