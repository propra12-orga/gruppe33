package propra2012.gruppe33.graphics.assets;

import propra2012.gruppe33.graphics.rendering.util.Resource;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class Asset<T> implements Resource<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final AssetManager assetManager;
	private final String assetPath;
	private final AssetLoader<T> assetLoader;
	private transient T asset = null;

	public Asset(AssetManager assetManager, String assetPath,
			AssetLoader<T> assetLoader) {
		if (assetManager == null) {
			throw new NullPointerException("assetManager");
		} else if (assetPath == null) {
			throw new NullPointerException("assetPath");
		} else if (assetLoader == null) {
			throw new NullPointerException("assetLoader");
		}

		this.assetManager = assetManager;
		this.assetPath = assetPath;
		this.assetLoader = assetLoader;
	}

	public AssetManager getAssetManager() {
		return assetManager;
	}

	public String getAssetPath() {
		return assetPath;
	}

	public AssetLoader<T> getAssetLoader() {
		return assetLoader;
	}

	public T load() throws Exception {
		if ((asset = assetLoader.loadAsset(assetManager, assetPath)) == null) {
			throw new IllegalStateException("Loaded asset is null. "
					+ "Please check your code.");
		}
		return asset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see propra2012.gruppe33.graphics.rendering.util.Resource#get()
	 */
	@Override
	public T get() throws Exception {
		if (asset == null) {
			// Load the asset
			load();
		}

		return asset;
	}
}
