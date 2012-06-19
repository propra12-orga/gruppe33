package com.indyforge.twod.engine.graphics.rendering.scenegraph.transform;

public class Pause implements Reachable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final float time;
	private float activeTime = 0;

	public Pause(float time) {
		this.time = time;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.transform.Reachable
	 * #reach(float)
	 */
	@Override
	public boolean reach(float tpf) {
		return (activeTime += tpf) >= time;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indyforge.twod.engine.graphics.rendering.scenegraph.transform.Reachable
	 * #cancel()
	 */
	@Override
	public void cancel() {
		// Do nothing...
	}
}
