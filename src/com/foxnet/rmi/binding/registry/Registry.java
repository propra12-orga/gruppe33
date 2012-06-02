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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.foxnet.rmi.Remote;
import com.foxnet.rmi.binding.LocalBinding;

/**
 * This class represents a simple abstract registry of bindings.
 * 
 * @author Christopher Probst
 */
public abstract class Registry<B extends LocalBinding> implements Iterable<B>,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Used to create indeces
	private long nextId = 0;

	// Used to log infos, warnings or messages
	protected final Logger logger = Logger.getLogger(getClass().getName());

	/**
	 * Fires the bound-to event if the given target implements the
	 * {@link RegistryListener} interface.
	 * 
	 * @param target
	 *            The remote target.
	 * @return true if the given target implements the {@link RegistryListener}
	 *         interface, otherwise false.
	 */
	protected boolean fireBoundTo(Remote target) {
		if (target instanceof RegistryListener) {
			try {
				((RegistryListener) target).boundTo(this);
			} catch (Exception e) {
				logger.warning("Failed to notify the boundTo() "
						+ "method. Reason: " + e.getMessage());
			}
			return true;
		}
		return false;
	}

	/**
	 * Fires the unbound-from event if the given target implements the
	 * {@link RegistryListener} interface.
	 * 
	 * @param target
	 *            The remote target.
	 * @return true if the given target implements the {@link RegistryListener}
	 *         interface, otherwise false.
	 */
	protected boolean fireUnboundFrom(Remote target) {
		if (target instanceof RegistryListener) {
			try {
				((RegistryListener) target).unboundFrom(this);
			} catch (Exception e) {
				logger.warning("Failed to notify the unboundFrom() "
						+ "method. Reason: " + e.getMessage());
			}
			return true;
		}
		return false;
	}

	/**
	 * @return the next free id. Please note that we use increasing long ids.
	 *         This is quite safe because a long can hold very large values.
	 */
	protected long getNextId() {
		return nextId++;
	}

	/**
	 * @return the id-2-binding map.
	 */
	protected abstract Map<Long, B> bindingMap();

	/**
	 * Removes the binding with the given id.
	 * 
	 * @param id
	 *            The id of the binding you want to remove.
	 * @return the old binding or null.
	 */
	public abstract B unbind(long id);

	/**
	 * Removes all bindings.
	 * 
	 * @return this for chaining.
	 */
	public abstract Registry<B> unbindAll();

	/**
	 * @param id
	 *            The id of the binding you want to get.
	 * @return the binding with the given id or null.
	 */
	public synchronized B get(long id) {
		return bindingMap().get(id);
	}

	/**
	 * @return a snapshot of all bindings as list with random access.
	 */
	public synchronized List<B> bindings() {
		return new ArrayList<B>(bindingMap().values());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<B> iterator() {
		return bindings().iterator();
	}

	/**
	 * @return the size of the registry.
	 */
	public synchronized int size() {
		return bindingMap().size();
	}
}
