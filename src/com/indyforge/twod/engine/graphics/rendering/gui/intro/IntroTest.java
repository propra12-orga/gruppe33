package com.indyforge.twod.engine.graphics.rendering.gui.intro;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.SceneProcessor;

public class IntroTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		// Start a game!
		new SceneProcessor("Gui Test", 800, 600).root(
				IntroCreator.createIntro()).start(60);
	}
}
