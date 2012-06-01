package propra2012.gruppe33.bomberman.io;

import java.io.FileOutputStream;
import java.io.IOException;

public class RFImpl implements RemoteFile {

	private final FileOutputStream fos;

	public RFImpl(FileOutputStream fos) {
		this.fos = fos;
	}

	@Override
	public void close() throws IOException {
		fos.close();
	}

	@Override
	public void write(byte[] data) throws IOException {
		fos.write(data);
	}

	public void write(byte[] data, int offset, int length) throws IOException {
		fos.write(data, offset, length);
	}
}
