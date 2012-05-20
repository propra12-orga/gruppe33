package propra2012.gruppe33.engine.graphics.rendering.scenegraph.filters;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class ExcludeFilter<E> implements IterationFilter<E> {

	private final E[] excluded;

	public ExcludeFilter(E... excluded) {
		this.excluded = excluded;
	}

	public E[] excluded() {
		return excluded;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see propra2012.gruppe33.engine.graphics.rendering.scenegraph.filters.
	 * IterationFilter#accept(java.lang.Object)
	 */
	@Override
	public boolean accept(E element) {
		for (E exclude : excluded) {
			if ((exclude == null && element == null)
					|| (exclude != null && exclude.equals(element))
					|| (element != null && element.equals(exclude))) {
				return false;
			}
		}
		return true;
	}
}
