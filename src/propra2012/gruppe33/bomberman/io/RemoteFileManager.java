package propra2012.gruppe33.bomberman.io;

import java.io.IOException;

import com.indyforge.foxnet.rmi.Remote;

/**
 * @author Christopher Probst
 */
public interface RemoteFileManager extends Remote {

	String hashFor(String relativePath) throws IOException;

	boolean exists(String relativePath) throws IOException;

	RemoteFile file(String relativePath) throws IOException;
}
