package com.indyforge.foxnet.rmi.transport.network.handler.codec;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class WrappedOutputStream extends FilterOutputStream {

	public WrappedOutputStream(OutputStream out) {
		super(out);
	}

	public OutputStream stream() {
		return out;
	}

	public WrappedOutputStream stream(OutputStream output) {
		out = output;
		return this;
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
	}
}
