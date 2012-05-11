package propra2012.gruppe33.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class IoRoutines {

	public static byte[] serialize(Object object) throws IOException {
		ByteArrayOutputStream array = new ByteArrayOutputStream();
		ObjectOutputStream objectOutput = new ObjectOutputStream(array);
		objectOutput.writeObject(object);
		objectOutput.flush();
		objectOutput.close();
		return array.toByteArray();
	}

	public static Object deserialize(byte[] data) throws IOException,
			ClassNotFoundException {
		return new ObjectInputStream(new ByteArrayInputStream(data))
				.readObject();
	}

	private IoRoutines() {
	}
}
