package propra2012.gruppe33.bomberman.ai.ninja;

import propra2012.gruppe33.bomberman.ai.AIControl;
import propra2012.gruppe33.bomberman.ai.AIProcessor;

public class Bot implements AIProcessor {

	@Override
	public void process(float timeSinceLastFrame, AIControl aiControl) {
		aiControl.edgeWeight(a, b);

	}

}
