package propra2012.gruppe33.engine.graphics.rendering.scenegraph.util;

import java.util.Iterator;

import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;

/**
 * An abstract entity iterator implementation which detaches an entity when
 * removing.
 * 
 * @author Christopher Probst
 */
public abstract class AbstractEntityIterator implements Iterator<Entity> {

	// The remove pointer
	protected Entity removePtr = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		// Remove by detaching
		if (removePtr != null) {
			removePtr.detach();
			removePtr = null;
		}
	}
}
