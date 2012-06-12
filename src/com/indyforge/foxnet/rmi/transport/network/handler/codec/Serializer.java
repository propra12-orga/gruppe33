package com.indyforge.foxnet.rmi.transport.network.handler.codec;

import static org.jboss.netty.buffer.ChannelBuffers.dynamicBuffer;

import java.io.ObjectOutputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public class Serializer extends OneToOneEncoder {
	private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

	private final int estimatedLength;
	private WrappedOutputStream wrapper;
	private ObjectOutputStream output;

	public Serializer() {
		this(512);
	}

	public Serializer(int estimatedLength) {
		if (estimatedLength < 0) {
			throw new IllegalArgumentException("estimatedLength: "
					+ estimatedLength);
		}
		this.estimatedLength = estimatedLength;
	}

	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		ChannelBufferOutputStream bout = new ChannelBufferOutputStream(
				dynamicBuffer(estimatedLength, ctx.getChannel().getConfig()
						.getBufferFactory()));
		bout.write(LENGTH_PLACEHOLDER);

		synchronized (this) {

			if (output == null) {
				output = new ObjectOutputStream(
						wrapper = new WrappedOutputStream(bout));
			} else {
				wrapper.stream(bout);
			}

			output.writeObject(msg);
			output.flush();
		}
		ChannelBuffer encoded = bout.buffer();
		int size = encoded.writerIndex() - 4;
		encoded.setInt(0, size);
		return encoded;
	}
}
