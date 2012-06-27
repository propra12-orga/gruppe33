package com.indyforge.twod.engine.graphics.rendering.gui.intro;

import java.applet.Applet;
import java.awt.Graphics;
import java.security.AccessController;
import java.security.PrivilegedAction;

import com.indyforge.twod.engine.graphics.GraphicsRoutines;
import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;

public class AppletTest extends Applet {

	private volatile SceneProcessor sp;

	@Override
	public void start() {

		AccessController.doPrivileged(new PrivilegedAction<SceneProcessor>() {
			@Override
			public SceneProcessor run() {
				try {
					return new SceneProcessor().root(IntroCreator.createIntro());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return null;
				}
			}
		});

		GraphicsRoutines.setupApplet(AppletTest.this, sp);
		setVisible(true);

		new Thread() {

			public void run() {
				try {
					sp.start(60);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}.start();

	}

	@Override
	public void destroy() {

		sp.shutdownRequest(true);

		super.destroy();
	}
}
