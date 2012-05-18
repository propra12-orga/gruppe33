package propra2012.gruppe33.engine.graphics.rendering.scenegraph;

/**
 * 
 * @author Christopher Probst
 * @see EntityFilter
 * @see Entity
 */
public final class TypeFilter implements EntityFilter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
	 * @see
	 * propra2012.gruppe33.engine.graphics.rendering.scenegraph.EntityFilter
	 * #accept(propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity)
	 */
	@Override
	public int accept(Entity entity) {
		// Fill valid flag
		return ((allowExtendedClasses ? entityType.isAssignableFrom(entity
				.getClass()) : entityType.equals(entity.getClass())) ? VALID
				: 0)
				| CONTINUE;
	}
}
