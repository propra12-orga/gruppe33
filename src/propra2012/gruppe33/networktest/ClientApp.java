package propra2012.gruppe33.networktest;

import com.indyforge.foxnet.rmi.InvokerManager;
import com.indyforge.foxnet.rmi.pattern.change.Session;
import com.indyforge.foxnet.rmi.util.Future;
import com.indyforge.foxnet.rmi.util.FutureCallback;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor.NetworkMode;

public class ClientApp {

	public static void main(String[] args) throws Exception {

		// Create a new scene
		final SceneProcessor sceneProcessor = new SceneProcessor(
				NetworkMode.Client, "Bomberman", 640, 1024);

		// Stop rendering if hidden...
		sceneProcessor.onlyRenderWithFocus(false);

		// Connect the scene
		Session<SceneProcessor> session = sceneProcessor.openClient(
				"localhost", 1337).linkClient("Kr0e");

		// Get the invoker manager
		InvokerManager man = InvokerManager.of(session);

		man.closeFuture().add(new FutureCallback() {

			@Override
			public void completed(Future future) throws Exception {

				sceneProcessor.shutdownRequest(true);
			}
		});

		// Start the processor (In this thread!)
		sceneProcessor.start(60);
	}
}
