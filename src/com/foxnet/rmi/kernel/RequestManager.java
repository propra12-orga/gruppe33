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
package com.foxnet.rmi.kernel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.foxnet.rmi.Future;
import com.foxnet.rmi.FutureCallback;

/**
 * @author Christopher Probst
 */
final class RequestManager {

	public final class Request extends Future {

		// Used to identify the request
		private volatile int id;

		// Used to store the initial request
		private final Object request;

		private void setId(int id) {
			this.id = id;
		}

		private Request(Object request) {
			this.request = request;

			// Remove this request when completed
			add(new FutureCallback() {

				@Override
				public void completed(Future future) throws Exception {
					// Remove this request
					requests.remove(id);
				}
			});
		}

		public RequestManager getRequestStorage() {
			return RequestManager.this;
		}

		public Object getRequest() {
			return request;
		}

		public int getId() {
			return id;
		}
	}

	// Used to store the requests
	private final ConcurrentMap<Integer, Request> requests = new ConcurrentHashMap<Integer, Request>();

	public Collection<Request> getRequests() {
		return requests.values();
	}

	public Request getRequest(int id) {
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

		// Create request object
		Request tmpRequest = new Request(null);

		// Get system hash code
		int hash = System.identityHashCode(tmpRequest);

		while (requests.putIfAbsent(hash, tmpRequest) != null) {
			// Increase hash
			hash++;
		}

		// Finally found a valid hash
		tmpRequest.setId(hash);

		return tmpRequest;
	}

	public Request addRequest(Object request) {

		// Get system hash code
		int hash = System.identityHashCode(request);

		// Create request object
		Request tmpRequest = new Request(request);

		while (requests.putIfAbsent(hash, tmpRequest) != null) {
			// Increase hash
			hash++;
		}

		// Finally found a valid hash
		tmpRequest.setId(hash);

		return tmpRequest;
	}
}
