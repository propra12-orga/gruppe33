package propra2012.gruppe33.engine.graphics.rendering.scenegraph.iterators;

import propra2012.gruppe33.engine.graphics.rendering.scenegraph.Entity;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class SiblingIterator extends PeerIterator<Entity> {

	public SiblingIterator(Entity root, boolean includeRootParent) {
		super(root.parent() != null ? new ChildIterator(root.parent(),
				includeRootParent) : new EmptyIterator<Entity>());
	}
}
