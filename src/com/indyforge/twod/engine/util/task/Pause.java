package com.indyforge.twod.engine.util.task;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class Pause implements Task {

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
	 * @see com.indyforge.twod.engine.util.task.Task#update(float)
	 */
	@Override
	public boolean update(float tpf) {
		return (activeTime += tpf) >= time;
	}
}
