package com.indyforge.twod.engine.resources;

/**
 * 
 * @author Christopher Probst
 * 
 * @param <T>
 */
public final class SerializableResource<T> implements Resource<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final T resource;

	public SerializableResource(T resource) {
		if (resource == null) {
			throw new NullPointerException("resource");
		}
		this.resource = resource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.twod.engine.resources.Resource#get()
	 */
	@Override
	public T get() {
		return resource;
	}
}
