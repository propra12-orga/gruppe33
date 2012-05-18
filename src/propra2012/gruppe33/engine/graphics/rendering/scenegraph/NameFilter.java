package propra2012.gruppe33.engine.graphics.rendering.scenegraph;

/**
 * 
 * @author Christopher Probst
 * @see EntityFilter
 * @see Entity
 */
public final class NameFilter implements EntityFilter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The name you are looking for
	private final String name;

	public NameFilter(String name) {
		if (name == null) {
			throw new NullPointerException("name");
		}
		this.name = name;
	}

	/**
	 * @return the name.
	 */
	public String getName() {
		return name;
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
		return (entity.getName().equals(name) ? VALID : 0) | CONTINUE;
	}
}
