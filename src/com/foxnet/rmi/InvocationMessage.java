package com.foxnet.rmi;

import java.io.Serializable;

/**
 * A simple class which holds the information about an invocation.
 * 
 * @author Christopher Probst
 */
public final class InvocationMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The dynamic flag
	private final boolean dynamic;

	// The id of the binding
	private final long bindingId;

	// The method id
	private final int methodId;

	// The arguments of the invocation
	private final Object[] arguments;

	/**
	 * Create a new invocation message using the given arguments.
	 * 
	 * @param dynamic
	 *            The dynamic flag.
	 * @param bindingId
	 *            The binding id.
	 * @param methodId
	 *            The method id.
	 * @param arguments
	 *            The arguments.
	 */
	InvocationMessage(boolean dynamic, long bindingId, int methodId,
			Object... arguments) {
		this.dynamic = dynamic;
		this.bindingId = bindingId;
		this.methodId = methodId;
		this.arguments = arguments;
	}

	/**
	 * @return the dynamic flag.
	 */
	public boolean isDynamic() {
		return dynamic;
	}

	/**
	 * @return the binding id.
	 */
	public long bindingId() {
		return bindingId;
	}

	/**
	 * @return the method id.
	 */
	public int methodId() {
		return methodId;
	}

	/**
	 * @return the arguments.
	 */
	public Object[] arguments() {
		return arguments;
	}
}
