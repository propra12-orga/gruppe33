package com.indyforge.twod.engine.graphics.rendering.scenegraph.network.transform;

import java.util.List;
import java.util.UUID;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.math.Vector2f;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity.OneToMany;

/**
 * 
 * @author Christopher Probst
 * 
 */
public abstract class OneVectorToMany<T extends Entity> extends
		OneToMany<T, Vector2f> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OneVectorToMany() {
		super();
	}

	public OneVectorToMany(List<UUID> registrationKeys) {
		super(registrationKeys);
	}
}
