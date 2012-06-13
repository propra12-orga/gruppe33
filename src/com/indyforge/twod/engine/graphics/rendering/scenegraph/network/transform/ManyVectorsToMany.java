package com.indyforge.twod.engine.graphics.rendering.scenegraph.network.transform;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity.ManyToMany;

/**
 * 
 * @author Christopher Probst
 * 
 * @param <T>
 */
public abstract class ManyVectorsToMany<T extends Entity> extends
		ManyToMany<T, Vector2f> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
