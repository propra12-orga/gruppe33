package propra2012.gruppe33.resources.assets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.security.MessageDigest;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * This class represents a bundle of assets which are located in a single
 * archive file. An asset bundle can define dependencies to other asset bundles
 * (other archives) by including a specific file. Please check the
 * implementation to see which form this file must have.
 * 
 * @author Christopher Probst
 * @see Asset
 * @see AssetLoader
 * @see AssetManager
 */
public final class AssetBundle implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This implementation interpretates "include.txt" as an include file.
	 */
	public static final String INCLUDE_FILE_NAME = "include.txt";

	/*
	 * Used for faster byte to hex conversion.
	 */
	private static final String HEX_RANGE = "0123456789ABCDEF";

	/**
	 * This method takes an archive and a set of files and collects all
	 * dependencies of this archive recursively. Duplicate dependencies are
	 * ignored sinec a set is used to store the archive files.
	 * 
	 * @param archive
	 *            The archive file you want to check.
	 * @param destination
	 *            The set where all dependencies will be stored.
	 * @throws IOException
	 *             If an I/O exception occurs.
	 */
	public static void collectArchives(File archive, Set<File> destination)
			throws IOException {

		if (archive == null) {
			throw new NullPointerException("archive");
		} else if (archive.isAbsolute()) {
			throw new IllegalArgumentException("Please use a relative path");
		} else if (!archive.exists()) {
			throw new FileNotFoundException("Included archive \""
					+ archive.getAbsolutePath() + "\" cannot be found.");
		} else if (destination == null) {
			throw new NullPointerException("destination");
		}

		/*
		 * IMPORTANT: Check the zip file path. If the path is already included
		 * we can stop here. Otherwise this could lead to infinite loops.
		 */
		if (destination.add(archive)) {

			// Create a new zip archive
			ZipFile zipArchive = new ZipFile(archive);

			try {
				// Try to find entry
				ZipEntry entry = zipArchive.getEntry(INCLUDE_FILE_NAME);

				/*
				 * Does this file defines dependencies ?
				 */
				if (entry != null) {
					// Here we store dependencies
					List<String> dependencies = new LinkedList<String>();

					// Open stream
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(
									zipArchive.getInputStream(entry)));

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

						// Merge the archives
						collectArchives(new File(archive.getParent(),
								dependency), destination);
					}
				}
			} finally {
				zipArchive.close();
			}
		}
	}

	/**
	 * Simple method to convert binary data to hex.
	 * 
	 * @param data
	 *            The binary data you want to represent as hex.
	 * @return the hex string.
	 */
	public static String toHex(byte[] data) {
		if (data == null) {
			throw new NullPointerException("data");
		}

		// Use a fast string builder for this purpose
		StringBuilder stringBuilder = new StringBuilder(2 * data.length);

		// Iterate over all bytes
		for (byte b : data) {
			// Bit shifting and &-operator do all the magic here...
			stringBuilder.append(HEX_RANGE.charAt((b & 0xF0) >> 4)).append(
					HEX_RANGE.charAt((b & 0x0F)));
		}

		// Represent the final string
		return stringBuilder.toString();
	}

	/**
	 * Calculates the string hash for a given input stream.
	 * 
	 * @param inputStream
	 *            The input stream.
	 * @return the hex string.
	 * @throws Exception
	 *             If an exception occurs.
	 */
	public static String calcHexHash(InputStream inputStream) throws Exception {
		return toHex(calcHash(inputStream));
	}

	/**
	 * Calculates the binary hash for a given input stream.
	 * 
	 * @param inputStream
	 *            The input stream.
	 * @return the binary hash.
	 * @throws Exception
	 *             If an exception occurs.
	 */
	public static byte[] calcHash(InputStream inputStream) throws Exception {
		if (inputStream == null) {
			throw new NullPointerException("inputStream");
		}

		try {
			// Use SHA-1 for hashing
			MessageDigest digest = MessageDigest.getInstance("SHA-1");

			// 0xFFFF should increase calculation
			byte[] buffer = new byte[0xFFFF];

			// Tmp
			int read;

			// Read & update as long there are remaining bytes
			while ((read = inputStream.read(buffer)) != -1) {
				digest.update(buffer, 0, read);
			}

			// Return the hash
			return digest.digest();
		} finally {
			inputStream.close();
		}
	}

	/**
	 * Used for {@link AssetBundle#validate()}.
	 * 
	 * @author Christopher Probst
	 */
	public enum ValidationResult {
		ArchiveDoesNotExist, InvalidArchiveHash, ArchiveOk
	}

	/*
	 * This is the archive which represents this asset bundle.
	 */
	private final File archive;

	/*
	 * This is the archive hash.
	 */
	private final String archiveHash;

	/**
	 * Creates a new asset bundle using the give archive file. This method will
	 * not validate the asset bundle.
	 * 
	 * @param archive
	 *            The archive file.
	 * @throws Exception
	 *             If an exception occurs.
	 */
	AssetBundle(File archive) throws Exception {

		// Do not allow absolute paths
		if (archive.isAbsolute()) {
			throw new IllegalArgumentException("Archive path must be absolute");
		}

		// Calc the archive hash
		archiveHash = calcHexHash(new FileInputStream(archive));

		// Save the archive
		this.archive = archive;
	}

	/**
	 * Validates this asset bundle which means that it will check the existence
	 * and integrity of the archive file.
	 * 
	 * @return the validation result.
	 * @throws Exception
	 *             If an exception occurs.
	 * @see {@link ValidationResult}
	 */
	public ValidationResult validate() throws Exception {

		try {
			// If the hash equals the calculated hash the archive is ok
			return calcHexHash(new FileInputStream(archive))
					.equals(archiveHash) ? ValidationResult.ArchiveOk
					: ValidationResult.InvalidArchiveHash;

		} catch (FileNotFoundException e) {

			// The archive obviouly does not exist
			return ValidationResult.ArchiveDoesNotExist;
		}
	}

	/**
	 * @return the archive hash string.
	 */
	public String getArchiveHash() {
		return archiveHash;
	}

	/**
	 * @return the archive file.
	 */
	public File getArchive() {
		return archive;
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
		result = prime * result + ((archive == null) ? 0 : archive.hashCode());
		result = prime * result
				+ ((archiveHash == null) ? 0 : archiveHash.hashCode());
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
		if (!(obj instanceof AssetBundle)) {
			return false;
		}
		AssetBundle other = (AssetBundle) obj;
		if (archive == null) {
			if (other.archive != null) {
				return false;
			}
		} else if (!archive.equals(other.archive)) {
			return false;
		}
		if (archiveHash == null) {
			if (other.archiveHash != null) {
				return false;
			}
		} else if (!archiveHash.equals(other.archiveHash)) {
			return false;
		}
		return true;
	}
}
