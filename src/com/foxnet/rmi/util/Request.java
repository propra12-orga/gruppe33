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

import com.foxnet.rmi.Future;
import com.foxnet.rmi.FutureCallback;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class Request extends Future {

	// The request manager of this request
	private final RequestManager requestManager;

	// Used to identify the request
	private final long id;

	// Used to store the initial request
	private final Object request;

	Request(RequestManager requestManager, long id, Object request) {

		if (requestManager == null) {
			throw new NullPointerException("requestManager");
		}

		// Save the request manager
		this.requestManager = requestManager;

		// The id of this request
		this.id = id;

		// Save the initial request
		this.request = request;

		// Remove this request when completed
		add(new FutureCallback() {

			@Override
			public void completed(Future future) throws Exception {
				// Remove this request
				getRequestManager().requests.remove(getId());
			}
		});
	}

	public RequestManager getRequestManager() {
		return requestManager;
	}

	public Object getRequest() {
		return request;
	}

	public long getId() {
		return id;
	}
}