package com.indyforge.twod.engine.graphics.rendering.scenegraph.network;

import com.foxnet.rmi.pattern.change.Change;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class SceneChange implements Change<SceneProcessor> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The root scene
	private final Scene scene;

	/**
	 * Create a new scene change using the given scene.
	 * 
	 * @param scene
	 *            The scene.
	 */
	public SceneChange(Scene scene) {
		this.scene = scene;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxnet.rmi.pattern.change.Change#apply(java.lang.Object)
	 */
	@Override
	public void apply(SceneProcessor ctx) {
		ctx.root(scene);
	}
}
