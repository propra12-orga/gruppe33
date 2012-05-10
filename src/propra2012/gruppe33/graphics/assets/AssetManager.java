package propra2012.gruppe33.graphics.assets;

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

import propra2012.gruppe33.graphics.rendering.scenegraph.grid.GridLoader;

/**
 * This class simplifies the handling of resources. Let it be images, maps,
 * sounds or something else. An asset manager basically creates assets which
 * represent data from an archive.
 * 
 * An asset manager and all its components are serializable to heavily simplify
 * the networking part.
 * 
 * @author Christopher Probst
 * @see Asset
 * @see AssetBundle
 * @see AssetLoader
 */
public final class AssetManager implements Serializable {

	/*
	 * The grid loader.
	 */
	private static final AssetLoader<char[][]> GRID_LOADER = new AssetLoader<char[][]>() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public char[][] loadAsset(AssetManager assetManager, String assetPath)
				throws Exception {
			return GridLoader.load(assetManager.open(assetPath));
		}
	};

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

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * Here we store all asset bundles.
	 */
	private final Map<File, AssetBundle> assetBundles;

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

	/**
	 * Opens an input stream to an asset.
	 * 
	 * @param assetPath
	 *            The asset path.
	 * @return the opened input stream.
	 * @throws Exception
	 *             If an exception occurs.
	 */
	private InputStream open(String assetPath) throws Exception {
		for (ZipFile source : archives) {
			ZipEntry entry = source.getEntry(assetPath);
			if (entry != null) {
				return source.getInputStream(entry);
			}
		}
		throw new IOException("Zip path does not exist");
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

		// Tmp map
		Map<File, AssetBundle> tmpAssetBundles = new LinkedHashMap<File, AssetBundle>();

		// Iterate over the destinations
		for (File file : destination) {

			// Add a new asset bundle for each file
			tmpAssetBundles.put(file, new AssetBundle(file));
		}

		// Save if everything is ok
		assetBundles = Collections.unmodifiableMap(tmpAssetBundles);

		// Open the archives
		archives = openArchives();
	}

	/**
	 * @return all assset bundles of this asset manager.
	 */
	public Map<File, AssetBundle> getAssetBundles() {
		return assetBundles;
	}

	/**
	 * Loads the grid.
	 * 
	 * @param assetPath
	 *            The asset path of the grid.
	 * @return an asset containing the grid.
	 * @throws Exception
	 *             If an exception occurs.
	 */
	public Asset<char[][]> loadGrid(String assetPath) throws Exception {
		return new Asset<char[][]>(this, assetPath, GRID_LOADER);
	}

	/**
	 * Loads the image.
	 * 
	 * @param assetPath
	 *            The asset path of the image.
	 * @return an asset containing the image.
	 * @throws Exception
	 *             If an exception occurs.
	 */
	public Asset<BufferedImage> loadImage(String assetPath) throws Exception {
		return new Asset<BufferedImage>(this, assetPath, IMAGE_LOADER);
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
		return new Asset<InputStream>(this, assetPath, STREAM_LOADER);
	}
}
