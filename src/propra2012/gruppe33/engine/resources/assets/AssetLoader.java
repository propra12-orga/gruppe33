package propra2012.gruppe33.engine.resources.assets;

import java.io.Serializable;

/**
 * This interface represents the connection between an asset and its asset
 * manager. Every asset has an asset loader.
 * 
 * @author Christopher Probst
 * 
 * @param <T>
 *            The asset type.
 * 
 * @see Asset
 * @see AssetBundle
 * @see AssetManager
 */
public interface AssetLoader<T> extends Serializable {

	/**
	 * Loads an asset.
	 * 
	 * @param assetManager
	 *            the asset manager.
	 * @param assetPath
	 *            The asset path.
	 * @return the loaded asset.
	 * @throws Exception
	 *             If an exception occurs.
	 */
	T loadAsset(AssetManager assetManager, String assetPath) throws Exception;
}
