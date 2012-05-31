package propra2012.gruppe33.engine.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;

/**
 * Provides some simple I/O routines.
 * 
 * @author Christopher Probst
 */
public final class IoRoutines {

	/*
	 * Used for faster byte to hex conversion.
	 */
	private static final String HEX_RANGE = "0123456789ABCDEF";

	/**
	 * This method reads the complete input stream and stores the data in an
	 * array.
	 * 
	 * @param inputStream
	 *            The input stream you want to read completely.
	 * @return an array which contains the binary data.
	 * @throws IOException
	 *             If an I/O exception occurs.
	 */
	public static byte[] readFully(InputStream inputStream) throws IOException {
		ByteArrayOutputStream array = new ByteArrayOutputStream();
		byte[] buffer = new byte[0xFFFF];
		int read;

		while ((read = inputStream.read(buffer)) != -1) {
			array.write(buffer, 0, read);
		}
		return array.toByteArray();
	}

	/**
	 * Simple method to convert binary data to hex.
	 * 
	 * @param data
	 *            The binary data you want to represent as hex.
	 * @return the hex string.
	 */
	public static String toHex(byte[] data) {
		if (data == null) {
			throw new NullPointerException("data");
		}

		// Use a fast string builder for this purpose
		StringBuilder stringBuilder = new StringBuilder(2 * data.length);

		// Iterate over all bytes
		for (byte b : data) {
			// Bit shifting and &-operator do all the magic here...
			stringBuilder.append(HEX_RANGE.charAt((b & 0xF0) >> 4)).append(
					HEX_RANGE.charAt((b & 0x0F)));
		}

		// Represent the final string
		return stringBuilder.toString();
	}

	/**
	 * Calculates the string hash for a given input stream.
	 * 
	 * @param inputStream
	 *            The input stream.
	 * @return the hex string.
	 * @throws Exception
	 *             If an exception occurs.
	 */
	public static String calcHexHash(InputStream inputStream) throws Exception {
		return toHex(calcHash(inputStream));
	}

	/**
	 * Calculates the binary hash for a given input stream.
	 * 
	 * @param inputStream
	 *            The input stream.
	 * @return the binary hash.
	 * @throws Exception
	 *             If an exception occurs.
	 */
	public static byte[] calcHash(InputStream inputStream) throws Exception {
		if (inputStream == null) {
			throw new NullPointerException("inputStream");
		}

		try {
			// Use SHA-1 for hashing
			MessageDigest digest = MessageDigest.getInstance("SHA-1");

			// 0xFFFF should increase calculation
			byte[] buffer = new byte[0xFFFF];

			// Tmp
			int read;

			// Read & update as long there are remaining bytes
			while ((read = inputStream.read(buffer)) != -1) {
				digest.update(buffer, 0, read);
			}

			// Return the hash
			return digest.digest();
		} finally {
			inputStream.close();
		}
	}

	/**
	 * Serializes the given object.
	 * 
	 * @param object
	 *            The object you want to serialize.
	 * @return an array which contains the binary data.
	 * @throws IOException
	 *             If an I/O exception occurs.
	 */
	public static byte[] serialize(Object object) throws IOException {
		ByteArrayOutputStream array = new ByteArrayOutputStream();
		ObjectOutputStream objectOutput = new ObjectOutputStream(array);
		objectOutput.writeObject(object);
		objectOutput.flush();
		objectOutput.close();
		return array.toByteArray();
	}

	/**
	 * Deserializes the given array.
	 * 
	 * @param data
	 *            The binary data you want to deserialize. You should use
	 *            {@link IoRoutines#serialize(Object)} to get such an array.
	 * @return the deserialized object.
	 * @throws IOException
	 *             If an I/O exception occurs.
	 * @throws ClassNotFoundException
	 *             If a class could not be loaded.
	 */
	public static Object deserialize(byte[] data) throws IOException,
			ClassNotFoundException {
		return new ObjectInputStream(new ByteArrayInputStream(data))
				.readObject();
	}

	// Should not be instantiated
	private IoRoutines() {
	}
}
