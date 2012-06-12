package com.indyforge.foxnet.rmi.transport.network.handler.codec;

import java.io.ObjectInputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;

public class Deserializer extends LengthFieldBasedFrameDecoder {

	private WrappedInputStream wrapper;
	private ObjectInputStream input;

	public Deserializer() {
		super(Integer.MAX_VALUE, 0, 4, 0, 4);
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer buffer) throws Exception {

		ChannelBuffer frame = (ChannelBuffer) super
				.decode(ctx, channel, buffer);
		if (frame == null) {
			return null;
		}

		if (input == null) {
			input = new ObjectInputStream(wrapper = new WrappedInputStream(
					new ChannelBufferInputStream(frame)));
		} else {
			wrapper.stream(new ChannelBufferInputStream(frame));
		}

		return input.readObject();
	}

	@Override
	protected ChannelBuffer extractFrame(ChannelBuffer buffer, int index,
			int length) {
		return buffer.slice(index, length);
	}
}
