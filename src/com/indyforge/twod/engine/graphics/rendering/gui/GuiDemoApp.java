package com.indyforge.twod.engine.graphics.rendering.gui;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;

public class GuiDemoApp {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// Create a new scene
		SceneProcessor sceneProcessor = new SceneProcessor("Bomberman", 800,
				600);

		GuiManager guiM = new GuiManager();

		// Set root
		sceneProcessor.root(guiM.getScene());
		sceneProcessor.start(60);
		sceneProcessor.dispose();

	}

}
