package com.indyforge.twod.engine.graphics.rendering.scenegraph.network.sound;

import com.indyforge.foxnet.rmi.pattern.change.Change;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class PlaySound implements Change<SceneProcessor> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * The sound name.
	 */
	private String soundName;

	public PlaySound() {
		this(null);
	}

	public PlaySound(String soundName) {
		this.soundName = soundName;
	}

	public String soundName() {
		return soundName;
	}

	public PlaySound soundName(String soundName) {
		this.soundName = soundName;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.foxnet.rmi.pattern.change.Change#apply(java.lang.Object)
	 */
	@Override
	public void apply(SceneProcessor ctx) {
		if (ctx.root() != null) {
			ctx.root().soundManager().playSound(soundName, true);
		}
	}
}
