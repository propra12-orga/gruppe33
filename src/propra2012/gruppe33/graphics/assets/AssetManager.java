package propra2012.gruppe33.graphics.assets;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import propra2012.gruppe33.graphics.assets.AssetBundle.ValidationResult;
import propra2012.gruppe33.graphics.rendering.scenegraph.grid.GridLoader;

/**
 * This class simplifies the handling of resources. Let it be images, maps,
 * sounds or something else.
 * 
 * @author Christopher Probst
 */
public final class AssetManager implements Serializable {

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
	 * Here we store all zip files of this asset manager.
	 */
	private transient Set<ZipFile> openZipFiles = null;

	/**
	 * @return a set with all open zip files. This will be done once. Please
	 *         validate the asset bundles before opening them, though this
	 *         checked here, too. But you could run into trouble detecting the
	 *         missing asset bundle.
	 * @throws Exception
	 *             If an exception occurs.
	 */
	private Set<ZipFile> getOpenZipFiles() throws Exception {
		if (openZipFiles == null) {
			openZipFiles = new LinkedHashSet<ZipFile>();
			for (AssetBundle assetBundle : assetBundles.values()) {
				if (assetBundle.validate() == ValidationResult.ArchiveOk) {
					openZipFiles.add(new ZipFile(assetBundle.getArchive()));
				}
			}
		}
		return openZipFiles;
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
		for (ZipFile source : getOpenZipFiles()) {
			ZipEntry entry = source.getEntry(assetPath);
			if (entry != null) {
				return source.getInputStream(entry);
			}
		}
		throw new IOException("Zip path does not exist");
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
	}

	/**
	 * @return all assset bundles of this asset manager.
	 */
	public Map<File, AssetBundle> getAssetBundles() {
		return assetBundles;
	}

	public Asset<char[][]> loadGridData(String assetPath) throws Exception {
		return new Asset<char[][]>(this, assetPath, GRID_LOADER);
	}

	public Asset<BufferedImage> loadImage(String assetPath) throws Exception {
		return new Asset<BufferedImage>(this, assetPath, IMAGE_LOADER);
	}

	public Asset<InputStream> loadStream(String assetPath) {
		return new Asset<InputStream>(this, assetPath, STREAM_LOADER);
	}
}
