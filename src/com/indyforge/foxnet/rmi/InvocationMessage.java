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
package com.indyforge.foxnet.rmi;

import java.io.Serializable;

/**
 * A simple class which holds the information about an invocation.
 * 
 * @author Christopher Probst
 */
public final class InvocationMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The dynamic flag
	private final boolean dynamic;

	// The id of the binding
	private final long bindingId;

	// The method id
	private final int methodId;

	// The arguments of the invocation
	private final Object[] arguments;

	/**
	 * Create a new invocation message using the given arguments.
	 * 
	 * @param dynamic
	 *            The dynamic flag.
	 * @param bindingId
	 *            The binding id.
	 * @param methodId
	 *            The method id.
	 * @param arguments
	 *            The arguments.
	 */
	InvocationMessage(boolean dynamic, long bindingId, int methodId,
			Object... arguments) {
		this.dynamic = dynamic;
		this.bindingId = bindingId;
		this.methodId = methodId;
		this.arguments = arguments;
	}

	/**
	 * @return the dynamic flag.
	 */
	public boolean isDynamic() {
		return dynamic;
	}

	/**
	 * @return the binding id.
	 */
	public long bindingId() {
		return bindingId;
	}

	/**
	 * @return the method id.
	 */
	public int methodId() {
		return methodId;
	}

	/**
	 * @return the arguments.
	 */
	public Object[] arguments() {
		return arguments;
	}
}
