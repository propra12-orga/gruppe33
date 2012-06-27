package com.indyforge.twod.engine.graphics.rendering.scenegraph.network.input;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EnumMap;
import java.util.Map;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.entity.Many;

/**
 * 
 * This method represents an enum input change. To receive the fired events you
 * must register the {@link InputChange} class object at {@link Entity#events()}
 * .
 * 
 * @author Christopher Probst
 * 
 * @param <E>
 *            The enum type.
 * @param <T>
 *            The entity type.
 */
public abstract class InputChange<E extends Enum<E>, T extends Entity> extends
		Many<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Represents the maximal number of parallel input states.
	 */
	public static final int USED_BITS = 8;

	// The enum type
	private final Class<E> enumType;

	// Here we store the input
	private Map<E, Boolean> inputMap;

	private void writeObject(ObjectOutputStream s) throws IOException {
		s.defaultWriteObject();

		// Init the input state
		int inputState = 0;

		if (!inputMap.isEmpty()) {

			// The offset
			int offset = 0;

			// Go through all enums
			for (Enum<?> input : enumType.getEnumConstants()) {

				if (offset >= USED_BITS) {
					throw new IOException("Enum type has more than "
							+ USED_BITS + " values. Please check your code.");
				} else {
					// Save to exists flag
					Boolean exists = inputMap.get(input);

					// Modify the bit set
					inputState |= (exists != null ? exists : false) ? 1 << offset
							: 0;

					// Inc
					offset++;
				}
			}
		}

		// Write as byte
		s.write(inputState);
	}

	private void readObject(ObjectInputStream s) throws IOException,
			ClassNotFoundException {
		s.defaultReadObject();

		// Recreate enm map
		inputMap = new EnumMap<E, Boolean>(enumType);

		// Read the input state
		int inputState = s.read();

		// The offset
		int offset = 0;

		// Go through all enums
		for (E input : enumType.getEnumConstants()) {

			if (offset >= USED_BITS) {
				throw new IOException("Enum type has more than " + USED_BITS
						+ " values. Please check your code.");
			} else {

				// Parse the bit set
				inputMap.put(input,
						inputState != 0 ? (inputState & (1 << offset++)) != 0
								: false);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.twod.engine.graphics.rendering.scenegraph.network.
	 * Many
	 * #apply(com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity)
	 */
	@Override
	protected void apply(T entity) {
		entity.fireEvent(InputChange.class, inputMap);
	}

	public InputChange(Class<E> enumType) {
		if (enumType == null) {
			throw new NullPointerException("enumType");
		}
		inputMap = new EnumMap<E, Boolean>(this.enumType = enumType);
	}

	public Class<E> enumType() {
		return enumType;
	}

	public Map<E, Boolean> inputMap() {
		return inputMap;
	}
}
