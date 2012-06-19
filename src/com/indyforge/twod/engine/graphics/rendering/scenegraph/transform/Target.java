package com.indyforge.twod.engine.graphics.rendering.scenegraph.transform;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.util.FilteredIterator;
import com.indyforge.twod.engine.util.IterationRoutines;
import com.indyforge.twod.engine.util.TypeFilter;

/**
 * Represents an abstract target. A target is basically something which can be
 * reached by transforming the controlled entity. This class uses the
 * {@link TransformMotor} to apply transformations.
 * 
 * @author Christopher Probst
 * 
 * @param <T>
 *            The target type.
 */
public abstract class Target<T> implements Reachable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * The controlled entity.
	 */
	private final GraphicsEntity controlled;

	/*
	 * The transform motor.
	 */
	private final TransformMotor transformMotor;

	/*
	 * The target.
	 */
	private final T target;

	/*
	 * The veloctiy which is used to reach the target.
	 */
	private final float veloctiy;

	/*
	 * Should target include the parent state ?
	 */
	private final boolean includeParentState;

	/*
	 * The reached flag.
	 */
	private boolean reached = false;

	/**
	 * @return the state of the controlled graphics entity.
	 */
	protected abstract T state();

	/**
	 * Adds the given state to the controlled graphics entity state.
	 * 
	 * @param state
	 *            The state you want to add.
	 */
	protected abstract void addToState(T state);

	/**
	 * Uses the {@link TransformMotor} to transform the controlled graphics
	 * entity every frame using the given state.
	 * 
	 * @param state
	 *            The state.
	 */
	protected abstract void transform(T state);

	/**
	 * @see Target#reach(float)
	 */
	protected abstract boolean reachTarget(float tpf);

	/**
	 * Create a new abstract target.
	 * 
	 * @param controlled
	 *            The controlled graphics entity.
	 * @param target
	 *            The target you want to reach.
	 * @param velocity
	 *            The velocity to reach the target.
	 * @param includeParentState
	 *            The include-parent-state flag.
	 */
	public Target(GraphicsEntity controlled, T target, float velocity,
			boolean includeParentState) {
		if (controlled == null) {
			throw new NullPointerException("controlled");
		} else if (target == null) {
			throw new NullPointerException("target");
		}

		// Lookup motor
		TransformMotor ptr = (TransformMotor) IterationRoutines
				.next(new FilteredIterator<Entity>(new TypeFilter(
						TransformMotor.class, true), controlled.childIterator(
						false, false)));

		if (ptr == null) {
			throw new IllegalArgumentException("The controlled entity does "
					+ "not have a transform motor as child");
		} else {
			// Save the parameters
			this.controlled = controlled;
			transformMotor = ptr;
			this.target = target;
			this.veloctiy = Math.abs(velocity);
			this.includeParentState = includeParentState;
		}
	}

	/**
	 * @return the difference between the target and the state.
	 */
	public abstract T diff();

	/**
	 * @return the controlled entity.
	 */
	public GraphicsEntity controlled() {
		return controlled;
	}

	/**
	 * @return the transform motor.
	 */
	public TransformMotor transformMotor() {
		return transformMotor;
	}

	/**
	 * @return the target.
	 */
	public T target() {
		return target;
	}

	/**
	 * @return the veloctiy.
	 */
	public float veloctiy() {
		return veloctiy;
	}

	/**
	 * @return the include-parent-state flag.
	 */
	public boolean includesParentState() {
		return includeParentState;
	}

	/**
	 * @return the true if the target is reached, otherwise false.
	 */
	public boolean isReached() {
		return reached;
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
		transform(null);
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
		return reached = reachTarget(tpf);
	}
}
