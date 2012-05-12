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
package com.foxnet.rmi.registry;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.foxnet.rmi.LifeCycle;
import com.foxnet.rmi.Remote;
import com.foxnet.rmi.binding.LocalBinding;

/**
 * This class represents a simple abstract registry of bindings.
 * 
 * @author Christopher Probst
 */
public abstract class Registry<B extends LocalBinding> implements Iterable<B> {

	// Used to create indeces
	private long nextId = 0;

	// Used to log infos, warnings or messages
	protected final Logger logger = Logger.getLogger(getClass().getName());

	protected boolean notifyTargetBoundTo(Remote target) {
		if (target instanceof LifeCycle) {
			try {
				((LifeCycle) target).bound();
			} catch (Exception e) {
				logger.warning("Failed to notify the bound() "
						+ "method. Reason: " + e.getMessage());
			}
			return true;
		}
		return false;
	}

	protected boolean notifyTargetUnboundFrom(Remote target) {
		if (target instanceof LifeCycle) {
			try {
				((LifeCycle) target).unbound();
			} catch (Exception e) {
				logger.warning("Failed to notify the unbound() "
						+ "method. Reason: " + e.getMessage());
			}
			return true;
		}
		return false;
	}

	protected long getNextId() {
		return nextId++;
	}

	protected abstract Map<Long, B> getIndexMap();

	public abstract B unbind(long id);

	public abstract void unbindAll();

	public synchronized B get(long id) {
		return getIndexMap().get(id);
	}

	public synchronized Set<B> getBindings() {
		return new AbstractSet<B>() {

			// Copy to array list
			private final List<B> bindings = new ArrayList<B>(getIndexMap()
					.values());

			@Override
			public Iterator<B> iterator() {
				return bindings.iterator();
			}

			@Override
			public int size() {
				return bindings.size();
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<B> iterator() {
		return getBindings().iterator();
	}

	public synchronized int size() {
		return getIndexMap().size();
	}
}
