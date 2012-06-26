package com.indyforge.twod.engine.util.iteration;

/**
 * A simple type filter.
 * 
 * @author Christopher Probst
 */
public final class TypeFilter implements Filter<Object> {

	// The element type you want to find
	private final Class<?> elementType;

	// Whether or not to allow extended classes
	private final boolean allowExtendedClasses;

	public TypeFilter(Class<?> elementType) {
		this(elementType, true);
	}

	public TypeFilter(Class<?> elementType, boolean allowExtendedClasses) {
		if (elementType == null) {
			throw new NullPointerException("elementType");
		}
		this.elementType = elementType;
		this.allowExtendedClasses = allowExtendedClasses;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.util.iteration.Filter#accept(java.lang.Object)
	 */
	@Override
	public boolean accept(Object element) {
		// Fill valid flag
		return element != null ? (allowExtendedClasses ? elementType
				.isAssignableFrom(element.getClass()) : elementType
				.equals(element.getClass())) : false;
	}
}
