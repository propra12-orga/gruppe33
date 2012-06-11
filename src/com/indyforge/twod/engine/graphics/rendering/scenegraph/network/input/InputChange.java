package com.indyforge.twod.engine.graphics.rendering.scenegraph.network.input;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.AbstractEntityChange;

/**
 * 
 * @author Christopher Probst
 * 
 * @param <E>
 *            The enum type.
 * @param <T>
 *            The entity type.
 */
public abstract class InputChange<E extends Enum<E>, T extends Entity> extends
		AbstractEntityChange<T> {

	public static final String EVENT_NAME = "remote_input_event";
	public static final int USED_BITS = 8;

	// Here we store the input
	private Map<E, Boolean> inputMap;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.twod.engine.graphics.rendering.scenegraph.network.
	 * AbstractEntityChange
	 * #apply(com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity)
	 */
	@Override
	protected void apply(T entity) {
		entity.fireEvent(EVENT_NAME, inputMap);
	}

	public InputChange() {
	}

	public InputChange(UUID registrationKey) {
		super(registrationKey);
	}

	public InputChange(Map<E, Boolean> inputMap) {
		this.inputMap = inputMap;
	}

	public InputChange(UUID registrationKey, Map<E, Boolean> inputMap) {
		super(registrationKey);
		this.inputMap = inputMap;
	}

	public Map<E, Boolean> inputMap() {
		return inputMap;
	}

	public void inputMap(Map<E, Boolean> inputMap) {
		this.inputMap = inputMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.twod.engine.graphics.rendering.scenegraph.network.
	 * AbstractEntityChange#readExternal(java.io.ObjectInput)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		super.readExternal(in);

		// Read the input state
		int inputState = in.read();

		// Any bits set ?
		if (inputState != 0) {

			// The offset
			int offset = 0;

			// Read the enum type
			Class<E> enumType = (Class<E>) Class.forName(in.readUTF());

			// Init the map
			if (inputMap == null) {
				inputMap = new EnumMap<E, Boolean>(enumType);
			} else {
				inputMap.clear();
			}

			// Go through all enums
			for (Enum<?> input : enumType.getEnumConstants()) {

				if (offset >= USED_BITS) {
					throw new IOException("Enum type has more than "
							+ USED_BITS + " values. Please check your code.");
				} else {

					// Parse the bit set
					inputMap.put((E) input, (inputState & (1 << offset++)) != 0);
				}
			}
		} else {
			// Clear the map
			inputMap = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.indyforge.twod.engine.graphics.rendering.scenegraph.network.
	 * AbstractEntityChange#writeExternal(java.io.ObjectOutput)
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);

		// Init the input state
		int inputState = 0;
		String typeName = null;

		if (inputMap != null && !inputMap.isEmpty()) {

			// The offset
			int offset = 0;

			// Get the first enum
			E first = inputMap.keySet().iterator().next();

			// The type name
			typeName = first.getClass().getName();

			// Go through all enums
			for (Enum<?> input : first.getClass().getEnumConstants()) {

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
		out.write(inputState);

		// If the name is not null
		if (typeName != null) {
			// Write the name of the enum type
			out.writeUTF(typeName);
		}
	}
}
