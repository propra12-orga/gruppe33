package com.indyforge.twod.engine.graphics.rendering.scenegraph;

import java.io.Serializable;

/**
 * 
 * @author Christopher Probst
 * 
 */
public interface Change extends Serializable {

	void apply(Entity root);
}
