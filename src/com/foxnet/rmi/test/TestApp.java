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
package com.foxnet.rmi.test;

import java.util.Random;

import com.foxnet.rmi.AsyncVoid;
import com.foxnet.rmi.OrderedExecution;
import com.foxnet.rmi.Acceptor;
import com.foxnet.rmi.Connection;
import com.foxnet.rmi.Connector;
import com.foxnet.rmi.Remote;

/**
 * @author Christopher Probst
 */
public class TestApp {

	public static interface QuoteService extends Remote {

		/*
		 * We annotated "@OrderedExecution". This forces the server (or
		 * invocator) to invoke this method synchronously but please note that
		 * this annotation also makes sure that never two clients invoke this
		 * method concurrently, which means that it behaves like a
		 * synchronized-method.
		 */
		@AsyncVoid
		@OrderedExecution
		void requestQuote();
	}

	public static class QuoteProvider implements QuoteService {

		private static final String[] quotes = new String[] { "Boring!",
				"Doh!", "2 is greater as 1", "Hello world!" };

		private final Random random = new Random();
		private int i = 0;

		@Override
		public void requestQuote() {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws Exception {

		// Create new rmi acceptor
		Acceptor rmiAcceptor = new Acceptor(8, 16, 1024 * 1024, 128 * 1024);

		// Open new acceptor channel
		rmiAcceptor.open(1337);

		// Bind the module
		rmiAcceptor.getStaticRegistry().bind("quote_service",
				new QuoteProvider());

		// Create a new rmi connector
		Connector rmiConnector = new Connector(8, 1024 * 1024);

		// Open rmi connection
		Connection rmi = rmiConnector.open("localhost", 1337);

		// Get invoker
		// Invoker quoteInvoker = rmi.invoker("quote_service");

		QuoteService qs = (QuoteService) rmi.lookup("quote_service");

		for (int i = 1; i < 100000; i++) {

			qs.requestQuote();

			// // Get ref
			// Invocation in = quoteInvoker.invoke("requestQuote");
			//
			// // Await the result
			// in.add(new InvocationCallback() {
			//
			// @Override
			// public void completed(Invocation invocation) throws Exception {
			//
			// System.out.println(j + ". quote is \""
			// + invocation.getResponse() + "\"");
			// }
			// });
		}
	}
};
