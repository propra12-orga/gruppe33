package propra2012.gruppe33.graphics.rendering.util;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import propra2012.gruppe33.graphics.rendering.scenegraph.grid.GridLoader;
import propra2012.gruppe33.graphics.sprite.Sprite;

/**
 * This class simplifies the handling of resources. Let it be images, maps,
 * sounds or something else. All useable assets are located in THIS package.
 * 
 * @author Christopher Probst
 */
public final class AssetManager {

	public static List<URL> readDependencies(File zipFile) throws Exception {
		return readDependencies(zipFile, null);
	}

	private static List<URL> readDependencies(File zipFile,
			Set<String> alreadyIncluded) throws Exception {

		if (zipFile == null) {
			throw new NullPointerException("zipFile");
		} else if (!zipFile.exists()) {
			throw new FileNotFoundException("Dependency \""
					+ zipFile.getAbsolutePath() + "\" cannot be found.");
		} else if (alreadyIncluded == null) {
			alreadyIncluded = new HashSet<String>();
		}

		// Work with the absolute path
		zipFile = zipFile.getAbsoluteFile();

		// This list will be returned afterwards
		List<URL> zipFiles = new LinkedList<URL>();

		// Add local url
		zipFiles.add(zipFile.toURI().toURL());

		/*
		 * IMPORTANT: Check the zip file path. If the path is already included
		 * we can stop here. Otherwise this could lead to infinite loops.
		 */
		if (alreadyIncluded.add(zipFile.getAbsolutePath())) {

			// Create a new scene file
			ZipFile sceneFile = new ZipFile(zipFile);

			try {
				// Try to find entry
				ZipEntry entry = sceneFile.getEntry("dependencies.txt");

				if (entry != null) {
					// Here we store dependencies
					List<String> dependencies = new LinkedList<String>();

					// Open jar stream
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(
									sceneFile.getInputStream(entry)));

					// Read all dependencies
					try {
						String line;
						while ((line = reader.readLine()) != null) {
							dependencies.add(line);
						}
					} finally {
						reader.close();
					}

					// Crawl all dependencies
					for (String dependency : dependencies) {

						// Merge the dependencies
						zipFiles.addAll(readDependencies(
								new File(zipFile.getParent(), dependency),
								alreadyIncluded));
					}
				}
			} finally {
				sceneFile.close();
			}
		}

		return zipFiles;
	}

	// The class loader where the resources are
	private final ClassLoader classLoader;

	public AssetManager(File zipFile) throws Exception {
		this(new URLClassLoader(readDependencies(zipFile).toArray(new URL[0])));
	}

	/**
	 * Creates a new asset manger using the given class loader.
	 * 
	 * @param classLoader
	 *            The class loader.
	 */
	public AssetManager(ClassLoader classLoader) {
		if (classLoader == null) {
			throw new NullPointerException("classLoader");
		}
		this.classLoader = classLoader;
	}

	/**
	 * @return the class loader of this asset manager.
	 */
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	/**
	 * Loads grid data from the asset path.
	 * 
	 * @param jarPath
	 *            The grid data path inside the jar file.
	 * @return the loaded grid data.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public char[][] loadGridData(String jarPath) throws IOException {
		return GridLoader.load(open(jarPath));
	}

	/**
	 * Loads a sprite from the asset path.
	 * 
	 * @param jarPath
	 *            The sprite path inside the jar file.
	 * @param rasterX
	 *            The columns of the sprite.
	 * @param rasterY
	 *            The rows of the sprite.
	 * @return the loaded sprite.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public Sprite loadSprite(String jarPath, int rasterX, int rasterY)
			throws IOException {
		// Load an image an create a new sprite
		return new Sprite(loadImage(jarPath), rasterX, rasterY);
	}

	/**
	 * Loads an image from the asset path.
	 * 
	 * @param jarPath
	 *            The image path inside the jar file.
	 * @return the loaded image.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public BufferedImage loadImage(String jarPath) throws IOException {
		return ImageIO.read(open(jarPath));
	}

	/**
	 * Creates an url for the resource.
	 * 
	 * @param jarPath
	 *            The path inside the jar file.
	 * @return the url.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public URL getURL(String jarPath) throws IOException {
		return classLoader.getResource(jarPath);
	}

	/**
	 * Opens an input stream to an asset.
	 * 
	 * @param jarPath
	 *            The path inside the jar file.
	 * @return the opened input stream.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public InputStream open(String jarPath) throws IOException {
		// Open the resource
		return classLoader.getResourceAsStream(jarPath);
	}
}
