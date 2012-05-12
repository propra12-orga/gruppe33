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
package com.foxnet.rmi.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Christopher Probst
 */
public final class RequestManager {

	// Used to generate ids
	private final AtomicLong nextId = new AtomicLong(0);

	// Used to store the requests
	final ConcurrentMap<Long, Request> requests = new ConcurrentHashMap<Long, Request>();

	public Collection<Request> getRequests() {
		return requests.values();
	}

	public Request getRequest(long id) {
		// Try to get request
		return requests.get(id);
	}

	public void setCauseToAll(Throwable cause) {
		// Copy requests
		List<Request> remainingRequests = new ArrayList<Request>(getRequests());
		requests.clear();

		for (Request r : remainingRequests) {
			r.failed(cause);
		}
	}

	public Request addRequest() {
		return addRequest(null);
	}

	public Request addRequest(Object request) {

		// Create request object
		Request tmpRequest = new Request(this, nextId.getAndIncrement(),
				request);

		// Add
		requests.put(tmpRequest.getId(), tmpRequest);

		return tmpRequest;
	}
}
