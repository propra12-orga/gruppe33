package propra2012.gruppe33.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class ObjectReader extends ByteArrayInputStream {

	private ObjectInputStream input;

	public ObjectReader() throws IOException {
		super(new byte[0]);
		count = 0;
	}

	public ObjectInputStream getInput() {
		return input;
	}

	public Object take(byte[] data) throws IOException, ClassNotFoundException {
		buf = data;
		count = data.length;
		pos = mark = 0;

		if (input == null) {
			input = new ObjectInputStream(this);
		}

		return input.readObject();
	}
}
