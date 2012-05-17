package propra2012.gruppe33.engine.resources.assets;

import java.io.IOException;
import java.io.ObjectInputStream;

import propra2012.gruppe33.engine.resources.Resource;

/**
 * The class represents an asset resource. It must be created by an asset
 * manager. An asset is of course serializable but it serializes only the asset
 * path to an asset instead of its binary representation. This implicates that
 * the binary asset bundle must already exist on the target platform to reload
 * this asset.
 * 
 * @author Christopher Probst
 * @param <T>
 *            The asset type.
 * 
 * @see AssetBundle
 * @see AssetLoader
 * @see AssetManager
 */
public final class Asset<T> implements Resource<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * The asset manager of this asset which created this asset.
	 */
	private final AssetManager assetManager;

	/*
	 * The asset path of this asset.
	 */
	private final String assetPath;

	/*
	 * The asset loader of this asset.
	 */
	private final AssetLoader<T> assetLoader;

	/*
	 * The transient asset. This must be reloaded after deserialization.
	 */
	private transient T asset;

	/*
	 * When deserializing we want to reload the resource from the asset bundle.
	 */
	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {

		// Restore all vars
		in.defaultReadObject();

		try {
			// Try to load the asset
			asset = load();
		} catch (Exception e) {
			throw new IOException("Failed to load asset", e);
		}
	}

	/**
	 * Loads the asset from the asset bundle.
	 * 
	 * @return the loaded asset.
	 * @throws Exception
	 *             If an exception occurs.
	 */
	private T load() throws Exception {
		T tmpAsset;
		if ((tmpAsset = assetLoader.loadAsset(assetManager, assetPath)) == null) {
			throw new IllegalStateException("Loaded asset is null. "
					+ "Please check your code.");
		}
		return tmpAsset;
	}

	/**
	 * Creates a new asset using the given parameters.
	 * 
	 * @param assetManager
	 *            The asset manager which created this asset.
	 * @param assetPath
	 *            The asset path of the asset.
	 * @param assetLoader
	 *            The asset loader which loads the asset.
	 */
	Asset(AssetManager assetManager, String assetPath,
			AssetLoader<T> assetLoader) throws Exception {
		if (assetManager == null) {
			throw new NullPointerException("assetManager");
		} else if (assetPath == null) {
			throw new NullPointerException("assetPath");
		} else if (assetLoader == null) {
			throw new NullPointerException("assetLoader");
		}

		// Save the parameters
		this.assetManager = assetManager;
		this.assetPath = assetPath;
		this.assetLoader = assetLoader;

		// Try to load the asset
		asset = load();
	}

	/**
	 * @return the asset manager.
	 */
	public AssetManager getAssetManager() {
		return assetManager;
	}

	/**
	 * @return the asset path.
	 */
	public String getAssetPath() {
		return assetPath;
	}

	/**
	 * @return the asset loader.
	 */
	public AssetLoader<T> getAssetLoader() {
		return assetLoader;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see propra2012.gruppe33.graphics.rendering.util.Resource#get()
	 */
	@Override
	public T get() {
		return asset;
	}
}
