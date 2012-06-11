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
package com.indyforge.foxnet.rmi.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class Request extends Future {

	// The INVALID id
	public static final long INVALID_ID = 0;

	// Used to create ids
	public static final AtomicLong ID_GENERATOR = new AtomicLong(INVALID_ID + 1);

	// Used to identify the request
	private final long id;

	// Used to store the initial data
	private final Object data;

	/**
	 * Creates a new request using the given data and a generated unique id.
	 * 
	 * @param data
	 *            The data of this request.
	 */
	public Request(Object data) {
		this(data, ID_GENERATOR.getAndIncrement());
	}

	/**
	 * Creates a new request using the given arguments.
	 * 
	 * @param data
	 *            The data of this request.
	 * @param id
	 *            The id of this request.
	 */
	public Request(Object data, long id) {

		if (id == INVALID_ID) {
			throw new IllegalArgumentException("The id is invalid");
		}

		// The id of this request
		this.id = id;

		// Save the initial data
		this.data = data;
	}

	/**
	 * @return the data of this request.
	 */
	public Object data() {
		return data;
	}

	/**
	 * @return the id of this request.
	 */
	public long id() {
		return id;
	}
}