package propra2012.gruppe33.engine.resources.assets;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import propra2012.gruppe33.engine.graphics.GraphicsRoutines;

/**
 * This class simplifies the handling of resources. Let it be images, maps,
 * sounds or something else. An asset manager basically creates assets which
 * represent data from an archive.
 * 
 * An asset manager and all its components are serializable to heavily simplify
 * the networking part. When you serialize assets you really just serialize its
 * paths. The deserializing component must have the same asset bundles to
 * restore the native assets. This concept is the best attempt to simplify the
 * exchange while maintaining consistency.
 * 
 * @author Christopher Probst
 * @author Matthias Hesse
 * @see Asset
 * @see AssetBundle
 * @see AssetLoader
 */
public final class AssetManager implements Serializable {

	/*
	 * The stream loader.
	 */
	private static final AssetLoader<InputStream> STREAM_LOADER = new AssetLoader<InputStream>() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public InputStream loadAsset(AssetManager assetManager, String assetPath)
				throws Exception {
			return assetManager.open(assetPath);
		}
	};

	/*
	 * The image loader.
	 */
	private static final AssetLoader<BufferedImage> IMAGE_LOADER = new AssetLoader<BufferedImage>() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public BufferedImage loadAsset(AssetManager assetManager,
				String assetPath) throws Exception {
			return ImageIO.read(assetManager.open(assetPath));
		}
	};

	/*
	 * The optimized image loader.
	 */
	private static final AssetLoader<BufferedImage> OPTIMIZED_IMAGE_LOADER = new AssetLoader<BufferedImage>() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public BufferedImage loadAsset(AssetManager assetManager,
				String assetPath) throws Exception {
			return GraphicsRoutines.optimizeImage(IMAGE_LOADER.loadAsset(
					assetManager, assetPath));
		}
	};

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * Here we store all asset bundles.
	 */
	private final Map<File, AssetBundle> assetBundles;

	/*
	 * Here we store all asset bundle hashes.
	 */
	private final Map<File, String> assetBundleHashes;

	/*
	 * Here we store all archives of this asset manager.
	 */
	private transient Set<ZipFile> archives;

	/**
	 * @return a set with all open zip files.
	 * @throws Exception
	 *             If an exception occurs.
	 */
	private Set<ZipFile> openArchives() throws Exception {

		Set<ZipFile> tmp = new LinkedHashSet<ZipFile>();

		// Iterate over all asset bundles
		for (AssetBundle assetBundle : assetBundles.values()) {

			// Open new zip file
			tmp.add(new ZipFile(assetBundle.getArchive()));
		}
		return tmp;
	}

	/*
	 * When loading the asset manager from stream all archive should be opened
	 * here.
	 */
	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {

		// Restore all vars
		in.defaultReadObject();

		try {
			// Try to open the zip files
			archives = openArchives();
		} catch (Exception e) {
			throw new IOException("Failed to load asset", e);
		}
	}

	/**
	 * Creates a new asset manager using the given archive file. All included
	 * asset bundles will be loaded automatically.
	 * 
	 * @param archive
	 *            The archive file you want to load.
	 * @throws Exception
	 *             If an exception occurs.
	 */
	public AssetManager(File archive) throws Exception {

		if (archive == null) {
			throw new NullPointerException("archive");
		}

		// Used to store the archive files during collection
		Set<File> destination = new LinkedHashSet<File>();

		// Collect all archives
		AssetBundle.collectArchives(archive, destination);

		// Tmp asset bundles
		Map<File, AssetBundle> tmpAssetBundles = new LinkedHashMap<File, AssetBundle>();

		// Tmp file hashes
		Map<File, String> tmpAssetBundleHashes = new LinkedHashMap<File, String>();

		// Iterate over the destinations
		for (File file : destination) {

			// Create a new asset bundle
			AssetBundle assetBundle = new AssetBundle(file);

			// Add a new asset bundle for each file
			tmpAssetBundles.put(file, assetBundle);

			// Store archive hash
			tmpAssetBundleHashes.put(file, assetBundle.getArchiveHash());
		}

		// Save if everything is ok
		assetBundles = Collections.unmodifiableMap(tmpAssetBundles);
		assetBundleHashes = Collections.unmodifiableMap(tmpAssetBundleHashes);

		// Open the archives
		archives = openArchives();
	}

	/**
	 * @return all asset bundles of this asset manager.
	 */
	public Map<File, AssetBundle> getAssetBundles() {
		return assetBundles;
	}

	/**
	 * @return all asset bundle hashes.
	 */
	public Map<File, String> getAssetBundleHashes() {
		return assetBundleHashes;
	}

	/**
	 * Loads the image.
	 * 
	 * @param assetPath
	 *            The asset path of the image.
	 * @param optimized
	 *            If true the loaded image will be optimized.
	 * @return an asset containing the image.
	 * @throws Exception
	 *             If an exception occurs.
	 */
	public Asset<BufferedImage> loadImage(String assetPath, boolean optimized)
			throws Exception {
		return loadAsset(assetPath, optimized ? OPTIMIZED_IMAGE_LOADER
				: IMAGE_LOADER);
	}

	/**
	 * Loads the stream.
	 * 
	 * @param assetPath
	 *            The asset path of the stream.
	 * @return an asset containing the stream.
	 * @throws Exception
	 *             If an exception occurs.
	 */
	public Asset<InputStream> loadStream(String assetPath) throws Exception {
		return loadAsset(assetPath, STREAM_LOADER);
	}

	/**
	 * Loads an asset using the given asset loader.
	 * 
	 * @param assetPath
	 *            The asset path of the asset.
	 * @param assetLoader
	 *            The asset loader of the asset.
	 * @return an asset.
	 * @throws Exception
	 *             If an exception occurs.
	 */
	public <T> Asset<T> loadAsset(String assetPath, AssetLoader<T> assetLoader)
			throws Exception {
		return new Asset<T>(this, assetPath, assetLoader);
	}

	/**
	 * Opens an input stream to an asset.
	 * 
	 * @param assetPath
	 *            The asset path.
	 * @return the opened input stream.
	 * @throws Exception
	 *             If an exception occurs.
	 */
	public InputStream open(String assetPath) throws Exception {
		for (ZipFile source : archives) {
			ZipEntry entry = source.getEntry(assetPath);
			if (entry != null) {
				return source.getInputStream(entry);
			}
		}
		throw new IOException("Zip path \"" + assetPath + "\" does not exist");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((assetBundles == null) ? 0 : assetBundles.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AssetManager)) {
			return false;
		}
		AssetManager other = (AssetManager) obj;
		if (assetBundles == null) {
			if (other.assetBundles != null) {
				return false;
			}
		} else if (!assetBundles.equals(other.assetBundles)) {
			return false;
		}
		return true;
	}
}
