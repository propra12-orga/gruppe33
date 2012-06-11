package propra2012.gruppe33.bomberman.io;

import java.io.Closeable;
import java.io.IOException;

import com.indyforge.foxnet.rmi.Remote;

/**
 * 
 * @author Christopher Probst
 * 
 */
public interface RemoteFile extends Remote, Closeable {

	void write(byte[] data) throws IOException;

	void write(byte[] data, int offset, int length) throws IOException;
}
