package com.indyforge.foxnet.rmi.transport.network.handler.codec;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class WrappedInputStream extends FilterInputStream {

	public WrappedInputStream(InputStream in) {
		super(in);
	}

	public InputStream stream() {
		return in;
	}

	public WrappedInputStream stream(InputStream input) {
		in = input;
		return this;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return in.read(b, off, len);
	}
}
