package propra2012.gruppe33.networktest;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import propra2012.gruppe33.PreMilestoneApp;
import propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid.InputUploader;

import com.foxnet.rmi.pattern.change.AdminSessionServer;
import com.foxnet.rmi.pattern.change.Change;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Scene;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor.NetworkMode;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.ResetNetworkTimeChange;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.network.SceneChange;
import com.indyforge.twod.engine.util.IterationRoutines;
import com.indyforge.twod.engine.util.TypeFilter;

public class ServerApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		// Create a new scene as server
		SceneProcessor serverProcessor = new SceneProcessor(NetworkMode.Server)
				.openServer(1337);

		// Scanner from console
		Scanner input = new Scanner(System.in);

		// Wait for two players
		while (true) {
			System.out.println("Started ?");
			input.next();

			synchronized (serverProcessor.adminSessionServer()) {
				if (serverProcessor.adminSessionServer().sessionCount() != 2) {
					System.out.println("Session count != 2");

				} else {
					serverProcessor.adminSessionServer().acceptingSessions(
							false);
					break;
				}
			}
		}

		// Scene sceneB = PreMilestoneApp.createDemoGame(longs);
		//
		// Entity eA = IterationRoutines.filterNext(new CompositeFilter<Entity>(
		// new TypeFilter(InputUploader.class, true), new EntityFilter() {
		//
		// @Override
		// public boolean accept(Entity element) {
		// return element.hasParent()
		// && element.parent().name().equals(longs[0]);
		// }
		// }), sceneA.childIterator(true, true));
		//
		// ((InputUploader) eA).active = true;
		//
		//
		// Entity eB = IterationRoutines.filterNext(new CompositeFilter<Entity>(
		// new TypeFilter(InputUploader.class, true), new EntityFilter() {
		//
		// @Override
		// public boolean accept(Entity element) {
		// return element.hasParent()
		// && element.parent().name().equals(longs[0]);
		// }
		// }), sceneA.childIterator(true, true));
		//
		// ((InputUploader) eA).active = true;

		final long[] longs = new long[serverProcessor.adminSessionServer()
				.sessionCount()];
		int i = 0;
		for (long l : serverProcessor.adminSessionServer().sessions().keySet()) {
			longs[i++] = l;
		}

		final List<UUID> refs = new LinkedList<UUID>();
		Scene scene = PreMilestoneApp.createDemoGame(refs, longs);
		System.out.println(refs);

		AdminSessionServer<SceneProcessor> server = serverProcessor
				.adminSessionServer();

		Change<SceneProcessor> a = new Change<SceneProcessor>() {
			@Override
			public void apply(SceneProcessor ctx) {
				Entity e = ctx.root().registry().get(refs.get(0));
				InputUploader iu = (InputUploader) IterationRoutines
						.filterNext(new TypeFilter(InputUploader.class, true),
								e.childIterator(true, true));

				iu.active = true;
			}
		};

		Change<SceneProcessor> b = new Change<SceneProcessor>() {
			@Override
			public void apply(SceneProcessor ctx) {
				Entity e = ctx.root().registry().get(refs.get(1));
				InputUploader iu = (InputUploader) IterationRoutines
						.filterNext(new TypeFilter(InputUploader.class, true),
								e.childIterator(true, true));

				iu.active = true;
			}
		};

		// Apply the scene change
		server.combined().applyChange(new SceneChange(scene));

		server.sessions().get(longs[0]).client().applyChange(a);
		server.sessions().get(longs[1]).client().applyChange(b);

		System.out.println("Send change");

		Thread.sleep(1000);

		// Reset the network time every where
		server.combined().applyChange(new ResetNetworkTimeChange());

		// Start the processor
		serverProcessor.start(60);
	}
}
