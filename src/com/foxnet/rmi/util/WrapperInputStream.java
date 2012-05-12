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

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author Christopher Probst
 * 
 * @param <T>
 */
public final class WrapperInputStream<T extends InputStream> extends
		InputStream {

	private T input;

	public WrapperInputStream() {
		this(null);
	}

	public WrapperInputStream(T input) {
		setInput(input);
	}

	public T setInput(T input) {
		T oldInput = this.input;
		this.input = input;
		return oldInput;
	}

	public T getInput() {
		return input;
	}

	@Override
	public int read() throws IOException {
		return input.read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return input.read(b);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return input.read(b, off, len);
	}

	@Override
	public void close() throws IOException {
		input.close();
	}

	@Override
	public long skip(long n) throws IOException {
		return input.skip(n);
	}

	@Override
	public int available() throws IOException {
		return input.available();
	}

	@Override
	public void mark(int readlimit) {
		input.mark(readlimit);
	}

	@Override
	public boolean markSupported() {
		return input.markSupported();
	}

	@Override
	public void reset() throws IOException {
		input.reset();
	}
}
