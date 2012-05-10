package propra2012.gruppe33.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ObjectWriter {

	private final ByteArrayOutputStream array = new ByteArrayOutputStream();
	private final ObjectOutputStream output = new ObjectOutputStream(array);

	public ObjectWriter() throws IOException {
	}

	public ByteArrayOutputStream getArray() {
		return array;
	}

	public ObjectOutputStream getOutput() {
		return output;
	}

	public byte[] lastObject() {
		return array.toByteArray();
	}

	public byte[] put(Object obj) throws IOException {		
		output.writeObject(obj);
		output.flush();
		return lastObject();
	}
}
