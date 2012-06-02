package com.foxnet.rmi;

import java.io.Serializable;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class InvocationMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final boolean dynamic;
	private final long bindingId;
	private final int methodId;
	private final Object[] arguments;

	InvocationMessage(boolean dynamic, long bindingId, int methodId,
			Object... arguments) {
		this.dynamic = dynamic;
		this.bindingId = bindingId;
		this.methodId = methodId;
		this.arguments = arguments;
	}

	public boolean isDynamic() {
		return dynamic;
	}

	public long bindingId() {
		return bindingId;
	}

	public int methodId() {
		return methodId;
	}

	public Object[] arguments() {
		return arguments;
	}
}
