package com.indyforge.twod.engine.io.network.tcp.rmi.test;

import java.awt.Frame;
import java.util.Iterator;
import java.util.UUID;

import javax.swing.JOptionPane;

import propra2012.gruppe33.PreMilestoneApp;

import com.foxnet.rmi.transport.network.ConnectionManager;
import com.indyforge.twod.engine.graphics.GraphicsRoutines;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Change;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.GraphicsEntity;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.util.NameFilter;
import com.indyforge.twod.engine.io.network.tcp.rmi.impl.ServerImpl;
import com.indyforge.twod.engine.util.FilteredIterator;

public class ServerApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		ConnectionManager servers = new ConnectionManager(true);

		ServerImpl si = new ServerImpl();
		servers.staticReg().bind("server", si);
		servers.openServer(1337);

		// Create a new scene
		SceneProcessor sceneProcessor = new SceneProcessor();

		// Create peer
		Frame frame = GraphicsRoutines.createFrame(sceneProcessor,
				"Bomberman SERVER", 800, 600);

		// Set root
		sceneProcessor.root(PreMilestoneApp.createDemoGame());

		Iterator<Entity> filter = new FilteredIterator<Entity>(new NameFilter(
				"Kr0e"), sceneProcessor.root().childIterator(true, true));
		final UUID regKey = filter.next().registryKey();

		JOptionPane.showMessageDialog(frame, "Wollen Sie das Spiel starten ?");

		si.broadcastRoot(sceneProcessor.root());

		Thread.sleep(1000);
		System.out.println("Send change");

		Change change = new Change() {

			@Override
			public void change(Entity root) {
				((GraphicsEntity) root.registry().get(regKey)).position().x += 3;
			}
		};

		change.change(sceneProcessor.root());
		si.broadcastChange(change);

		while (!sceneProcessor.isShutdownRequested()) {

			// Process the world (the main game-loop)
			sceneProcessor.process(60);
		}

		// Destroy
		frame.dispose();

		// Shutdown complete servers
		servers.shudown();
	}
}
