package propra2012.gruppe33.graphics.scenegraph;

/**
 * 
 * @author Christopher Probst
 * 
 */
public class SceneGraphObject {

	private final SceneGraph sceneGraph;
	private final String name;

	public SceneGraphObject(SceneGraph sceneGraph, String name) {
		if (sceneGraph == null) {
			throw new NullPointerException("sceneGraph");
		}
		if (name == null) {
			throw new NullPointerException("name");
		}
		this.sceneGraph = sceneGraph;
		this.name = name;
	}

	public SceneGraph getSceneGraph() {
		return sceneGraph;
	}

	public String getName() {
		return name;
	}
}
