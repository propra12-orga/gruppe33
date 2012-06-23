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
 * @param <D>
 *            The destination type.
 */
public abstract class Target<D> extends EntityReachable<D, GraphicsEntity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * The transform motor.
	 */
	private final TransformMotor transformMotor;

	/**
	 * Uses the {@link TransformMotor} to transform the controlled graphics
	 * entity every frame using the given state.
	 * 
	 * @param state
	 *            The state.
	 */
	protected abstract void transform(D state);

	/**
	 * @return the state of the controlled entity.
	 */
	protected abstract D state();

	/**
	 * Create a new abstract target.
	 * 
	 * @param controlled
	 *            The controlled graphics entity.
	 * @param destination
	 *            The destination you want to reach.
	 * @param velocity
	 *            The velocity to reach the target.
	 */
	public Target(GraphicsEntity controlled, D destination, float velocity) {
		super(controlled, destination, velocity);

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
			transformMotor = ptr;
		}
	}

	/**
	 * @return the difference between the destination and the state.
	 */
	public abstract D diff();

	/**
	 * @return the transform motor.
	 */
	public TransformMotor transformMotor() {
		return transformMotor;
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
}
