package propra2012.gruppe33.graphics.assets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 
 * @author Christopher Probst
 * 
 */
public final class AssetBundle implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String INCLUDE_FILE_NAME = "include.txt";

	public static final String HEX_RANGE = "0123456789ABCDEF";

	public static void collectArchives(File archive, Set<File> destination)
			throws IOException {

		if (archive == null) {
			throw new NullPointerException("archive");
		} else if (archive.isAbsolute()) {
			throw new IllegalArgumentException("Please use a relative path");
		} else if (!archive.exists()) {
			throw new FileNotFoundException("Dependency \""
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

	public static String toHex(byte[] data) {
		if (data == null) {
			throw new NullPointerException("data");
		}
		StringBuilder stringBuilder = new StringBuilder(2 * data.length);

		for (byte b : data) {
			stringBuilder.append(HEX_RANGE.charAt((b & 0xF0) >> 4)).append(
					HEX_RANGE.charAt((b & 0x0F)));
		}
		return stringBuilder.toString();
	}

	public static String calcHexHash(InputStream inputStream)
			throws NoSuchAlgorithmException, DigestException, IOException {
		return toHex(calcHash(inputStream));
	}

	public static byte[] calcHash(InputStream inputStream)
			throws NoSuchAlgorithmException, IOException, DigestException {
		if (inputStream == null) {
			throw new NullPointerException("inputStream");
		}

		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");

			byte[] buffer = new byte[0xFFFF];
			int read;

			while ((read = inputStream.read(buffer)) != -1) {
				digest.update(buffer, 0, read);
			}

			// Return the hash
			return digest.digest();
		} finally {
			inputStream.close();
		}
	}

	/*
	 * This is the archive which represents this asset bundle.
	 */
	private final File archive;

	/*
	 * This is the archive hash.
	 */
	private final String archiveHash;

	AssetBundle(File archive) throws NoSuchAlgorithmException, DigestException,
			IOException {

		// Do not allow absolute paths
		if (archive.isAbsolute()) {
			throw new IllegalArgumentException("Archive path must be absolute");
		}

		// Calc the archive hash
		String calculatedArchiveHash = calcHexHash(new FileInputStream(archive));

		// Save the archive
		this.archive = archive;

		// Save the calculated archive hash
		archiveHash = calculatedArchiveHash;
	}

	public enum ValidationResult {
		ArchiveDoesNotExist, InvalidArchiveHash, ArchiveOk
	}

	public ValidationResult validate() throws NoSuchAlgorithmException,
			DigestException, IOException {

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

	public String getArchiveHash() {
		return archiveHash;
	}

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
