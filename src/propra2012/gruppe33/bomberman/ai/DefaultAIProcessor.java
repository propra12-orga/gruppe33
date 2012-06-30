package propra2012.gruppe33.bomberman.ai;

import com.indyforge.twod.engine.graphics.rendering.scenegraph.Entity;

public class DefaultAIProcessor extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final AIControl aiControl;
	private final AIProcessor aiProcessor;

	public DefaultAIProcessor(AIControl aiControl, AIProcessor aiProcessor) {
		this.aiControl = aiControl;
		this.aiProcessor = aiProcessor;
	}

	@Override
	protected void onUpdate(float tpf) {
		super.onUpdate(tpf);

		aiProcessor.process(tpf, aiControl);
	}
}
