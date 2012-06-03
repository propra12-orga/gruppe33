package com.indyforge.twod.engine.io.network.tcp.rmi.test;

import java.awt.Frame;

import com.foxnet.rmi.InvokerManager;
import com.foxnet.rmi.transport.network.ConnectionManager;
import com.indyforge.twod.engine.graphics.GraphicsRoutines;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Change;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;
import com.indyforge.twod.engine.io.network.tcp.rmi.Server;
import com.indyforge.twod.engine.io.network.tcp.rmi.Session;
import com.indyforge.twod.engine.io.network.tcp.rmi.SessionCallback;

public class ClientApp {
	public static volatile Session session;
	public static volatile Scene scene;

	public static void main(String[] args) throws Exception {

		ConnectionManager clients = new ConnectionManager(false);

		InvokerManager client = clients.openClient("localhost", 1337);
		Server server = (Server) client.lookupProxy("server");

		SessionCallback callback = new SessionCallback() {

			@Override
			public void processChange(final Change change) {
				scene.processor().tasks().offer(new Runnable() {

					@Override
					public void run() {
						change.change(scene);
					}
				});
			}

			@Override
			public void root(final Scene root) throws Exception {
				System.out.println("world received");
				scene = root;
				new Thread() {
					public void run() {

						try {
							// ***************************************************
							// Create a new scene
							SceneProcessor sceneProcessor = new SceneProcessor();

							// Create peer
							Frame frame = GraphicsRoutines.createFrame(
									sceneProcessor, "Bomberman CLIENT", 800,
									600);

							sceneProcessor.root(root);

							// Iterator<Entity> kr0e = new
							// FilteredIterator<Entity>(
							// new NameFilter("Kr0e"), root.childIterator(
							// true, true));
							// Entity kEnt = kr0e.next();
							//
							//
							//
							// Map<Direction, Boolean> inputMap = new
							// EnumMap<Direction, Boolean>(
							// Direction.class);
							//
							// long last = System.currentTimeMillis();
							// if (System.currentTimeMillis() - last > 40) {
							//
							// inputMap.put(Direction.North,
							// root.isPressed(KeyEvent.VK_UP));
							// inputMap.put(Direction.South,
							// root.isPressed(KeyEvent.VK_DOWN));
							// inputMap.put(Direction.West,
							// root.isPressed(KeyEvent.VK_LEFT));
							// inputMap.put(Direction.East,
							// root.isPressed(KeyEvent.VK_RIGHT));
							//
							// session.sendToAll(inputMap);
							//
							// last = System.currentTimeMillis();
							// }
							while (!sceneProcessor.isShutdownRequested()) {

								// Process the world (the main game-loop)
								sceneProcessor.process(60);
							}

							// Destroy
							frame.dispose();
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							System.exit(1);
						}
					}

				}.start();
			}

			@Override
			public void onMessage(String origin, Object message) {
				System.out.println("msg from " + origin + ":" + message);
			}
		};
		session = server.openSession(callback, "Kr0e");

		session.sendToAll("Hello: " + session.callback());
	}
}
