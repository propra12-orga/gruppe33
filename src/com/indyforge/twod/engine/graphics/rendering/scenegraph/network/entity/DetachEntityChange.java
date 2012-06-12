package com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.indyforge.foxnet.rmi.pattern.change.Change;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class DetachEntityChange implements Change<SceneProcessor> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public List<UUID> list = new ArrayList<UUID>();

	@Override
	public void apply(SceneProcessor ctx) {

		for (UUID reg : list) {
			Entity e = ctx.root().registry().get(reg);

			if (e != null) {
				e.detach();
			}
		}
	}
}
