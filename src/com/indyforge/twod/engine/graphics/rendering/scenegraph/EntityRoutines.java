package com.indyforge.twod.engine.graphics.rendering.scenegraph;

import java.util.Iterator;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class EntityRoutines {

	/**
	 * Sets the visible flag for all entities.
	 * 
	 * @param entities
	 *            The entities.
	 * @param visible
	 *            The visible flag.
	 */
	public static void visible(Iterator<? extends Entity> entities,
			boolean visible) {
		if (entities != null) {
			while (entities.hasNext()) {
				Entity next = entities.next();
				if (next instanceof GraphicsEntity) {
					((GraphicsEntity) next).visible(visible);
				}
			}
		}
	}

	private EntityRoutines() {
	}
}
