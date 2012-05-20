package propra2012.gruppe33.engine.graphics.rendering.scenegraph.filters;

import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;
import propra2012.gruppe33.engine.graphics.rendering.scenegraph.EntityFilter;

/**
 * @author Christopher Probst
 * 
 */
public final class TypeFilter implements EntityFilter {

	// The entity type you want to find
	private final Class<? extends Entity> entityType;

	// Whether or not to allow extended classes
	private final boolean allowExtendedClasses;

	public TypeFilter(Class<? extends Entity> entityType,
			boolean allowExtendedClasses) {
		if (entityType == null) {
			throw new NullPointerException("entityType");
		}
		this.entityType = entityType;
		this.allowExtendedClasses = allowExtendedClasses;
	}

	/**
	 * 
	 * @return the entity type you want to find.
	 */
	public Class<? extends Entity> getEntityType() {
		return entityType;
	}

	/**
	 * 
	 * @return the allow-extended-classes flag.
	 */
	public boolean isAllowExtendedClasses() {
		return allowExtendedClasses;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see propra2012.gruppe33.engine.graphics.rendering.scenegraph.filters.
	 * IterationFilter#accept(java.lang.Object)
	 */
	@Override
	public boolean accept(Entity entity) {
		// Fill valid flag
		return entity != null ? (allowExtendedClasses ? entityType
				.isAssignableFrom(entity.getClass()) : entityType.equals(entity
				.getClass())) : false;
	}
}
