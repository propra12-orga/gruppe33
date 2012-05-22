package propra2012.gruppe33.bomberman.graphics.rendering.scenegraph.grid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

/**
 * Diese Klasse laedt aus einer Textdatei Zeilenweise die Karte aus. Kann
 * IOExceptions werfen.
 * 
 * @author Malte Schmidt
 */
public final class GridLoader {

	/**
	 * Berechnet die Map aus der Textdatei und gibt ein Array mit dem Inhalt der
	 * Map zurueck. Wirft IOException, wenn die Karte nicht geladen werden kann,
	 * z.B. wenn die File nicht geladen werden kann.
	 * 
	 * @param input
	 *            Die zu ladene Map.
	 * @return Gibt ein Char-array zurueck mit den geladenen Werten der Map.
	 * @throws IOException
	 *             Wenn zu ladene Karte leer oder nicht kompatibel (z.B. Wenn
	 *             die Strings unterschiedlich lang sind.
	 */
	public static char[][] load(InputStream input) throws IOException {
		if (input == null) {
			throw new NullPointerException("input");
		}

		// Eine Zeile der Karte.
		String line;

		// Die map
		char[][] map;

		// Liste in der Zwischengespeichert wird
		List<String> cache = new LinkedList<String>();

		// Laden des files
		BufferedReader br = new BufferedReader(new InputStreamReader(input));

		try {
			// Lies Zeile fuer Zeile
			while ((line = br.readLine()) != null) {
				cache.add(line);
			}

			// Nichts zu lesen
			if (cache.isEmpty()) {
				throw new IOException("Empty grid");
			}
		} finally {
			// Schliesse den Stream
			br.close();
		}

		// Neues array erstellen
		map = new char[cache.size()][];

		// Alles in das char array kopieren
		int i = 0, len = -1;
		for (String row : cache) {
			if (len == -1) {
				len = row.length();
			} else if (len != row.length()) {
				throw new IOException("Zeilen nicht alle gleich lang!");
			}

			map[i++] = row.toCharArray();
		}
		return map;
	}

	// Muss nicht erstellbar sein.
	private GridLoader() {
	}
	
	private boolean nextTo(char[][] map, int x, int y, char typ) {
		if (map[y-1][x]==typ) {return true;}
		else if (map[y][x+1]==typ) {return true;}
		else if (map[y+1][x]==typ) {return true;}
		else if (map[y][x-1]==typ) {return true;}
		else {return false;}
	}
}
