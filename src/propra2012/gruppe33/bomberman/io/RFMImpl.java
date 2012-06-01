package propra2012.gruppe33.bomberman.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.indyforge.twod.engine.io.IoRoutines;

public class RFMImpl implements RemoteFileManager {

	@Override
	public String hashFor(String relativePath) throws IOException {
		try {
			return IoRoutines.calcHexHash(new FileInputStream(new File(
					relativePath)));
		} catch (Exception e) {
			throw new IOException("error", e);
		}
	}

	@Override
	public boolean exists(String relativePath) throws IOException {
		return new File(relativePath).exists();
	}

	@Override
	public RemoteFile file(String relativePath) throws IOException {
		File f = new File(relativePath);
		if (!f.exists()) {
			f.getParentFile().mkdirs();
		}
		return new RFImpl(new FileOutputStream(f));
	}
}
