package com.indyforge.twod.engine.graphics.rendering.scenegraph.network.scene;

import com.indyforge.foxnet.rmi.pattern.change.Change;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;

/**
 * Causes a network time reset of the remote.
 * 
 * @author Christopher Probst
 */
public final class ResetNetworkTimeChange implements Change<SceneProcessor>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void apply(SceneProcessor ctx) {
		// Simply reset the network time
		ctx.resetNetworkTime();
	}
}
