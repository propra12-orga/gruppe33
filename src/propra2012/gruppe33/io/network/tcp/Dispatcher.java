package propra2012.gruppe33.io.network.tcp;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public interface Dispatcher {

	void dispatch(SelectionKey key) throws IOException;
}
